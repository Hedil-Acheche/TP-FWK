package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
import net.thevpc.gaming.atom.model.Player;
import net.thevpc.gaming.atom.model.Sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TCPMainServerDAO implements MainServerDAO {
    MainServerDAOListener l;
    AppConfig c;

    @Override
    public void start(MainServerDAOListener l, AppConfig c) {
        this.l = l;
        this.c = c;
        new Thread(() -> {
            try {
                startServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    void startServer() throws IOException {
        ServerSocket ss = new ServerSocket(c.getServerPort());
        while (true) {
            Socket s = ss.accept();
            new Thread(() -> {
                try {
                    processClient(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    void processClient(Socket s) throws IOException {
        DataInputStream inputStream = new DataInputStream(s.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(s.getOutputStream());
        int c = inputStream.readInt();
        if (c != ProtocolConstants.CONNECT) {
            s.close();
            return;
        }
        String n = inputStream.readUTF();
        StartGameInfo sgi = l.onReceivePlayerJoined(n);
        ClientSession cs = new ClientSession();
        cs.playerId = sgi.getPlayerId();
        cs.dataInputStream = inputStream;
        cs.dataOutputStream = outputStream;
        cs.socket = s;
        playerToSocketMap.put(sgi.getPlayerId(), cs);
        outputStream.writeInt(ProtocolConstants.OK);
        outputStream.writeInt(cs.playerId);
        outputStream.writeInt(sgi.getMaze().length);
        outputStream.writeInt(sgi.getMaze()[0].length);
        for (int i = 0; i < sgi.getMaze().length; i++) {
            for (int j = 0; j < sgi.getMaze()[0].length; j++) {
                outputStream.writeInt(sgi.getMaze()[i][j]);
            }
        }
        while (true) {
            try {
                int cmd = inputStream.readInt();
                switch (cmd) {
                    case ProtocolConstants.LEFT: {
                        l.onReceiveMoveLeft(cs.playerId);
                        break;
                    }
                    case ProtocolConstants.RIGHT: {
                        l.onReceiveMoveRight(cs.playerId);
                        break;
                    }
                    case ProtocolConstants.UP: {
                        l.onReceiveMoveUp(cs.playerId);
                        break;
                    }
                    case ProtocolConstants.DOWN: {
                        l.onReceiveMoveDown(cs.playerId);
                        break;
                    }
                    case ProtocolConstants.FIRE: {
                        l.onReceiveReleaseBomb(cs.playerId);
                        break;
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    void writePlayer(Player player, DataOutputStream out) throws IOException {
        out.writeInt(player.getId());
        out.writeUTF(player.getName() == null ? "" : player.getName());
        Map<String, Object> properties = player.getProperties();
        if (properties == null) {
            out.writeInt(0);
        } else {
            out.writeInt(properties.size());
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeUTF(entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
    }

    void writeSprite(Sprite sprite, DataOutputStream out) throws IOException {
        out.writeInt(sprite.getId());
        out.writeUTF(sprite.getKind() == null ? "" : sprite.getKind());
        out.writeUTF(sprite.getName() == null ? "" : sprite.getName());
        out.writeDouble(sprite.getLocation().getX());
        out.writeDouble(sprite.getLocation().getY());
        out.writeDouble(sprite.getDirection());
        out.writeInt(sprite.getPlayerId());
        out.writeInt(sprite.getMovementStyle());
        Map<String, Object> properties = sprite.getProperties();
        if (properties == null) {
            out.writeInt(0);
        } else {
            out.writeInt(properties.size());
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeUTF(entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
    }

    @Override
    public void sendModelChanged(DynamicGameModel dynamicGameModel) {
        synchronized (playerToSocketMap) {
            for (ClientSession session : playerToSocketMap.values()) {
                try {
                    DataOutputStream out = session.dataOutputStream;
                    out.writeLong(dynamicGameModel.getFrame());
                    out.writeInt(dynamicGameModel.getPlayers().size());
                    for (Player player : dynamicGameModel.getPlayers()) {
                        writePlayer(player, out);
                    }
                    out.writeInt(dynamicGameModel.getSprites().size());
                    for (Sprite sprite : dynamicGameModel.getSprites()) {
                        writeSprite(sprite, out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientSession {
        public int playerId;
        public Socket socket;
        public DataInputStream dataInputStream;
        public DataOutputStream dataOutputStream;
    }

    private Map<Integer, ClientSession> playerToSocketMap = new HashMap<>();

}
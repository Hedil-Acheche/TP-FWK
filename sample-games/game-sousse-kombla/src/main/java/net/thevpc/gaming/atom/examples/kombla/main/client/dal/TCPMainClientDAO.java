package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
import net.thevpc.gaming.atom.model.DefaultPlayer;
import net.thevpc.gaming.atom.model.DefaultSprite;
import net.thevpc.gaming.atom.model.Player;
import net.thevpc.gaming.atom.model.Sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPMainClientDAO implements MainClientDAO {
    MainClientDAOListener listener;
    AppConfig config;
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    @Override
    public void start(MainClientDAOListener listener, AppConfig config) {
        this.listener = listener;
        this.config = config;
    }

    @Override
    public StartGameInfo connect() {
        try {
            System.out.println("Attempting to connect to " + config.getServerAddress() + ":" + config.getServerPort());
            socket = new Socket(config.getServerAddress(), config.getServerPort());
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeInt(ProtocolConstants.CONNECT);
            outputStream.writeUTF(config.getPlayerName());
            StartGameInfo sgi = readStartGameInfo();
            onLoopReceiveModelChanged();
            System.out.println("Successfully connected to server!");
            return sgi;
        } catch (IOException e) {
            String errorMsg = "Failed to connect to server at " + config.getServerAddress() + ":"
                    + config.getServerPort() +
                    "\n\nPossible causes:" +
                    "\n1. Server is not running - Start a server first (Host Game)" +
                    "\n2. Wrong server address - Check the server address field" +
                    "\n3. Wrong port number - Check the port number field" +
                    "\n4. Firewall blocking connection" +
                    "\n\nError details: " + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    void onLoopReceiveModelChanged() {
        new Thread(() -> {
            while (true) {
                try {
                    DynamicGameModel model = readModelChanged();
                    listener.onModelChanged(model);
                } catch (IOException e) {
                    break;
                }
            }
        }).start();
    }

    StartGameInfo readStartGameInfo() throws IOException {
        int status = inputStream.readInt();
        if (status != ProtocolConstants.OK) {
            throw new RuntimeException("Connection failed");
        }
        int playerId = inputStream.readInt();
        int rows = inputStream.readInt();
        int cols = inputStream.readInt();
        int[][] maze = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = inputStream.readInt();
            }
        }
        return new StartGameInfo(playerId, maze);
    }

    DynamicGameModel readModelChanged() throws IOException {
        long frame = inputStream.readLong();
        int playersCount = inputStream.readInt();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) {
            players.add(readPlayer());
        }
        int spritesCount = inputStream.readInt();
        List<Sprite> sprites = new ArrayList<>();
        for (int i = 0; i < spritesCount; i++) {
            sprites.add(readSprite());
        }
        return new DynamicGameModel(frame, sprites, players);
    }

    Player readPlayer() throws IOException {
        int id = inputStream.readInt();
        String name = inputStream.readUTF();
        DefaultPlayer player = new DefaultPlayer();
        player.setId(id);
        player.setName(name);
        int propsCount = inputStream.readInt();
        for (int i = 0; i < propsCount; i++) {
            String key = inputStream.readUTF();
            String value = inputStream.readUTF();
            player.setProperty(key, value);
        }
        return player;
    }

    Sprite readSprite() throws IOException {
        int id = inputStream.readInt();
        String kind = inputStream.readUTF();
        String name = inputStream.readUTF();
        double x = inputStream.readDouble();
        double y = inputStream.readDouble();
        double direction = inputStream.readDouble();
        int playerId = inputStream.readInt();
        int movementStyle = inputStream.readInt();
        DefaultSprite sprite = new DefaultSprite();
        sprite.setId(id);
        sprite.setKind(kind);
        sprite.setName(name);
        sprite.setLocation(x, y);
        sprite.setDirection(direction);
        sprite.setPlayerId(playerId);
        sprite.setMovementStyle(movementStyle);
        int propsCount = inputStream.readInt();
        for (int i = 0; i < propsCount; i++) {
            String key = inputStream.readUTF();
            String value = inputStream.readUTF();
            sprite.setProperty(key, value);
        }
        return sprite;
    }

    @Override
    public void sendMoveLeft() {
        try {
            outputStream.writeInt(ProtocolConstants.LEFT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMoveRight() {
        try {
            outputStream.writeInt(ProtocolConstants.RIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMoveUp() {
        try {
            outputStream.writeInt(ProtocolConstants.UP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMoveDown() {
        try {
            outputStream.writeInt(ProtocolConstants.DOWN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFire() {
        try {
            outputStream.writeInt(ProtocolConstants.FIRE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package net.thevpc.gaming.helloworld;

import net.thevpc.gaming.atom.engine.SpriteFilter;
import net.thevpc.gaming.atom.model.Orientation;
import net.thevpc.gaming.atom.model.Sprite;
import net.thevpc.gaming.atom.presentation.*;
import net.thevpc.gaming.atom.engine.maintasks.MoveToPointSpriteMainTask;

/**
 * Created with IntelliJ IDEA.
 * User: vpc
 * Date: 8/15/13
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpriteController2 extends DefaultSceneController {
    private SpriteFilter sprite;
    private KeyCodeSet up = KeyCodeSet.of(KeyCode.E);
    private KeyCodeSet down = KeyCodeSet.of(KeyCode.D);
    private KeyCodeSet left = KeyCodeSet.of(KeyCode.S);
    private KeyCodeSet right = KeyCodeSet.of(KeyCode.F);
    /**
     * if true use cross mode (4 directions) else use star mode (8 directions)
     */
    private boolean crossMode = false;

    public SpriteController2(SpriteFilter sprite) {
        this.sprite = sprite;
        if(sprite==null){
            throw new NullPointerException("null sprite condition");
        }
    }

    public boolean isCrossMode() {
        return crossMode;
    }

    public SpriteController2 setCrossMode(boolean crossMode) {
        this.crossMode = crossMode;
        return this;
    }

    @Override
    public void keyChanged(SceneKeyEvent e) {
        for (Sprite sp : e.getScene().getSceneEngine().findSprites(sprite)) {
            keyChanged(sp, e);
        }
    }

    public KeyCodeSet getUp() {
        return up;
    }

    public SpriteController2 setUp(KeyCode... up) {
        this.up = KeyCodeSet.of(up);
        if (this.up.isEmpty()) {
            this.up = KeyCodeSet.of(KeyCode.E);
        }
        return this;
    }

    public KeyCodeSet getDown() {
        return down;
    }

    public SpriteController2 setDown(KeyCode... down) {
        this.down = KeyCodeSet.of(down);
        if (this.down.isEmpty()) {
            this.down = KeyCodeSet.of(KeyCode.D);
        }
        return this;
    }

    public SpriteController2 setArrowKeysLayout() {
        setUp(KeyCode.UP);
        setDown(KeyCode.DOWN);
        setLeft(KeyCode.LEFT);
        setRight(KeyCode.RIGHT);
        return this;
    }

    public SpriteController2 setESDFLayout() {
        setUp(KeyCode.E);
        setDown(KeyCode.D);
        setLeft(KeyCode.S);
        setRight(KeyCode.F);
        return this;
    }

    public SpriteController2 setIJKLLayout() {
        setUp(KeyCode.I);
        setDown(KeyCode.K);
        setLeft(KeyCode.J);
        setRight(KeyCode.L);
        return this;
    }

    public KeyCodeSet getLeft() {
        return left;
    }

    public SpriteController2 setLeft(KeyCode... left) {
        this.left = KeyCodeSet.of(left);
        if (this.left.isEmpty()) {
            this.left = KeyCodeSet.of(KeyCode.S);
        }

        return this;
    }

    public KeyCodeSet getRight() {
        return right;
    }

    public SpriteController2 setRight(KeyCode... right) {
        this.right = KeyCodeSet.of(right);
        if (this.right.isEmpty()) {
            this.right = KeyCodeSet.of(KeyCode.F);
        }
        return this;
    }
   /* @Override
    public void mouseClicked(SceneMouseEvent e) {

        for (Sprite sp : e.getScene().getSceneEngine().findSprites(sprite)) {

            sp.addTask(new MoveToPointSpriteMainTask(e.getX(), e.getY()));
        }
    }
*/
    public void keyChanged(Sprite sprite, SceneKeyEvent e) {
        KeyCodeSet keyCodes = e.getKeyCodes();
        Orientation or = null;
        if (keyCodes.equals(getUp())) {
            or = Orientation.NORTH;
        } else if (keyCodes.equals(getDown())) {
            or = Orientation.SOUTH;
        } else if (keyCodes.equals(getLeft())) {
            or = Orientation.WEST;
        } else if (keyCodes.equals(getRight())) {
            or = Orientation.EAST;
        } else {
            if (!isCrossMode()) {
                if (keyCodes.equals(getUp().plus(getLeft()))) {
                    or = Orientation.NORTH_WEST;
                } else if (keyCodes.equals(getUp().plus(getRight()))) {
                    or = Orientation.NORTH_EAST;
                } else if (keyCodes.equals(getDown().plus(getLeft()))) {
                    or = Orientation.SOUTH_WEST;
                } else if (keyCodes.equals(getDown().plus(getRight()))) {
                    or = Orientation.SOUTH_EAST;
                }
            }
        }
        if (or != null) {
            sprite.setDirection(or);
        }
    }
}

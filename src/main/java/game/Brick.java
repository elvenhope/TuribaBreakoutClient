package game;

import Packets.DestroyedBrickPacket;
import Packets.SinglePaddlePacket;

import javax.swing.ImageIcon;

public class Brick extends Sprite{
    private boolean destroyed;
    private int id;
    public Brick(int x, int y, int id) {
        initBrick(x, y, id);
    }

    private void initBrick(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;

        destroyed = false;
        loadImage();
        getImageDimensions();
    }

    private void loadImage() {
        var icon = new ImageIcon(getClass().getResource("/Images/block1.png"));
        image = icon.getImage();
    }

    boolean isDestroyed() {
        return destroyed;
    }

    void setDestroyed(boolean value) {
        this.destroyed = value;
        Board.ball.UpdateScore(1);
    }

    void UpdateBrick() {
        DestroyedBrickPacket packet = new DestroyedBrickPacket();
        packet.id = id;
        Breakout.client.sendObject(packet);
        System.out.println("BRICK ID:" + id);
    }
}

package game;

import Packets.ScoreboardPacket;
import Packets.SinglePaddlePacket;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Paddle extends Sprite{
    private int dx;
    private int PaddleId;
    private boolean PressedForTheFirstTime = true;

    private boolean ballBelow = false;

    private boolean ballOver = true;

    public Paddle(int Id) {
        this.PaddleId = Id;
        initPaddle();
    }

    private void initPaddle() {
        loadImage();
        getImageDimensions();
        resetState();
    }

    private void loadImage() {
        ImageIcon Icon = new ImageIcon(getClass().getResource("/Images/bat1.png"));
        if(PaddleId == 0) {
            Icon = new ImageIcon(getClass().getResource("/Images/bat1.png"));
        }else if(PaddleId == 1) {
            Icon = new ImageIcon(getClass().getResource("/Images/bat2.png"));
        } else if(PaddleId == 2) {
            Icon = new ImageIcon(getClass().getResource("/Images/bat3.png"));
        }
        image = Icon.getImage();
    }

    void move() {
        x += dx;
        if(x <= 0) {
            x = 0;
        }
        if(x >= Commons.WIDTH_LIMIT - imageWidth) {
            x = Commons.WIDTH_LIMIT - imageWidth;
        }
        if(Board.ball.getYCord() > getY() && !ballBelow) {
            ScoreboardPacket packet = new ScoreboardPacket(PaddleId, -1);
            Breakout.client.sendObject(packet);
            ballBelow = true;
            ballOver = false;
        }
        if(Board.ball.getYCord() < getY()) {
            ballBelow = false;
            ballOver = true;
        }
    }

    void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_A) {
            dx = -1;
        }
        if(key == KeyEvent.VK_D) {
            dx = 1;
        }
        if(PressedForTheFirstTime) {
            PressedForTheFirstTime = false;
            Breakout.client.sendObject(new SinglePaddlePacket(PaddleId, dx));
        }
    }

    void keyReleased(KeyEvent e) {
        dx = 0;
        if(!PressedForTheFirstTime) {
            Breakout.client.sendObject(new SinglePaddlePacket(PaddleId, dx));
            PressedForTheFirstTime = true;
        }
    }

    private void resetState() {
        x = Commons.INIT_PADDLE_X;
        y = Commons.INIT_PADDLE_Y - 50 * PaddleId;
        System.out.println("X : " + x + " Y: " + y);
    }

    public void resetToggle() {
        PressedForTheFirstTime = true;
        dx = 0;
        Breakout.client.sendObject(new SinglePaddlePacket(PaddleId, dx));
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDx() {
        return dx;
    }

    public int getPaddleId() {
        return PaddleId;
    }

    public void setPaddleY(int y){
        this.y = Commons.INIT_PADDLE_Y - 50 * y;
    }
}

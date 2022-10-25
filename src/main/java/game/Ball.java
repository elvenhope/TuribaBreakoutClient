package game;

import Packets.BallCordsPacket;
import Packets.ScoreboardPacket;
import sockets.ConnectionHandler;

import javax.swing.ImageIcon;
import java.util.Timer;
import java.util.TimerTask;

public class Ball extends Sprite{
    private int xdir;
    private int ydir;

    private int lastPaddle;
    private boolean AlreadyFirst = false;

    public Timer t;

    public Ball() {
        initBall();
    }

    private void initBall() {
        xdir = 1;
        ydir = -1;
        loadImage();
        getImageDimensions();
        resetState();
    }
    private void loadImage() {
        var Icon = new ImageIcon(getClass().getResource("/Images/Ball.png"));
        image = Icon.getImage();
    }

    void move() {
        x += xdir;
        y += ydir;

        if(x <= 0) {
            setXDir(1);
        }
        if(x >= Commons.WIDTH_LIMIT - imageWidth) {
            setXDir(-1);
        }
        if(y == 0) {
            setYDir(1);
        }
        if(ConnectionHandler.MyId == ConnectionHandler.getLowestMember() && !AlreadyFirst) {
            AlreadyFirst = true;
            UpdateBallCords();
        }
    }

    private void resetState() {
        x = Commons.INIT_BALL_X;
        y = Commons.INIT_BALL_Y;
    }

    public void setXDir(int x) {
        xdir = x;
    }

    public void setYDir(int y) {
        ydir = y;
    }

    public void setLastPaddle(int lastPaddle) {
        this.lastPaddle = lastPaddle;
        System.out.println(this.lastPaddle);
        this.UpdateScore(1);
    }

    public void UpdateScore(int score) {
        ScoreboardPacket packet = new ScoreboardPacket(this.lastPaddle, score);
        Breakout.client.sendObject(packet);
//        System.out.println("Update Score " + score + " Paddle " + this.lastPaddle);
    }

    int getYdir() {
        return ydir;
    }

    public void UpdateBallCords() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BallCordsPacket packet = new BallCordsPacket(x, y, xdir, ydir);
                Breakout.client.sendObject(packet);
            }
        }, 0,1000);
    }

    public void Update() {
        setX(ConnectionHandler.BallX);
        setY(ConnectionHandler.BallY);
        setXDir(ConnectionHandler.Xdir);
        setYDir(ConnectionHandler.Ydir);
        if(x <= 0) {
            setXDir(1);
        }
        if(x >= Commons.WIDTH_LIMIT - imageWidth) {
            setXDir(-1);
        }
    }
    public int getXCord() {
        return this.x;
    }
    public int getYCord() {
        return this.y;
    }
}

package game;

import Packets.GameStatePacket;
import Packets.ScoreboardPacket;
import sockets.ConnectionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Vector;

public class Board extends JPanel{
    private Timer timer;
    private String message = "Game Over";
    public static Ball ball;
    private static Paddle paddle;
    public static HashMap<Integer, Paddle> OtherPaddles = new HashMap<>();
    private static Brick[] bricks;
    private boolean inGame = true;

    public Board() {
        initBoard();
    }
    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));
        setBorder(BorderFactory.createLineBorder(Color.black));
        gameInit();
    }

    public static void updateState() {
//        OtherPaddles.clear();
//        System.out.println(ConnectionHandler.paddles.size());
        Vector<Integer> removeList = new Vector<>();
        for (HashMap.Entry<Integer, Integer> entry : ConnectionHandler.paddles.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
//            System.out.println("Connections:" + key + ":" + ConnectionHandler.connections.get(key));
//            System.out.println("LobbyList:" + key + ":" + ConnectionHandler.lobbyList.get(key));
            if(ConnectionHandler.connections.get(key) == null && ConnectionHandler.lobbyList.get(key) == null) {
                removeList.add(key);
            }
        }
        for (int i = 0; i < removeList.size(); i++) {
//            System.out.println("Removed :" + removeList.get(i));
            ConnectionHandler.paddles.remove(removeList.get(i));
            OtherPaddles.remove(removeList.get(i));
        }
//        System.out.println("Fresh Paddles: " + ConnectionHandler.paddles );
        for (HashMap.Entry<Integer, Integer> entry : ConnectionHandler.paddles.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
//            System.out.println("Update Key: " + key);
//            System.out.println("ConnectionHandler.MyId: " + ConnectionHandler.MyId);
            if(key != ConnectionHandler.MyId) {
//                System.out.println("Updating:" + key);
                if(OtherPaddles.get(key) != null) {
                    if(OtherPaddles.get(key).getDx() != value) {
                        OtherPaddles.get(key).setDx(value);
                    }
                } else {
                    OtherPaddles.put(key, new Paddle(key));
                    OtherPaddles.get(key).setDx(value);
                }
            }
        }
        if(ConnectionHandler.paddlesOrder.size() > 0) {
            for (HashMap.Entry<Integer, Integer> entry : ConnectionHandler.paddlesOrder.entrySet()) {
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                if(key == ConnectionHandler.MyId) {
                    paddle.setPaddleY(value);
                } else {
                    OtherPaddles.get(key).setPaddleY(value);
                }
            }
        }
        int k = 0;
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 6; j++) {
                if(ConnectionHandler.brickTracker.get(k) != null) {
                    if(ConnectionHandler.brickTracker.get(k) == true && bricks[k].isDestroyed() == false) {
                        bricks[k].setDestroyed(true);
                    }
                }
                k++;
            }
        }
    }

    private void gameInit() {
        bricks = new Brick[Commons.N_OF_BRICKS];
        ball = new Ball();
        paddle = new Paddle(ConnectionHandler.MyId);
//        System.out.println(ConnectionHandler.paddles);
        for (HashMap.Entry<Integer, Integer> entry : ConnectionHandler.paddles.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
//            System.out.println("Initalizing : " + key);
            if(key != ConnectionHandler.MyId) {
                OtherPaddles.put(key, new Paddle(key));
                OtherPaddles.get(key).setDx(value);
            }
        }
        int k = 0;
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 45 + 30, i * 15 + 50, k);
                if(ConnectionHandler.brickTracker.get(k) != null) {
                    if(ConnectionHandler.brickTracker.get(k) == true && bricks[k].isDestroyed() == false) {
                        bricks[k].setDestroyed(true);
                    }
                }
                k++;
            }
        }
        Breakout.game.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (e.getOppositeWindow()==null) {
                    System.out.println("Gained Focus");
                }
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                System.out.println("Lost Focus");
                paddle.resetToggle();
            }
        });
        timer = new Timer(Commons.PERIOD, new GameCycle());
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if(inGame) {
            drawObjects(g2d);
        } else {
            if(ball.t != null) {
                ball.t.cancel();
            }
            stopGame();
            gameFinished(g2d);
        }
        Toolkit.getDefaultToolkit().sync();
    }
    private void drawObjects(Graphics2D g2d) {
        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(), ball.getImageWidth(), ball.getImageHeight(), this);
        g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(), paddle.getImageWidth(), paddle.getImageHeight(), this);
//        System.out.println("Fresh Paddles: " + OtherPaddles);
        for (HashMap.Entry<Integer, Paddle> entry : OtherPaddles.entrySet()) {
            Integer key = entry.getKey();
            Paddle value = entry.getValue();
            if(key != ConnectionHandler.MyId) {
                g2d.drawImage(value.getImage(), value.getX(), value.getY(), value.getImageWidth(), value.getImageHeight(), this);
            }
        }
        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
            if(!bricks[i].isDestroyed()) {
                g2d.drawImage(bricks[i].getImage(), bricks[i].getX(), bricks[i].getY(), bricks[i].getImageWidth(), bricks[i].getImageHeight(), this);
            }
        }
    }

    private void gameFinished(Graphics2D g2d) {
        var font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics fontMetrics = this.getFontMetrics(font);
        if(ConnectionHandler.GetWinner() == ConnectionHandler.MyId) {
            message = "Congratulations You Won!";
            g2d.setColor(Color.black);
            g2d.setFont(font);
            g2d.drawString(message, (Commons.WIDTH_LIMIT - fontMetrics.stringWidth(message)), Commons.HEIGHT / 2);
        } else {
            message = "You lost";
            g2d.setColor(Color.black);
            g2d.setFont(font);
            g2d.drawString(message, (Commons.WIDTH_LIMIT - fontMetrics.stringWidth(message)), Commons.HEIGHT / 2);
            message = "But you will get them";
            g2d.drawString(message, (Commons.WIDTH_LIMIT - fontMetrics.stringWidth(message)), Commons.HEIGHT / 2 + 50);
            message = "Next Time";
            g2d.drawString(message, (Commons.WIDTH_LIMIT - fontMetrics.stringWidth(message)), Commons.HEIGHT / 2 + 100);
        }
    }

    private class TAdapter extends KeyAdapter {
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private void doGameCycle() {
        ball.move();
        paddle.move();
        for (HashMap.Entry<Integer, Paddle> entry : OtherPaddles.entrySet()) {
            Integer key = entry.getKey();
            Paddle value = entry.getValue();
            value.move();
        }
        checkCollision();
        repaint();
    }

    public void stopGame() {
        inGame = false;
        timer.stop();
    }

    private void checkCollision() {
        if(ball.getRect().getMaxY() > Commons.BOTTOM_EDGE) {
//            stopGame();
            ScoreboardPacket packet = new ScoreboardPacket(ConnectionHandler.MyId, -1);
            Breakout.client.sendObject(packet);
            ball.setYDir(-1);
        }
        for (int i = 0, j = 0; i < Commons.N_OF_BRICKS; i++) {
            if(bricks[i].isDestroyed()) {
                j++;
            }
            if(j == Commons.N_OF_BRICKS) {
                stopGame();
                GameStatePacket finalPacket = new GameStatePacket("Finished");
                Breakout.client.sendObject(finalPacket);
                message = "Congratulations";
            }
        }

        if(ball.getRect().intersects(paddle.getRect())) {
            int PaddleLPos = (int) paddle.getRect().getMinX();
            int BallLPos = (int) ball.getRect().getMinX();
            int first = PaddleLPos + 8;
            int second = PaddleLPos + 16;
            int third = PaddleLPos + 24;
            int fourth = PaddleLPos + 32;
            ball.setLastPaddle(paddle.getPaddleId());

            if(BallLPos < first) {
                ball.setXDir(-1);
                ball.setYDir(-1);
            }
            if(BallLPos >= first && BallLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYdir());
            }
            if(BallLPos >= second && BallLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
            }
            if(BallLPos >= third && BallLPos < fourth) {
                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYdir());
            }
            if(BallLPos > fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
            }
            System.out.println("");
        }

        for (HashMap.Entry<Integer, Paddle> entry : OtherPaddles.entrySet()) {
            Integer key = entry.getKey();
            Paddle value = entry.getValue();
            if(ball.getRect().intersects(value.getRect())) {
                int PaddleLPos = (int) value.getRect().getMinX();
                int BallLPos = (int) ball.getRect().getMinX();
                int first = PaddleLPos + 8;
                int second = PaddleLPos + 16;
                int third = PaddleLPos + 24;
                int fourth = PaddleLPos + 32;
                ball.setLastPaddle(value.getPaddleId());

                if(BallLPos < first) {
                    ball.setXDir(-1);
                    ball.setYDir(-1);
                }
                if(BallLPos >= first && BallLPos < second) {
                    ball.setXDir(-1);
                    ball.setYDir(-1 * ball.getYdir());
                }
                if(BallLPos >= second && BallLPos < third) {
                    ball.setXDir(0);
                    ball.setYDir(-1);
                }
                if(BallLPos >= third && BallLPos < fourth) {
                    ball.setXDir(-1);
                    ball.setYDir(-1 * ball.getYdir());
                }
                if(BallLPos > fourth) {
                    ball.setXDir(1);
                    ball.setYDir(-1);
                }
            }
        }

        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
            if((ball.getRect().intersects(bricks[i].getRect()))) {
                int ballLeft = (int) ball.getRect().getMinX();
                int ballHeight = (int) ball.getRect().getHeight();
                int ballWidth = (int) ball.getRect().getWidth();
                int ballTop = (int) ball.getRect().getMinY();

                var pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                var pointLeft = new Point(ballLeft - 1, ballTop);
                var pointTop = new Point(ballLeft, ballTop - 1);
                var pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if(!bricks[i].isDestroyed()) {
                    if(bricks[i].getRect().contains(pointRight)) {
                        ball.setXDir(-1);
                    }
                    if(bricks[i].getRect().contains(pointLeft)) {
                        ball.setXDir(1);
                    }
                    if(bricks[i].getRect().contains(pointTop)) {
                        ball.setYDir(1);
                    }
                    if(bricks[i].getRect().contains(pointBottom)) {
                        ball.setYDir(-1);
                    }
                    bricks[i].setDestroyed(true);
                    bricks[i].UpdateBrick();
                }
            }
        }
    }
}
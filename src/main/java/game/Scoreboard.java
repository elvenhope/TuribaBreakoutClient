package game;

import sockets.ConnectionHandler;

import javax.swing.*;
import java.awt.*;

public class Scoreboard extends JPanel {
    private static JList playerList = new JList<>(ConnectionHandler.lobbyList);
    private static JList scoreList = new JList<>();
    public Scoreboard() {
        initUI();
    }
    private void initUI() {
        setPreferredSize(new Dimension(Commons.WIDTH - Commons.WIDTH_LIMIT, Commons.HEIGHT));
        setBackground(new Color(255, 204, 0));
        add(playerList);
        add(scoreList);
    }

    public static void setList() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < ConnectionHandler.lobbyList.size(); i++) {
            model.addElement(ConnectionHandler.lobbyList.get(i));
        }
        playerList.setModel(model);
    }

    public static void setScoreList() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < ConnectionHandler.scoreTracker.size(); i++) {
            model.addElement(ConnectionHandler.scoreTracker.get(i));
        }
        scoreList.setModel(model);
    }
}

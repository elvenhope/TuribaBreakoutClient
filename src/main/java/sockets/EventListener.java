package sockets;

import Packets.*;
import game.Board;
import game.Breakout;
import game.Scoreboard;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

import static game.Board.OtherPaddles;
import static game.Breakout.board;
import static game.Breakout.scoreboard;

public class EventListener {

    public void received(Object p) {
        if(p instanceof AddConnectionPacket) {
            AddConnectionPacket packet = (AddConnectionPacket)p;
            ConnectionHandler.connections.put(packet.id,new Connection(packet.id));
            System.out.println(packet.id + " has connected");
        } else if(p instanceof RemoveConnectionPacket) {
            RemoveConnectionPacket packet = (RemoveConnectionPacket)p;
            System.out.println("Connection: " + packet.id + " has disconnected");
            ConnectionHandler.connections.remove(packet.id);
            ConnectionHandler.paddles.remove(packet.id);
            ConnectionHandler.scoreTracker.remove(packet.id);
            OtherPaddles.remove(packet.id);
        } else if (p instanceof LobbyConnectionListPacket) {
            LobbyConnectionListPacket packet = (LobbyConnectionListPacket) p;
            ConnectionHandler.lobbyList.clear();
            for (int i = 0; i < packet.CurrentUserIDs.size(); i++) {
                ConnectionHandler.lobbyList.add(packet.CurrentUserIDs.get(i));
            }
            System.out.println("Current Lobby List: " + ConnectionHandler.lobbyList);
            Scoreboard.setList();
        } else if (p instanceof IdentityPacket) {
            IdentityPacket packet = (IdentityPacket) p;
            ConnectionHandler.MyId = packet.id;
            System.out.println("My Id is " + ConnectionHandler.MyId);
        } else if (p instanceof PaddlesPacket) {
            PaddlesPacket packet = (PaddlesPacket) p;
//            System.out.println(p);
            ConnectionHandler.paddles.clear();
            for (HashMap.Entry<Integer, Integer> entry : packet.paddles.entrySet()) {
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                ConnectionHandler.paddles.put(key, value);
            }
//            System.out.println("Event Listening: " + ConnectionHandler.paddles);
            Board.updateState();
        } else if (p instanceof brickTrackerPacket) {
            brickTrackerPacket packet = (brickTrackerPacket) p;
            ConnectionHandler.brickTracker.clear();
            for (HashMap.Entry<Integer, Boolean> entry : packet.brickTracker.entrySet()) {
                Integer key = entry.getKey();
                Boolean value = entry.getValue();
                ConnectionHandler.brickTracker.put(key, value);
            }
//            System.out.println(ConnectionHandler.brickTracker);
        } else if (p instanceof BallCordsPacket) {
            BallCordsPacket packet = (BallCordsPacket) p;
            ConnectionHandler.Update(packet.BallX, packet.BallY, packet.Xdir, packet.Ydir);
            if(Board.ball != null) {
                Board.ball.Update();
            }
        } else if(p instanceof GameStatePacket) {
            GameStatePacket packet = (GameStatePacket) p;
            System.out.println(Objects.equals(packet.State, "Started"));
            if (Objects.equals(packet.State, "Started")) {
                Breakout.game.board = new Board();
                Breakout.game.scoreboard = new Scoreboard();
                Breakout.game.add(board, BorderLayout.CENTER);
                Breakout.game.add(scoreboard, BorderLayout.EAST);
                Breakout.game.MainMenu.setVisible(false);
                board.updateState();
            }
        } else if(p instanceof AllScoresPacket) {
            AllScoresPacket packet = (AllScoresPacket) p;
            ConnectionHandler.scoreTracker.clear();
            for (HashMap.Entry<Integer, Integer> entry : packet.scoreTracker.entrySet()) {
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                ConnectionHandler.scoreTracker.put(key, value);
            }
//            System.out.println("Current Score List: " + ConnectionHandler.scoreTracker);
            Scoreboard.setScoreList();
        } else if(p instanceof PaddleSwitchPacket) {
            System.out.println("Switching");
            PaddleSwitchPacket packet = (PaddleSwitchPacket) p;
            ConnectionHandler.paddlesOrder.clear();
            for (HashMap.Entry<Integer, Integer> entry : packet.paddles.entrySet()) {
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                ConnectionHandler.paddlesOrder.put(key, value);
            }
            System.out.println(ConnectionHandler.paddlesOrder);
//            Board.updateState();
        }
    }

}

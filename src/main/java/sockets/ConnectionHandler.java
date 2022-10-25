package sockets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class ConnectionHandler {
    public static HashMap<Integer, Connection> connections = new HashMap<>();
    public static Vector<Integer> lobbyList = new Vector<>();

    public static HashMap<Integer, Integer> paddles = new HashMap<>();

    public static HashMap<Integer, Integer> paddlesOrder = new HashMap<>();

    public static HashMap<Integer, Boolean> brickTracker = new HashMap<>();

    public static HashMap<Integer, Integer> scoreTracker = new HashMap<>();

    public static int BallX;
    public static int BallY;
    public static int Xdir;
    public static int Ydir;

    public static int MyId;

    public static void Update(int NewBallX, int NewBallY, int NewXdir, int NewYdir) {
        BallX = NewBallX;
        BallY = NewBallY;
        Xdir = NewXdir;
        Ydir = NewYdir;
    }

    public static int getLowestMember() {
        return Collections.min(lobbyList);
    }

    public static int GetWinner() {
        int MaxValueID = 0;
        int MaxValue = 0;
        for (HashMap.Entry<Integer, Integer> entry : scoreTracker.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if(value > MaxValue) {
                MaxValue = value;
                MaxValueID = key;
            }
        }
        return MaxValueID;
    }
}

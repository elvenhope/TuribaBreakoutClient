package Packets;

import sockets.ConnectionHandler;

import java.io.Serializable;
import java.util.HashMap;

public class PaddlesPacket implements Serializable {
    private static final long serialVersionUID = 8L;
    public HashMap<Integer, Integer> paddles = new HashMap<>();
}

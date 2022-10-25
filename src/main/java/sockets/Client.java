package sockets;

import Packets.RemoveConnectionPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
    // Client Variables
    private String host;
    private int port;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running = false;
    private EventListener listener;


    // constructor
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //Connect To The Server
    public void connect() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            listener = new EventListener();
            new Thread(this).start();
        } catch (ConnectException e) {
            System.out.println("Server is Offline or No Internet Connection. Unable to Connect");
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Close The Connection
    public void close() {
        try {
            running = false;
            RemoveConnectionPacket packet = new RemoveConnectionPacket();
            sendObject(packet);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Send Data To The Server
    public void sendObject(Object Packet) {
        try {
            out.writeObject(Packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Object data = in.readObject();
//                System.out.println("Incoming Data: " + data);
                //Handle Data
                listener.received(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        close();
    }
}

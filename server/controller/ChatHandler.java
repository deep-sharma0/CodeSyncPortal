package server.controller;

import shared.Message;
import server.ServerMain;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatHandler extends Thread {
    private Socket socket;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    private String name;

    public ChatHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            name = (String) in.readObject(); // Get client name
            System.out.println(" " + name + " joined the chat.");

            Object input;
            while ((input = in.readObject()) != null) {
                if (input instanceof Message) {
                    Message msg = (Message) input;
                    System.out.println( msg.getSender() + ": " + msg.getContent());
                    broadcast(msg);
                }
            }
        } catch (Exception e) {
            System.out.println(" " + name + " disconnected.");
        } finally {
            ServerMain.clients.remove(this);
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(Message msg) {
        for (ChatHandler client : ServerMain.clients) {
            if (client != this) {
                try {
                    client.out.writeObject(msg);
                    client.out.flush();
                } catch (Exception e) {
                    System.out.println(" Failed to send to " + client.name);
                }
            }
        }
    }
}

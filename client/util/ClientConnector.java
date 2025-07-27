package client.util;

import shared.Message;
import shared.Message.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.BiConsumer;

public class ClientConnector {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String username;
    private BiConsumer<String, String> chatCallback;
    private BiConsumer<String, String> codeCallback;

    public ClientConnector(String username,
                           BiConsumer<String, String> chatCallback,
                           BiConsumer<String, String> codeCallback) {
        this.username = username;
        this.chatCallback = chatCallback;
        this.codeCallback = codeCallback;

        connect();
    }

    private void connect() {
        try {
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Send username
            out.writeObject(username);
            out.flush();

            // Start listener thread
            new Thread(() -> {
                try {
                    while (true) {
                        Object obj = in.readObject();
                        if (obj instanceof Message msg) {
                            if (msg.getType() == MessageType.CHAT) {
                                chatCallback.accept(msg.getSender(), msg.getContent());
                            } else if (msg.getType() == MessageType.CODE_EDIT && !msg.getSender().equals(username)) {
                                codeCallback.accept(msg.getSender(), msg.getContent());
                            }
                        }
                    }
                } catch (Exception e) {
                    chatCallback.accept("System", "⚠ Disconnected from server.");
                }
            }).start();

        } catch (Exception e) {
            chatCallback.accept("System", "⚠ Cannot connect to server.");
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            chatCallback.accept("System", "⚠ Failed to send message.");
        }
    }
}

package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.controller.ChatHandler;

public class ServerMain {
    public static final int PORT = 5000;
    public static List<ChatHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(" Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(" New client connected.");
                ChatHandler handler = new ChatHandler(clientSocket);
                clients.add(handler);
                handler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

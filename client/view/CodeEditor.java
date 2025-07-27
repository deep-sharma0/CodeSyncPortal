package client.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import shared.Message;
import shared.Message.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CodeEditor extends Application {

    private TextArea codeArea;
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String username;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Ask username
        TextInputDialog dialog = new TextInputDialog("User");
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");
        username = dialog.showAndWait().orElse("Anonymous");

        // Setup stage
        stage.setTitle("CodeSync Portal - " + username);

        // Code editor area
        codeArea = new TextArea();
        codeArea.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        codeArea.setPromptText("Write code here...");
        codeArea.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 13px;");
        codeArea.setWrapText(false);
        codeArea.setPrefHeight(400);
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            if (out != null && !newText.equals(oldText)) {
                sendMessage(new Message(MessageType.CODE_EDIT, username, newText));
            }
        });

        // Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(100);

        // Input field
        inputField = new TextField();
        inputField.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        inputField.setPromptText("Enter message...");

        // Send button
        sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(new Message(MessageType.CHAT, username, message));
                inputField.clear();
            }
        });

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setPadding(new Insets(5));

        VBox layout = new VBox(10, codeArea, chatArea, inputBox);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 800, 600));
        stage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Send username to server
            out.writeObject(username);
            out.flush();

            // Background listener thread
            new Thread(() -> {
                try {
                    while (true) {
                        Object obj = in.readObject();
                        if (obj instanceof Message msg) {
                            Platform.runLater(() -> {
                                switch (msg.getType()) {
                                    case CHAT -> chatArea.appendText(msg.getSender() + ": " + msg.getContent() + "\n");
                                    case CODE_EDIT -> {
                                        if (!msg.getSender().equals(username)) {
                                            codeArea.setText(msg.getContent());
                                        }
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> chatArea.appendText(" Disconnected from server.\n"));
                }
            }).start();

        } catch (Exception e) {
            chatArea.appendText(" Cannot connect to server.\n");
        }
    }

    private void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            chatArea.appendText(" Failed to send message.\n");
        }
    }
}

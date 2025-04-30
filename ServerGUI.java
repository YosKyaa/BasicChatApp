import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerGUI extends JFrame {
    private JTextArea clientStatusArea;
    private JTextArea chatLogArea;
    private ServerSocket serverSocket;
    public static Map<String, ClientHandler> clients = new HashMap<>();

    public ServerGUI() {
        setTitle("Chat Server");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        clientStatusArea = new JTextArea();
        chatLogArea = new JTextArea();

        clientStatusArea.setEditable(false);
        chatLogArea.setEditable(false);

        add(new JLabel("Client Status"));
        add(new JScrollPane(clientStatusArea));
        add(new JLabel("Chat Log"));
        add(new JScrollPane(chatLogArea));

        setVisible(true);
        startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(12345);
            appendChat("Server started on port 12345...");
            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(socket, this);
                        handler.start();
                    } catch (IOException e) {
                        appendChat("Error accepting client.");
                    }
                }
            }).start();
        } catch (IOException e) {
            appendChat("Failed to start server.");
        }
    }

    public synchronized void updateClientStatus() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            sb.append(entry.getKey())
              .append(" : ")
              .append(entry.getValue().isConnected() ? "Online" : "Offline")
              .append("\n");
        }
        clientStatusArea.setText(sb.toString());
    }

    public synchronized void appendChat(String message) {
        chatLogArea.append(message + "\n");
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}

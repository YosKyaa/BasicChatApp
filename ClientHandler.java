import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private ServerGUI serverGUI;

    public ClientHandler(Socket socket, ServerGUI serverGUI) {
        this.socket = socket;
        this.serverGUI = serverGUI;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            clientName = in.readLine();

            ServerGUI.clients.put(clientName, this);
            serverGUI.appendChat(clientName + " joined.");
            serverGUI.updateClientStatus();
            broadcast(clientName + " joined the chat.");

            String message;
            while ((message = in.readLine()) != null) {
                serverGUI.appendChat(clientName + ": " + message);
                broadcast(clientName + ": " + message);
            }
        } catch (IOException e) {
            serverGUI.appendChat(clientName + " disconnected.");
        } finally {
            ServerGUI.clients.remove(clientName);
            broadcast(clientName + " left the chat.");
            serverGUI.updateClientStatus();
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : ServerGUI.clients.values()) {
            client.out.println(message);
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}

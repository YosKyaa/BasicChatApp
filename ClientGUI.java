import javax.swing.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;
    private String name;

    public ClientGUI() {
        name = JOptionPane.showInputDialog(this, "Enter your name:");
        setTitle("Chat - " + name);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        inputField = new JTextField();

        add(new JScrollPane(chatArea), "Center");
        add(inputField, "South");

        inputField.addActionListener(e -> {
            String msg = inputField.getText();
            out.println(msg);
            inputField.setText("");
        });

        setVisible(true);
        connectToServer();
    }

    public void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(name); // Send name to server

            new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        chatArea.append(line + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connection lost.\n");
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to server.");
        }
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}

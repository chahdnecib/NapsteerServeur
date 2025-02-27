package src;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Server {
    private static final int PORT = 9090;

    public static void main(String[] args) {
        
        System.out.println("Serveur en cours de démarrage...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté : " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (Connection conn = DatabaseManager.getConnection();
             DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                String command = in.readUTF();
                CommandHandler.handle(command, conn, in, out);
            }
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}

package src;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    private static final int PORT = 9090;

    public static void main(String[] args) {
        System.out.println("Serveur en attente de connexions sur le port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté : " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
             Connection conn = DatabaseManager.getConnection()) { // Connexion à la base de données

            System.out.println("Attente de la commande...");
            String command = in.readUTF();
            System.out.println("Commande reçue : " + command);

            if ("LOGIN".equalsIgnoreCase(command)) {
                String username = in.readUTF();
                String password = in.readUTF();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                final int port_c = 5000;
                if (isAuthenticated(conn, username, password)) {
                    out.writeUTF("SUCCESS");
                    CommandHandler.updateClientConnection(conn, username, clientIP, port_c);
                } else {
                    out.writeUTF("FAIL");
                }

            } else if ("UPLOAD".equalsIgnoreCase(command)) {
                FileUploadHandler.handleFileUpload(conn, in, out);

            } else if ("SEARCH".equalsIgnoreCase(command)) {
                FileSearchHandler.handleSearch(conn, in, out);
                out.writeUTF("END"); // Signal de fin des résultats

            } else if ("SIGNUP".equalsIgnoreCase(command)) {
                try {
                
                    String clientIp = clientSocket.getInetAddress().getHostAddress(); // Récupération de l'adresse IP du client
                    CommandHandler.handleSignup(conn, in, out,clientIp); // Passer l'adresse IP
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }if ("LOGOUT".equalsIgnoreCase(command)) {
                try {
                    CommandHandler.handleLogout(in, out, conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
             else {
                out.writeUTF("Commande inconnue.");
            }

        } catch (EOFException e) {
            System.out.println("Erreur : Données incomplètes reçues du client.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isAuthenticated(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM clients WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static void updateClientIpAddress(Connection conn, String username, String clientIpAddress) throws SQLException {
        String updateQuery = "UPDATE clients SET adresseIP = ?, derniereConnexion = NOW() WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, clientIpAddress);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Adresse IP et dernière connexion mises à jour pour " + username);
        }
    }
    
}

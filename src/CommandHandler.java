// CommandHandler.java
package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandHandler {

    public static void handle(String command, Connection conn, DataInputStream in, DataOutputStream out, String clientIpAddress) throws Exception {
        switch (command) {
            case "SIGNUP" -> handleSignup(conn, in, out, clientIpAddress);
            case "LOGIN" -> handleLogin(conn, in, out, clientIpAddress);
            case "SEARCH" -> FileSearchHandler.handleSearch(conn, in, out);
            case "UPLOAD" -> FileUploadHandler.handleFileUpload(conn, in, out);
            case "LOGOUT" -> handleLogout(in, out, conn);
            default -> out.writeUTF("Commande inconnue.");
        }
    }

    public static void handleSignup(Connection conn, DataInputStream in, DataOutputStream out, String clientIpAddress) throws Exception {
        String username = in.readUTF();
        String password = in.readUTF();
        final int port = 5000;

        // Vérifier si le nom d'utilisateur est déjà utilisé
        String checkQuery = "SELECT COUNT(*) FROM clients WHERE username = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    out.writeUTF("Nom d'utilisateur déjà pris.");
                    return;
                }
            }
        }

        // Insérer les informations du client (nom d'utilisateur, mot de passe, IP, port)
        String insertQuery = "INSERT INTO clients (username, password, adresseIP, port, derniereConnexion ,connecte) VALUES (?, ?, ?, ?, NOW(), TRUE)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, clientIpAddress);
            insertStmt.setInt(4, port);
            insertStmt.executeUpdate();
            out.writeUTF("SUCCESS");
            System.out.println("Nouvel utilisateur ajouté : " + username + " (" + clientIpAddress + ":" + port + ")");
        }
    }
//ATTENTION ELLE N EST PAS UTILISER
    public static void handleLogin(Connection conn, DataInputStream in, DataOutputStream out, String clientIpAddress) throws Exception {
        String username = in.readUTF();
        String password = in.readUTF();
        int port = in.readInt();
        System.out.println("Port reçu pour " + username + " : " + port);

        String query = "SELECT COUNT(*) FROM clients WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    out.writeUTF("Connexion réussie.");
                  // Met à jour l'adresse IP, le port et le statut connecté
                updateClientConnection(conn, username, clientIpAddress, port);
                    RefreshTaskManager.startRefreshTask(conn, username);
                } else {
                    out.writeUTF("Nom d'utilisateur ou mot de passe incorrect.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.writeUTF("Erreur serveur lors de la connexion.");
        }
    }

     static void updateClientIpAddress(Connection conn, String username, String clientIpAddress) throws SQLException {
        String updateQuery = "UPDATE clients SET adresseIP = ?, connecte = TRUE, derniereConnexion = now() WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, clientIpAddress);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Adresse IP mise à jour pour " + username + ": " + clientIpAddress);
        }
    }
    public static void updateClientConnection(Connection conn, String username, String clientIpAddress, int port) throws SQLException {
        String updateQuery = "UPDATE clients SET adresseIP = ?, port = ?, connecte = 1 ,derniereConnexion = now() WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, clientIpAddress);
            stmt.setInt(2, port);
            stmt.setString(3, username);
            stmt.executeUpdate();
            System.out.println("Mise à jour de la connexion : " + username + " | IP: " + clientIpAddress + " | Port: " + port);
        }
    }
    

   static void handleLogout(DataInputStream in, DataOutputStream out, Connection conn) throws Exception {
        String username = in.readUTF();
    
        // Mettre à jour le statut de connexion de l'utilisateur
        String updateQuery = "UPDATE clients SET connecte = FALSE WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println(username + " s'est déconnecté.");
        }
    
        out.writeUTF("Déconnexion réussie.");
        RefreshTaskManager.stopRefreshTask(username);
    }
    
}

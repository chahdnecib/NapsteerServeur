package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CommandHandler {

    // Gestion des commandes
    public static void handle(String command, Connection conn, DataInputStream in, DataOutputStream out) throws Exception {
        switch (command) {
            case "SIGNUP" -> handleSignup(conn, in, out);
            case "LOGIN" -> handleLogin(conn, in, out);
            case "SEARCH" -> FileSearchHandler.handleSearch(conn, in, out);
            case "UPLOAD" ->FileUploadHandler.handleFileUpload(conn, in, out);
            case "LOGOUT" -> handleLogout(in, out);
            default -> out.writeUTF("Commande inconnue.");
        }
    }

    // Fonction d'inscription
    private static void handleSignup(Connection conn, DataInputStream in, DataOutputStream out) throws Exception {
        String username = in.readUTF();
        String password = in.readUTF();
        String adresseIP = in.readUTF();
        int port = in.readInt();

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

        String insertQuery = "INSERT INTO clients (username, password, adresseIP, port, derniereConnexion) VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, adresseIP);
            insertStmt.setInt(4, port);
            insertStmt.executeUpdate();
            out.writeUTF("Inscription réussie.");
        }

        // Démarrer le rafraîchissement périodique des données
        RefreshTaskManager.startRefreshTask(conn, username);
    }

    // Fonction de connexion
    private static void handleLogin(Connection conn, DataInputStream in, DataOutputStream out) throws Exception {
        // Lire les données envoyées par le client
        String username = in.readUTF();          // Récupérer le nom d'utilisateur
        String password = in.readUTF();          // Récupérer le mot de passe
        String clientIpAddress = in.readUTF();   // Récupérer l'adresse IP envoyée par le client
    
        // Requête SQL pour vérifier les identifiants
        String query = "SELECT COUNT(*) FROM clients WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
    
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    // Si les identifiants sont corrects
                    out.writeUTF("Connexion réussie.");
    
                    // Mettre à jour l'adresse IP dans la base de données
                    updateClientIpAddress(conn, username, clientIpAddress);
    
                    // Démarrer une tâche périodique pour cet utilisateur (si applicable)
                    RefreshTaskManager.startRefreshTask(conn, username);
                    
                } else {
                    // Si les identifiants sont incorrects
                    out.writeUTF("Nom d'utilisateur ou mot de passe incorrect.");
                }
            }
        } catch (SQLException e) {
            // Gérer les erreurs SQL
            e.printStackTrace();
            out.writeUTF("Erreur serveur lors de la connexion.");
        }
    }
    
    private static void updateClientIpAddress(Connection conn, String username, String clientIpAddress) throws SQLException {
    String updateQuery = "UPDATE clients SET adresseIP = ? WHERE username = ?";
    try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
        stmt.setString(1, clientIpAddress);
        stmt.setString(2, username);
        stmt.executeUpdate();
        System.out.println("Adresse IP mise à jour pour " + username + ": " + clientIpAddress);
    }
}
    

    // Fonction de déconnexion
    private static void handleLogout(DataInputStream in, DataOutputStream out) throws Exception {
        String username = in.readUTF();
        out.writeUTF("Déconnexion réussie.");
        // Arrêter la tâche périodique pour cet utilisateur
        RefreshTaskManager.stopRefreshTask(username);
    }
}

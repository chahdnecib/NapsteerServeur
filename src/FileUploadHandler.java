package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileUploadHandler {

    public static void handleFileUpload(Connection conn, DataInputStream in, DataOutputStream out) throws IOException {
        try {
            // Lire le nom d'utilisateur, le nom du fichier et la taille
            String username = in.readUTF();
            String nomFichier = in.readUTF();
            long tailleFichier = in.readLong();
    
            // Insérer dans la table 'fichiers'
            String query = "INSERT INTO fichiers (username, nomFichier, tailleFichier) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);    // Associer le fichier à l'utilisateur
                stmt.setString(2, nomFichier); // Nom du fichier
                stmt.setLong(3, tailleFichier); // Taille du fichier
                stmt.executeUpdate();
            }
    
            // Confirmation au client
            out.writeUTF("Fichier '" + nomFichier + "' (taille : " + tailleFichier + " octets) enregistré pour l'utilisateur " + username + ".");
            System.out.println("Fichier enregistré : " + nomFichier + " (" + tailleFichier + " octets) pour l'utilisateur " + username);
        } catch (SQLException e) {
            out.writeUTF("Erreur lors de l'enregistrement du fichier : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}    
package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSearchHandler {

    public static void handleSearch(Connection conn, DataInputStream in, DataOutputStream out) throws Exception {
        // Lire le nom du fichier à rechercher
        String searchFilename = in.readUTF();

        // Requête pour trouver les utilisateurs associés au nom de fichier
        String query = "SELECT c.username, c.adresseIP, c.port, c.derniereConnexion " +
                       "FROM clients c " +
                       "JOIN fichiers f ON c.username = f.username " +
                       "WHERE f.nomFichier LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchFilename + "%"); // Recherche partielle sur le nom de fichier
            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    String username = rs.getString("username");
                    String adresseIP = rs.getString("adresseIP");
                    int port = rs.getInt("port");
                    String lastConnection = rs.getString("derniereConnexion");

                    // Convertir la dernière connexion en objet Date
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date lastConnectionDate = formatter.parse(lastConnection);

                    // Heure actuelle
                    Date currentTime = new Date();

                    // Calculer la différence en millisecondes
                    long diffInMillis = currentTime.getTime() - lastConnectionDate.getTime();

                    // Convertir la différence en minutes
                    long diffInMinutes = diffInMillis / (1000 * 60);

                    // Vérification si la différence est de moins de 5 minutes
                    if (diffInMinutes < 5) {
                        String result = "Noeud : " + username + ", Adresse IP : " + adresseIP + ", Port : " + port;
                        out.writeUTF(result);
                        found = true;
                    }
                }

                if (!found) {
                    out.writeUTF("Aucun utilisateur trouvé avec ce fichier et une connexion récente (< 5 minutes).");
                }
            }
        }
    }
}

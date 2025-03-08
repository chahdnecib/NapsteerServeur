package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSearchHandler {

    public static void handleSearch(Connection conn, DataInputStream in, DataOutputStream out) {
        try {
            String searchFilename = in.readUTF();
            System.out.println("Recherche du fichier : " + searchFilename);

            String query = "SELECT c.username, c.adresseIP, c.port, c.derniereConnexion " +
                           "FROM clients c " +
                           "JOIN fichiers f ON c.username = f.username " +
                           "WHERE f.nomFichier LIKE ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "%" + searchFilename + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean found = false;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentTime = new Date();

                    while (rs.next()) {
                        String username = rs.getString("username");
                        String adresseIP = rs.getString("adresseIP");
                        int port = rs.getInt("port");
                        String lastConnection = rs.getString("derniereConnexion");

                        Date lastConnectionDate = formatter.parse(lastConnection);
                        long diffInMinutes = (currentTime.getTime() - lastConnectionDate.getTime()) / (1000 * 60);

                        if (diffInMinutes < 5) {
                            String result = "Noeud : " + username + ", IP : " + adresseIP + ", Port : " + port;
                            out.writeUTF(result);
                            found = true;
                        }
                    }

                    if (!found) {
                        out.writeUTF("Aucun utilisateur actif trouvé pour ce fichier.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                out.writeUTF("Erreur lors de la recherche du fichier.");
            } catch (Exception ignored) {}
        }
    }
}

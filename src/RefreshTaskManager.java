package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;


public class RefreshTaskManager {
    private static final ConcurrentHashMap<String, Thread> activeThreads = new ConcurrentHashMap<>();

    // Méthode pour démarrer une tâche de rafraîchissement
    public static void startRefreshTask(Connection conn, String username) {
        // Si une tâche existe déjà pour cet utilisateur, ne rien faire
        if (activeThreads.containsKey(username)) return;

        // Créer un thread pour gérer la mise à jour périodique
        Thread refreshThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) { // Vérifie si le thread est interrompu
                    // Requête SQL pour mettre à jour la dernière connexion
                    String query = "UPDATE clients SET derniereConnexion = NOW() WHERE username = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, username);
                        stmt.executeUpdate();
                        System.out.println("Dernière connexion mise à jour pour : " + username);
                    } catch (SQLException e) {
                        System.err.println("Erreur pendant la mise à jour pour " + username + ": " + e.getMessage());
                    }

                    // Attendre 5 minutes avant la prochaine mise à jour
                    Thread.sleep(5 * 60 * 1000);
                }
            } catch (InterruptedException e) {
                // Le thread a été interrompu
                System.out.println("Rafraîchissement arrêté pour : " + username);
            }
        });

        // Ajouter le thread à la Map et le démarrer
        activeThreads.put(username, refreshThread);
        refreshThread.start();
    }

    // Méthode pour arrêter une tâche de rafraîchissement
    public static void stopRefreshTask(String username) {
        // Récupérer le thread de l'utilisateur
        Thread thread = activeThreads.remove(username);
        if (thread != null) {
            // Interrompre le thread
            thread.interrupt();
            System.out.println("Rafraîchissement arrêté pour : " + username);
        } else {
            System.out.println("Aucune tâche de rafraîchissement trouvée pour : " + username);
        }
    }
}

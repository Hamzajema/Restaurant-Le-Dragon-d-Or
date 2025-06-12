import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.swing.*;

// Order management with database implementation
public class CommandeManager {
    private static final String COMMANDES_TABLE = "commandes";
    private static final String COMMANDE_PRODUITS_TABLE = "commande_produits";

    public CommandeManager() {
        // Create tables if they don't exist
        createTablesIfNotExist();
    }

    private void createTablesIfNotExist() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            // Create commandes table
            String sqlCommandes = "CREATE TABLE IF NOT EXISTS " + COMMANDES_TABLE + " ("
                    + "id INT PRIMARY KEY, "
                    + "client_login VARCHAR(50) NOT NULL, "
                    + "type_commande VARCHAR(20) NOT NULL, "
                    + "etat_commande VARCHAR(20) NOT NULL, "
                    + "adresse_livraison VARCHAR(255), "
                    + "date_commande TIMESTAMP NOT NULL, "
                    + "commentaire TEXT, "
                    + "FOREIGN KEY (client_login) REFERENCES clients(login)"
                    + ")";

            stmt.executeUpdate(sqlCommandes);

            // Create commande_produits junction table
            String sqlCommandeProduits = "CREATE TABLE IF NOT EXISTS " + COMMANDE_PRODUITS_TABLE + " ("
                    + "commande_id INT, "
                    + "produit_id INT, "
                    + "quantite INT NOT NULL, "
                    + "PRIMARY KEY (commande_id, produit_id), "
                    + "FOREIGN KEY (commande_id) REFERENCES " + COMMANDES_TABLE + "(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (produit_id) REFERENCES produits(id)"
                    + ")";

            stmt.executeUpdate(sqlCommandeProduits);
            System.out.println("Tables commandes and commande_produits created or already exist");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la création des tables commandes: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, null);
        }
    }

    public void ajouterCommande(Commande c) {
        Connection conn = null;
        PreparedStatement pstmtCommande = null;
        PreparedStatement pstmtProduits = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into commandes table
            String sqlCommande = "INSERT INTO " + COMMANDES_TABLE
                    + " (id, client_login, type_commande, etat_commande, adresse_livraison, date_commande, commentaire) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmtCommande = conn.prepareStatement(sqlCommande);
            pstmtCommande.setInt(1, c.getId());
            pstmtCommande.setString(2, c.getClient().getLogin());
            pstmtCommande.setString(3, c.getTypeCommande().toString());
            pstmtCommande.setString(4, c.getEtat().toString());
            pstmtCommande.setString(5, c.getAdresseLivraison());
            pstmtCommande.setTimestamp(6, new java.sql.Timestamp(c.getDateCommande().getTime()));
            pstmtCommande.setString(7, c.getCommentaire());

            pstmtCommande.executeUpdate();

            // Insert products into commande_produits table
            String sqlProduits = "INSERT INTO " + COMMANDE_PRODUITS_TABLE
                    + " (commande_id, produit_id, quantite) VALUES (?, ?, ?)";

            pstmtProduits = conn.prepareStatement(sqlProduits);

            for (Map.Entry<Produit, Integer> entry : c.getProduits().entrySet()) {
                Produit produit = entry.getKey();
                Integer quantite = entry.getValue();

                pstmtProduits.setInt(1, c.getId());
                pstmtProduits.setInt(2, produit.getId());
                pstmtProduits.setInt(3, quantite);

                pstmtProduits.executeUpdate();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'ajout de la commande: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            closeResources(null, pstmtProduits, null);
            closeResources(null, pstmtCommande, null);
        }
    }

    public boolean modifierEtatCommande(int id, EtatCommande nouvelEtat) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE " + COMMANDES_TABLE + " SET etat_commande = ? WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nouvelEtat.toString());
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la modification de l'état de la commande: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean modifierCommentaireCommande(int id, String commentaire) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE " + COMMANDES_TABLE + " SET commentaire = ? WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, commentaire);
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la modification du commentaire de la commande: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean supprimerCommande(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            // With CASCADE delete, we only need to delete from the main table
            String sql = "DELETE FROM " + COMMANDES_TABLE + " WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la suppression de la commande: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public Commande trouverCommande(int id) {
        Connection conn = null;
        PreparedStatement pstmtCommande = null;
        PreparedStatement pstmtProduits = null;
        ResultSet rsCommande = null;
        ResultSet rsProduits = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Get commande information
            String sqlCommande = "SELECT * FROM " + COMMANDES_TABLE + " WHERE id = ?";
            pstmtCommande = conn.prepareStatement(sqlCommande);
            pstmtCommande.setInt(1, id);
            rsCommande = pstmtCommande.executeQuery();

            if (rsCommande.next()) {
                // Get products from the commande
                String sqlProduits = "SELECT p.*, cp.quantite FROM " + COMMANDE_PRODUITS_TABLE + " cp " +
                        "JOIN produits p ON cp.produit_id = p.id " +
                        "WHERE cp.commande_id = ?";

                pstmtProduits = conn.prepareStatement(sqlProduits);
                pstmtProduits.setInt(1, id);
                rsProduits = pstmtProduits.executeQuery();

                // Build the commande
                return buildCommandeFromResultSets(rsCommande, rsProduits);
            }

            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la recherche de la commande: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } finally {
            closeResources(rsProduits, pstmtProduits, null);
            closeResources(rsCommande, pstmtCommande, null);
        }
    }

    public List<Commande> getCommandes() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Commande> commandesList = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM " + COMMANDES_TABLE + " ORDER BY date_commande DESC";

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                Commande commande = trouverCommande(id); // Use existing method to get the full commande
                if (commande != null) {
                    commandesList.add(commande);
                }
            }

            return commandesList;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération des commandes: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    public List<Commande> getCommandesClient(Client client) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Commande> resultList = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM " + COMMANDES_TABLE + " WHERE client_login = ? ORDER BY date_commande DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, client.getLogin());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Commande commande = trouverCommande(id); // Use existing method to get the full commande
                if (commande != null) {
                    resultList.add(commande);
                }
            }

            return resultList;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération des commandes du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    public int getNextId() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT MAX(id) FROM " + COMMANDES_TABLE;

            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1; // Start with ID 1 if table is empty
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération du prochain ID: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return 1;
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    private Commande buildCommandeFromResultSets(ResultSet rsCommande, ResultSet rsProduits) throws SQLException {
        // Get client info
        String clientLogin = rsCommande.getString("client_login");
        ClientManager clientManager = new ClientManager();
        Client client = clientManager.trouverClient(clientLogin);

        if (client == null) {
            throw new SQLException("Client not found for login: " + clientLogin);
        }

        // Get commande details
        int id = rsCommande.getInt("id");
        String typeCommandeStr = rsCommande.getString("type_commande");
        String etatCommandeStr = rsCommande.getString("etat_commande");
        String adresseLivraison = rsCommande.getString("adresse_livraison");
        Date dateCommande = new Date(rsCommande.getTimestamp("date_commande").getTime());
        String commentaire = rsCommande.getString("commentaire");

        // Convert enum strings
        TypeCommande typeCommande = TypeCommande.fromLibelle(typeCommandeStr);
        EtatCommande etatCommande = EtatCommande.fromLibelle(etatCommandeStr);

        // Get produits
        Map<Produit, Integer> produits = new HashMap<>();
        while (rsProduits.next()) {
            // Extract produit
            int produitId = rsProduits.getInt("id");
            String nom = rsProduits.getString("nom");
            String description = rsProduits.getString("description");
            double prix = rsProduits.getDouble("prix");
            String type = rsProduits.getString("type");
            boolean disponible = rsProduits.getBoolean("disponible");

            Produit produit = new Produit(produitId, nom, description, prix, type);
            produit.setDisponible(disponible);

            // Get quantity
            int quantite = rsProduits.getInt("quantite");
            produits.put(produit, quantite);
        }

        // Create and return commande
        Commande commande = new Commande(id, client, produits, typeCommande, adresseLivraison);
        commande.setEtat(etatCommande);
        commande.setDateCreation(dateCommande);
        commande.setCommentaire(commentaire);

        return commande;
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            // We don't close the connection here as it's managed by DatabaseConnection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
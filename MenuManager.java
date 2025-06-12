import java.sql.*;
import java.util.*;
import javax.swing.*;

// Product management with database implementation
public class MenuManager {
    private static final String TABLE_NAME = "produits";

    public MenuManager() {
        // Create table if it doesn't exist
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "id INT PRIMARY KEY, "
                    + "nom VARCHAR(100) NOT NULL, "
                    + "description TEXT, "
                    + "prix DOUBLE NOT NULL, "
                    + "type VARCHAR(50) NOT NULL, "
                    + "disponible BOOLEAN NOT NULL DEFAULT TRUE, "
                    + "imageURL VARCHAR(1000) NOT NULL"
                    + ")";

            stmt.executeUpdate(sql);
            System.out.println("Table produits created or already exists");

            // Check if the table is empty, if so initialize with sample data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + TABLE_NAME);
            if (rs.next() && rs.getInt(1) == 0) {
                initSampleProducts();
            }
            rs.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la création de la table produits: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, null);
        }
    }

    public boolean ajouterProduit(Produit p) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Check if ID already exists
            if (trouverProduit(p.getId()) != null) {
                return false;
            }

            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO " + TABLE_NAME + " (id, nom, description, prix, type, disponible) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, p.getId());
            pstmt.setString(2, p.getNom());
            pstmt.setString(3, p.getDescription());
            pstmt.setDouble(4, p.getPrix());
            pstmt.setString(5, p.getType());
            pstmt.setBoolean(6, p.isDisponible());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'ajout du produit: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean modifierProduit(int id, String nouveauNom, String description, double prix, boolean disponible,
            String immageURL) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE " + TABLE_NAME + " SET nom = ?, description = ?, prix = ?, disponible = ? "
                    + "WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nouveauNom);
            pstmt.setString(2, description);
            pstmt.setDouble(3, prix);
            pstmt.setBoolean(4, disponible);
            pstmt.setInt(5, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la modification du produit: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean supprimerProduit(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la suppression du produit: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public Produit trouverProduit(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractProduitFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la recherche du produit: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    // Add this method at the class level (not inside another method)
    private Produit extractProduitFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String description = rs.getString("description");
        double prix = rs.getDouble("prix");
        String type = rs.getString("type");
        String imageUrl = rs.getString("imageURL");
        boolean disponible = rs.getBoolean("disponible");

        // Use the constructor with imageUrl parameter
        Produit produit = new Produit(id, nom, description, prix, type, imageUrl);

        // Set disponible separately since it's not in the constructor
        produit.setDisponible(disponible);

        return produit;
    }

    public List<Produit> getProduits() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Produit> produitsList = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY id";

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Produit produit = extractProduitFromResultSet(rs);
                System.out.println("Produits récupérés: " + produit);
                produitsList.add(produit);
            }

            return produitsList;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération des produits: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    public List<Produit> getProduitsByType(String type) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Produit> result = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE type = ? ORDER BY id";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Produit produit = extractProduitFromResultSet(rs);
                result.add(produit);
            }

            return result;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération des produits par type: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    public List<String> getTypes() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<String> types = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT DISTINCT type FROM " + TABLE_NAME + " ORDER BY type";

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                types.add(rs.getString("type"));
            }

            return types;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération des types de produits: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    public int getNextId() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT MAX(id) FROM " + TABLE_NAME;

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

    // private Produit extractProduitFromResultSet(ResultSet rs) throws SQLException
    // {
    // int id = rs.getInt("id");
    // String nom = rs.getString("nom");
    // String description = rs.getString("description");
    // double prix = rs.getDouble("prix");
    // String type = rs.getString("type");
    // boolean disponible = rs.getBoolean("disponible");

    // Produit produit = new Produit(id, nom, description, prix, type);
    // produit.setDisponible(disponible);
    // return produit;
    // }

    // Initialize with sample products
    private void initSampleProducts() {
        ajouterProduit(new Produit(1, "Pizza Margherita", "Tomate, mozzarella, basilic", 12.50, "Plat"));
        ajouterProduit(new Produit(2, "Pasta Carbonara", "Pâtes, crème, lardons, œuf", 10.00, "Plat"));
        ajouterProduit(new Produit(3, "Tiramisu", "Café, mascarpone, biscuits", 5.50, "Dessert"));
        ajouterProduit(new Produit(4, "Coca-Cola", "33cl", 2.00, "Boisson"));
        ajouterProduit(new Produit(5, "Eau minérale", "50cl", 1.50, "Boisson"));
        System.out.println("Sample products initialized");
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
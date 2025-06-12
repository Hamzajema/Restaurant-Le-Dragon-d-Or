import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.swing.*;

// Client management with database implementation
public class ClientManager {
    private static final String TABLE_NAME = "clients";

    public ClientManager() {
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
                    + "login VARCHAR(50) PRIMARY KEY, "
                    + "mot_de_passe VARCHAR(100) NOT NULL, "
                    + "nom VARCHAR(50) NOT NULL, "
                    + "prenom VARCHAR(50) NOT NULL, "
                    + "date_naissance DATE, "
                    + "adresse VARCHAR(255), "
                    + "telephone VARCHAR(20)"
                    + ")";

            stmt.executeUpdate(sql);
            System.out.println("Table clients created or already exists");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la création de la table clients: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, null);
        }
    }

    public boolean ajouterClient(Client c) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Check if login already exists
            if (trouverClient(c.getLogin()) != null) {
                return false;
            }

            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO " + TABLE_NAME
                    + " (login, mot_de_passe, nom, prenom, date_naissance, adresse, telephone) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getLogin());
            pstmt.setString(2, c.getMotDePasse());
            pstmt.setString(3, c.getNom());
            pstmt.setString(4, c.getPrenom());
            pstmt.setDate(5, new java.sql.Date(c.getDateNaissance().getTime()));
            pstmt.setString(6, c.getAdresse());
            pstmt.setString(7, c.getTelephone());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'ajout du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean modifierClient(String login, String nouvelleAdresse, String nouveauTel) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE " + TABLE_NAME + " SET adresse = ?, telephone = ? WHERE login = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nouvelleAdresse);
            pstmt.setString(2, nouveauTel);
            pstmt.setString(3, login);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la modification du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean changerMotDePasse(String login, String newPass) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE " + TABLE_NAME + " SET mot_de_passe = ? WHERE login = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPass);
            pstmt.setString(2, login);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors du changement de mot de passe: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public boolean supprimerClient(String login) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE login = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, login);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la suppression du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    public Client trouverClient(String login) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE login = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, login);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractClientFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la recherche du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    public Client authentifier(String login, String motDePasse) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE login = ? AND mot_de_passe = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, login);
            pstmt.setString(2, motDePasse);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractClientFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'authentification: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    public List<Client> getClients() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Client> clientsList = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM " + TABLE_NAME;

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Client client = extractClientFromResultSet(rs);
                clientsList.add(client);
            }

            return clientsList;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la récupération des clients: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    public boolean updateClient(Client client) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE " + TABLE_NAME
                    + " SET mot_de_passe = ?, nom = ?, prenom = ?, date_naissance = ?, adresse = ?, telephone = ? WHERE login = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, client.getMotDePasse());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getPrenom());
            pstmt.setDate(4, new java.sql.Date(client.getDateNaissance().getTime()));
            pstmt.setString(5, client.getAdresse());
            pstmt.setString(6, client.getTelephone());
            pstmt.setString(7, client.getLogin());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la mise à jour du client: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    private Client extractClientFromResultSet(ResultSet rs) throws SQLException {
        String login = rs.getString("login");
        String motDePasse = rs.getString("mot_de_passe");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        Date dateNaissance = rs.getDate("date_naissance");
        String adresse = rs.getString("adresse");
        String telephone = rs.getString("telephone");

        return new Client(login, motDePasse, nom, prenom, dateNaissance, adresse, telephone);
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
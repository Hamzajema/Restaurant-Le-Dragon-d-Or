import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// public class DatabaseConnection {
//     // private static final String URL =
//     // "jjdbc:mysql://localhost:3306/insurance_db?autoReconnect=true&useSSL=false";
//     // // CORRECTString
//     private static final String URL = "jdbc:mysql://localhost:3306/insurance_db?autoReconnect=true&useSSL=false"; // url
//     // =
//     // dbc:mysql://127.0.0.1:3306/insurance_db

//     private static final String USER = "root";
//     private static final String PASSWORD = "";
//     private static Connection connection;

//     // public static Connection getConnection() {
//     // if (connection == null) {
//     // try {
//     // Class.forName("com.mysql.cj.jdbc.Driver");
//     // connection = DriverManager.getConnection(URL, USER, PASSWORD);
//     // System.out.println("Database connection established");
//     // } catch (ClassNotFoundException | SQLException e) {
//     // e.printStackTrace();
//     // System.out.println("Failed to connect to database: " + e.getMessage());
//     // }
//     // }
//     // return connection;
//     // }
//     public static Connection getConnection() {
//         try {
//             if (connection == null || connection.isClosed()) {
//                 Class.forName("com.mysql.cj.jdbc.Driver");
//                 connection = DriverManager.getConnection(URL, USER, PASSWORD);
//             }
//             return connection;
//         } catch (ClassNotFoundException | SQLException e) {
//             e.printStackTrace();
//             System.out.println("Failed to connect to database: " + e.getMessage());
//             return null;
//         }
//     }

//     public static void closeConnection() {
//         if (connection != null) {
//             try {
//                 connection.close();
//                 System.out.println("Database connection closed");
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/RestaurantApp?autoReconnect=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established");
            }
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database: " + e.getMessage());
            return null;
        }
    }

    // Only call this when shutting down your application
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
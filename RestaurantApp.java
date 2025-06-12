
import java.io.*;
import javax.net.ssl.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;

enum EtatCommande {
    NON_TRAITEE("Non traitée"),
    EN_PREPARATION("En préparation"),
    PRETE("Prête"),
    EN_ROUTE("En route"),
    LIVREE("Livrée"),
    ANNULEE("Annulée");

    private final String libelle;

    EtatCommande(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }

    public static EtatCommande fromLibelle(String libelle) {
        for (EtatCommande type : EtatCommande.values()) {
            if (type.libelle.equalsIgnoreCase(libelle)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with libelle: " + libelle);
    }
}

enum TypeCommande {
    DOMICILE("Livraison à domicile"),
    SUR_PLACE("Sur place"),
    A_EMPORTER("À emporter");

    private final String libelle;

    TypeCommande(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }

    public static TypeCommande fromLibelle(String libelle) {
        for (TypeCommande type : TypeCommande.values()) {
            if (type.libelle.equalsIgnoreCase(libelle)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with libelle: " + libelle);
    }
}

class DataStorage {
    public static void save(Object obj, String filename) throws IOException {
        File file = new File(filename);
        // Create parent directories if they don't exist
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(obj);
        }
    }

    public static Object load(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }
}

class UIUtils {
    // Set look and feel
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Center a frame on screen
    public static void centerOnScreen(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dim.width - frame.getWidth()) / 2;
        int y = (dim.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    // Add a new method that works with any Window (parent of both JFrame and
    // JDialog)
    public static void centerOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - window.getWidth()) / 2;
        int y = (screenSize.height - window.getHeight()) / 2;
        window.setLocation(x, y);
    }

    // Create a standardized panel with title border
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    // Format currency
    public static String formatPrice(double price) {
        return String.format("%.2f TND", price);
    }

    public static ImageIcon createTextIcon(String text) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24)); // Emoji-friendly font
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics fm = g.getFontMetrics();
        int x = (image.getWidth() - fm.stringWidth(text)) / 2;
        int y = ((image.getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
        g.dispose();
        return new ImageIcon(image);
    }

    public static JButton createRoundButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground().darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };

        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static ImageIcon getIcon(String name, int size) {
        try {
            // Build the icon path
            String iconPath = "/icons/" + name + ".png";

            // Get the original image from resources
            java.net.URL resource = UIUtils.class.getResource(iconPath);
            if (resource == null) {
                System.err.println("Icon not found: " + iconPath);
                return createDefaultIcon(size); // Return a default icon
            }

            // Load the image
            BufferedImage originalImg = ImageIO.read(resource);

            // Scale the image if needed
            if (originalImg.getWidth() != size || originalImg.getHeight() != size) {
                BufferedImage scaledImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImg.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImg, 0, 0, size, size, null);
                g2d.dispose();
                return new ImageIcon(scaledImg);
            } else {
                return new ImageIcon(originalImg);
            }
        } catch (IOException e) {
            System.err.println("Error loading icon: " + name);
            e.printStackTrace();
            return createDefaultIcon(size); // Return a default icon
        }
    }

    /**
     * Creates a simple default icon when the requested icon cannot be found.
     * 
     * @param size Size of the icon to create
     * @return A simple default ImageIcon
     */
    private static ImageIcon createDefaultIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Draw a gray square with a red 'X'
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(0, 0, size, size);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(2, 2, size - 2, size - 2);
        g2d.drawLine(size - 2, 2, 2, size - 2);

        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Overloaded version that returns an icon with a default size of 16 pixels.
     * 
     * @param name Base name of the icon (without extension)
     * @return ImageIcon object with default size (16x16)
     */
    public static ImageIcon getIcon(String name) {
        return getIcon(name, 16); // Default size
    }
}

class SplashScreen extends JWindow {
    private final Color RED_CHINESE = new Color(190, 0, 0);
    private final Color GOLD_CHINESE = new Color(212, 175, 55);
    private final Color BG_COLOR = new Color(251, 246, 240);

    public SplashScreen() {
        setSize(400, 300);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0)); // Transparent corners
        setShape(new RoundRectangle2D.Double(0, 0, 400, 300, 30, 30));
        UIUtils.centerOnScreen(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_CHINESE, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Titre
        JLabel titleLabel = new JLabel("LE DRAGON D'OR", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(RED_CHINESE);

        // Symbole chinois
        JLabel symbolLabel = new JLabel("福", JLabel.CENTER);
        symbolLabel.setFont(new Font("SimSun", Font.BOLD, 60));
        symbolLabel.setForeground(GOLD_CHINESE);

        // Barre de chargement
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(GOLD_CHINESE);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorder(BorderFactory.createLineBorder(RED_CHINESE, 2));

        mainPanel.add(symbolLabel, BorderLayout.NORTH);
        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public void showSplash(int durationMs) {
        setVisible(true);
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setVisible(false);
        dispose();
    }

}

public class RestaurantApp {
    public static void main(String[] args) {
        // System.setProperty("https.protocols", "TLSv1.2");

        // // Désactiver la vérification SSL au démarrage
        // disableSslVerification();

        UIUtils.setLookAndFeel();
        SplashScreen splash = new SplashScreen();
        splash.showSplash(2000); // 2 secondes

        // Après splash, lancer ton LoginFrame
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame(new ClientManager(), new MenuManager(), new CommandeManager());
        });
    }

    private static void disableSslVerification() {
        try {
            // Créer un trust manager qui accepte tous les certificats
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Installer le trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Créer un host verifier qui accepte tous les hôtes
            // Utilisez la bonne interface javax.net.ssl.HostnameVerifier
            javax.net.ssl.HostnameVerifier allHostsValid = new javax.net.ssl.HostnameVerifier() {
                @Override
                public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                    return true;
                }
            };

            // Installer le host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// javac -encoding
// UTF-8-cp".;lib\mysql-connector-j-9.3.0.jar;lib\jfreechart-1.0.19.jar;lib\jcommon-1.0.23.jar"
// *.java
// javac -cp ".;lib/mysql-connector-j-9.3.0.jar" DatabaseConnection.java
// javac -encoding
// UTF-8-cp".;lib/mysql-connector-j-9.3.0.jar;lib/jfreechart-1.0.19.jar;lib/jcommon-1.0.23.jar"
// DatabaseConnection.java

// java -cp
// ".;lib/mysql-connector-j-9.3.0.jar;lib/jfreechart-1.0.19.jar;lib/jcommon-1.0.23.jar"
// RestaurantApp

// javac -encoding UTF-8 -cp
// ".;lib/mysql-connector-j-9.3.0.jar;lib/jfreechart-1.0.19.jar;lib/jcommon-1.0.23.jar"
// *.java
// javac -encoding UTF-8 -cp
// ".;lib/mysql-connector-j-9.3.0.jar;lib/jfreechart-1.0.19.jar;lib/jcommon-1.0.23.jar"
// DatabaseConnection.java

// javac -encoding UTF-8 -cp ".;lib/*" *.java
// java -cp ".;lib/*" RestaurantApp
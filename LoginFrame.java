import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;
    private ClientManager clientManager;
    private MenuManager menuManager;
    private CommandeManager commandeManager;

    private final Color RED_CHINESE = new Color(190, 0, 0);
    private final Color GOLD_CHINESE = new Color(212, 175, 55);
    private final Color DARK_RED = new Color(128, 0, 0);
    private final Color BLACK_CHINESE = new Color(25, 25, 25);
    private final Color BG_COLOR = new Color(251, 246, 240);
    private final Color FIELD_BG = new Color(255, 253, 250);
    private final Color ACCENT_COLOR = new Color(230, 211, 162);
    private final Color BUTTON_BG_GREY = new Color(230, 211, 162);

    public LoginFrame(ClientManager clientManager, MenuManager menuManager, CommandeManager commandeManager) {
        this.clientManager = clientManager;
        this.menuManager = menuManager;
        this.commandeManager = commandeManager;

        setTitle("Golden Dragon - Connexion");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 600, 700, 20, 20));
        setLayout(new BorderLayout());
        UIUtils.centerOnScreen(this);
        getContentPane().setBackground(BG_COLOR);

        Font titleFont = new Font("Arial", Font.BOLD, 26);
        Font chineseFont = new Font("Arial", Font.BOLD, 18);
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(RED_CHINESE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, GOLD_CHINESE),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        JButton closeButton = createCloseButton();
        closeButton.addActionListener(e -> System.exit(0));
        closePanel.add(closeButton);
        headerPanel.add(closePanel, BorderLayout.NORTH);

        JPanel headerContentPanel = new JPanel(new BorderLayout());
        headerContentPanel.setOpaque(false);

        JLabel chineseSymbol = new JLabel("福");
        chineseSymbol.setFont(new Font("SimSun", Font.BOLD, 48));
        chineseSymbol.setForeground(GOLD_CHINESE);
        chineseSymbol.setHorizontalAlignment(JLabel.CENTER);

        JLabel titleLabel = new JLabel("LE DRAGON D'OR");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitleLabel = new JLabel("CUISINE RAFFINÉE CHINOISE");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(ACCENT_COLOR);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        headerContentPanel.add(chineseSymbol, BorderLayout.WEST);
        headerContentPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(headerContentPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40),
                BorderFactory.createCompoundBorder(
                        new ShadowBorder(10),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                                BorderFactory.createEmptyBorder(30, 30, 30, 30)))));

        JLabel decorLabel = new JLabel(createGoldConnection());
        decorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginTitle = new JLabel("Connexion", JLabel.CENTER);
        loginTitle.setFont(chineseFont);
        loginTitle.setForeground(RED_CHINESE);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel loginPanel = createSquareIconField("Identifiant", labelFont);
        for (Component c : loginPanel.getComponents()) {
            if (c instanceof JTextField) {
                loginField = (JTextField) c;
                break;
            }
        }

        JPanel passPanel = createSquareIconField("Mot de passe", labelFont);
        for (Component c : passPanel.getComponents()) {
            if (c instanceof JPasswordField) {
                passField = (JPasswordField) c;
                break;
            }
        }

        formPanel.add(loginPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(passPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 15, 0)); // Réduit l'espace en bas
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = createRedButton("SE CONNECTER", buttonFont);
        registerButton = createOutlinedButton("S'INSCRIRE", buttonFont);

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(registerButton);

        centerPanel.add(decorLabel);
        centerPanel.add(loginTitle);
        centerPanel.add(formPanel);
        centerPanel.add(buttonPanel);
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel footerLabel = new JLabel("© 2025 LE DRAGON D'OR", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        footerLabel.setForeground(DARK_RED);
        JPanel patternPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        patternPanel.setOpaque(false);
        URL iconUrl = getClass().getResource("resources/icons/chinese-lantern.png");
        Image img = new ImageIcon(iconUrl).getImage()
                .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        for (int i = 0; i < 5; i++) {
            JLabel patternLabel = new JLabel(icon);
            patternLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            patternLabel.setForeground(GOLD_CHINESE);
            patternLabel.setHorizontalAlignment(JLabel.CENTER);
            patternPanel.add(patternLabel);
        }

        footerPanel.add(patternPanel, BorderLayout.NORTH);
        footerPanel.add(footerLabel, BorderLayout.CENTER);

        MouseAdapter moveAdapter = new MouseAdapter() {
            private int initialX;
            private int initialY;

            @Override
            public void mousePressed(MouseEvent e) {
                initialX = e.getX();
                initialY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - initialX,
                        getLocation().y + e.getY() - initialY);
            }
        };

        headerPanel.addMouseListener(moveAdapter);
        headerPanel.addMouseMotionListener(moveAdapter);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegistrationDialog());

        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    private ImageIcon createGoldConnection() {
        int size = 60;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(GOLD_CHINESE);
        g2d.setStroke(new BasicStroke(2));

        int margin = 5;
        int innerSize = size - 2 * margin;
        g2d.drawRect(margin, margin, innerSize, innerSize);

        g2d.drawLine(margin, size / 2, size - margin, size / 2);
        g2d.drawLine(size / 2, margin, size / 2, size - margin);

        g2d.drawOval(size / 3, size / 3, size / 3, size / 3);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private JPanel createSquareIconField(String labelText, Font font) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(350, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(DARK_RED);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);

        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setBackground(ACCENT_COLOR);
        iconPanel.setLayout(new BorderLayout());
        String iconPath = "/resources/icons/user.png";
        if (labelText.contains("passe")) {
            iconPath = "/resources/icons/padlock.png";
        }

        URL iconUrl = getClass().getResource(iconPath);
        Image img = new ImageIcon(iconUrl).getImage()
                .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(BLACK_CHINESE);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 16));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        JComponent field;
        if (labelText.contains("passe")) {
            field = new JPasswordField(15);
        } else {
            field = new JTextField(15);
        }

        field.setFont(font);
        field.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        field.setBackground(Color.WHITE);
        if (labelText.equals("Identifiant")) {
            loginField = (JTextField) field;
        }
        if (labelText.equals("Mot de passe")) {
            passField = (JPasswordField) field;
        }
        inputPanel.add(iconPanel, BorderLayout.WEST);
        inputPanel.add(field, BorderLayout.CENTER);

        container.add(label);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(inputPanel);

        return container;
    }

    // Bouton rouge pour Se Connecter
    private JButton createRedButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setBackground(BUTTON_BG_GREY);
        button.setForeground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RED_CHINESE, 10),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(170, 0, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(RED_CHINESE);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(140, 0, 0));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(170, 0, 0));
            }
        });

        return button;
    }

    // Bouton avec bordure pour S'inscrire
    private JButton createOutlinedButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setOpaque(true);
        button.setBackground(BUTTON_BG_GREY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(GOLD_CHINESE, 10),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(250, 245, 230));
                button.setForeground(DARK_RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(BLACK_CHINESE);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(240, 235, 220));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(250, 245, 230));
            }
        });

        return button;
    }

    // Classe pour les bordures arrondies
    private class RoundedBorder extends AbstractBorder {
        private Color color;
        private int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private void handleLogin() {
        String login = loginField.getText().trim();
        String pass = new String(passField.getPassword());

        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = clientManager.authentifier(login, pass);
        if (client != null) {
            dispose();
            if ("admin".equals(login)) {
                new AdminFrame(clientManager, menuManager, commandeManager);
            } else {
                new ClientFrame(client, menuManager, commandeManager);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Login ou mot de passe incorrect",
                    "Erreur d'authentification", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(this, title);

        dialog.getContentPane().setBackground(BG_COLOR);
        if (dialog.getContentPane() instanceof JPanel) {
            ((JPanel) dialog.getContentPane()).setBorder(
                    BorderFactory.createMatteBorder(3, 0, 0, 0, RED_CHINESE));
        }
        dialog.setVisible(true);
    }

    // private void openRegistrationDialog() {
    // JDialog dialog = new JDialog(this, "", true);
    // dialog.setSize(600, 700);
    // dialog.setUndecorated(true);
    // dialog.setShape(new RoundRectangle2D.Double(0, 0, 600, 700, 15, 15));
    // dialog.setLayout(new BorderLayout());
    // dialog.getContentPane().setBackground(BG_COLOR);
    // UIUtils.centerOnScreen(dialog);

    // Font titleFont = new Font("Arial", Font.BOLD, 20);
    // Font labelFont = new Font("Arial", Font.PLAIN, 14);
    // Font buttonFont = new Font("Arial", Font.BOLD, 14);
    // JPanel headerPanel = new JPanel(new BorderLayout());
    // headerPanel.setBackground(RED_CHINESE);
    // headerPanel.setBorder(BorderFactory.createCompoundBorder(
    // BorderFactory.createMatteBorder(0, 0, 3, 0, GOLD_CHINESE),
    // BorderFactory.createEmptyBorder(15, 20, 15, 20)));
    // JLabel titleLabel = new JLabel("Inscription", JLabel.CENTER);
    // titleLabel.setFont(titleFont);
    // titleLabel.setForeground(Color.WHITE);

    // headerPanel.add(titleLabel, BorderLayout.CENTER);
    // JButton closeButton = createCloseButton();
    // closeButton.addActionListener(e -> dialog.dispose());
    // JPanel formPanel = new JPanel();
    // formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    // formPanel.setBackground(BG_COLOR);
    // formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    // JTextField regLoginField = createStyledTextField(labelFont);
    // JPasswordField regPassField = createStyledPasswordField(labelFont);
    // JTextField nomField = createStyledTextField(labelFont);
    // JTextField prenomField = createStyledTextField(labelFont);
    // JTextField dateField = createStyledTextField(labelFont);
    // dateField.setText("JJ/MM/AAAA");
    // JTextField adresseField = createStyledTextField(labelFont);
    // JTextField telField = createStyledTextField(labelFont);
    // formPanel.add(createFormField("Identifiant", regLoginField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // formPanel.add(createFormField("Mot de passe", regPassField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // formPanel.add(createFormField("Nom", nomField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // formPanel.add(createFormField("Prénom", prenomField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // formPanel.add(createFormField("Date de naissance", dateField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // formPanel.add(createFormField("Adresse", adresseField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // formPanel.add(createFormField("Téléphone", telField, labelFont));
    // formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    // JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    // buttonPanel.setOpaque(false);
    // buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

    // JButton registerBtn = createRedButton("S'INSCRIRE", buttonFont);
    // JButton cancelBtn = createOutlinedButton("ANNULER", buttonFont);

    // buttonPanel.add(registerBtn);
    // buttonPanel.add(cancelBtn);
    // JPanel bottomPanel = new JPanel();
    // bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
    // bottomPanel.setOpaque(false);
    // bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
    // URL iconUrl = getClass().getResource("/resources/icons/chinese-lantern.png");
    // Image img = new ImageIcon(iconUrl).getImage()
    // .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
    // ImageIcon icon = new ImageIcon(img);
    // JLabel decorLabel = new JLabel(icon, JLabel.CENTER);
    // decorLabel.setFont(new Font("Arial", Font.BOLD, 14));
    // decorLabel.setForeground(GOLD_CHINESE);
    // decorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // bottomPanel.add(buttonPanel);
    // bottomPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    // bottomPanel.add(decorLabel);
    // dialog.add(headerPanel, BorderLayout.NORTH);
    // dialog.add(formPanel, BorderLayout.CENTER);
    // dialog.add(bottomPanel, BorderLayout.SOUTH);
    // MouseAdapter moveAdapter = new MouseAdapter() {
    // private int initialX;
    // private int initialY;

    // @Override
    // public void mousePressed(MouseEvent e) {
    // initialX = e.getX();
    // initialY = e.getY();
    // }

    // @Override
    // public void mouseDragged(MouseEvent e) {
    // dialog.setLocation(dialog.getLocation().x + e.getX() - initialX,
    // dialog.getLocation().y + e.getY() - initialY);
    // }
    // };

    // headerPanel.addMouseListener(moveAdapter);
    // headerPanel.addMouseMotionListener(moveAdapter);
    // regLoginField.requestFocusInWindow();
    // dateField.addFocusListener(new FocusAdapter() {
    // @Override
    // public void focusGained(FocusEvent e) {
    // if (dateField.getText().equals("JJ/MM/AAAA")) {
    // dateField.setText("");
    // }
    // }

    // @Override
    // public void focusLost(FocusEvent e) {
    // if (dateField.getText().isEmpty()) {
    // dateField.setText("JJ/MM/AAAA");
    // }
    // }
    // });
    // registerBtn.addActionListener(e -> {
    // try {
    // String login = regLoginField.getText().trim();
    // String pass = new String(regPassField.getPassword());
    // String nom = nomField.getText().trim();
    // String prenom = prenomField.getText().trim();
    // String dateStr = dateField.getText().trim();
    // String adresse = adresseField.getText().trim();
    // String tel = telField.getText().trim();
    // if (login.isEmpty() || pass.isEmpty() || nom.isEmpty() || prenom.isEmpty() ||
    // dateStr.equals("JJ/MM/AAAA") || adresse.isEmpty() || tel.isEmpty()) {
    // showStyledMessage("Veuillez remplir tous les champs", "Erreur",
    // JOptionPane.ERROR_MESSAGE);
    // return;
    // }
    // // Vérification si 'tel' ne contient pas uniquement des chiffres
    // if (!tel.matches("\\d+")) {
    // showStyledMessage("Le numéro de téléphone doit contenir uniquement des
    // chiffres", "Erreur",
    // JOptionPane.ERROR_MESSAGE);
    // return;
    // }
    // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    // Date dateNaissance = sdf.parse(dateStr);
    // if (clientManager.trouverClient(login) != null) {
    // showStyledMessage("Ce login existe déjà", "Erreur",
    // JOptionPane.ERROR_MESSAGE);
    // return;
    // }
    // Client newClient = new Client(login, pass, nom, prenom, dateNaissance,
    // adresse, tel);
    // if (clientManager.ajouterClient(newClient)) {
    // showStyledMessage("Inscription réussie. Vous pouvez maintenant vous
    // connecter.",
    // "Succès", JOptionPane.INFORMATION_MESSAGE);
    // dialog.dispose();
    // } else {
    // showStyledMessage("Erreur lors de l'inscription", "Erreur",
    // JOptionPane.ERROR_MESSAGE);
    // }
    // } catch (Exception ex) {
    // showStyledMessage("Format de date invalide. Utilisez JJ/MM/AAAA", "Erreur",
    // JOptionPane.ERROR_MESSAGE);
    // }
    // });

    // cancelBtn.addActionListener(e -> dialog.dispose());

    // dialog.setVisible(true);
    // }

    private void openRegistrationDialog() {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setSize(600, 700);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 600, 700, 15, 15));
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        UIUtils.centerOnScreen(dialog);

        Font titleFont = new Font("Arial", Font.BOLD, 20);
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(RED_CHINESE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, GOLD_CHINESE),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        JLabel titleLabel = new JLabel("Inscription", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        JButton closeButton = createCloseButton();
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JTextField regLoginField = createStyledTextField(labelFont);
        JPasswordField regPassField = createStyledPasswordField(labelFont);
        JTextField nomField = createStyledTextField(labelFont);
        JTextField prenomField = createStyledTextField(labelFont);
        JTextField dateField = createStyledTextField(labelFont);
        dateField.setText("JJ/MM/AAAA");
        JTextField adresseField = createStyledTextField(labelFont);
        JTextField telField = createStyledTextField(labelFont);

        formPanel.add(
                createFormFieldWithDelete("Identifiant", regLoginField, labelFont, e -> regLoginField.setText("")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel
                .add(createFormFieldWithDelete("Mot de passe", regPassField, labelFont, e -> regPassField.setText("")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormFieldWithDelete("Nom", nomField, labelFont, e -> nomField.setText("")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormFieldWithDelete("Prénom", prenomField, labelFont, e -> prenomField.setText("")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormFieldWithDelete("Date de naissance", dateField, labelFont,
                e -> dateField.setText("JJ/MM/AAAA")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormFieldWithDelete("Adresse", adresseField, labelFont, e -> adresseField.setText("")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormFieldWithDelete("Téléphone", telField, labelFont, e -> telField.setText("")));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton registerBtn = createRedButton("S'INSCRIRE", buttonFont);
        JButton cancelBtn = createOutlinedButton("ANNULER", buttonFont);

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        URL iconUrl = getClass().getResource("/resources/icons/chinese-lantern.png");
        Image img = new ImageIcon(iconUrl).getImage()
                .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        JLabel decorLabel = new JLabel(icon, JLabel.CENTER);
        decorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        decorLabel.setForeground(GOLD_CHINESE);
        decorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(buttonPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bottomPanel.add(decorLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        MouseAdapter moveAdapter = new MouseAdapter() {
            private int initialX;
            private int initialY;

            @Override
            public void mousePressed(MouseEvent e) {
                initialX = e.getX();
                initialY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                dialog.setLocation(dialog.getLocation().x + e.getX() - initialX,
                        dialog.getLocation().y + e.getY() - initialY);
            }
        };

        headerPanel.addMouseListener(moveAdapter);
        headerPanel.addMouseMotionListener(moveAdapter);
        regLoginField.requestFocusInWindow();

        dateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dateField.getText().equals("JJ/MM/AAAA")) {
                    dateField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dateField.getText().isEmpty()) {
                    dateField.setText("JJ/MM/AAAA");
                }
            }
        });

        registerBtn.addActionListener(e -> {
            try {
                String login = regLoginField.getText().trim();
                String pass = new String(regPassField.getPassword());
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String dateStr = dateField.getText().trim();
                String adresse = adresseField.getText().trim();
                String tel = telField.getText().trim();
                if (login.isEmpty() || pass.isEmpty() || nom.isEmpty() || prenom.isEmpty() ||
                        dateStr.equals("JJ/MM/AAAA") || adresse.isEmpty() || tel.isEmpty()) {
                    showStyledMessage("Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!tel.matches("\\d+")) {
                    showStyledMessage("Le numéro de téléphone doit contenir uniquement des chiffres", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dateNaissance = sdf.parse(dateStr);
                if (clientManager.trouverClient(login) != null) {
                    showStyledMessage("Ce login existe déjà", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Client newClient = new Client(login, pass, nom, prenom, dateNaissance, adresse, tel);
                if (clientManager.ajouterClient(newClient)) {
                    showStyledMessage("Inscription réussie. Vous pouvez maintenant vous connecter.",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    showStyledMessage("Erreur lors de l'inscription", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                showStyledMessage("Format de date invalide. Utilisez JJ/MM/AAAA", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Helper method to create form field with delete icon
    private JPanel createFormFieldWithDelete(String labelText, JComponent field, Font font,
            ActionListener deleteAction) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(Color.BLACK);

        JButton deleteBtn = new JButton();
        deleteBtn.setPreferredSize(new Dimension(20, 20));
        deleteBtn.setBorder(BorderFactory.createEmptyBorder());
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false);

        URL deleteIconUrl = getClass().getResource("/resources/icons/delete.png");
        if (deleteIconUrl != null) {
            ImageIcon deleteIcon = new ImageIcon(deleteIconUrl);
            deleteBtn.setIcon(deleteIcon);
        } else {
            deleteBtn.setText("X");
        }

        deleteBtn.addActionListener(deleteAction);

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(label, BorderLayout.WEST);
        labelPanel.add(deleteBtn, BorderLayout.EAST);

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(GOLD_CHINESE);
        closeButton.setBackground(RED_CHINESE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(DARK_RED);
                closeButton.setForeground(Color.WHITE);
                closeButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD_CHINESE, 1),
                        BorderFactory.createEmptyBorder(1, 11, 1, 11)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(RED_CHINESE);
                closeButton.setForeground(GOLD_CHINESE);
                closeButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                closeButton.setBackground(new Color(100, 0, 0));
            }
        });

        return closeButton;
    }

    private JTextField createStyledTextField(Font font) {
        JTextField field = new JTextField(15);
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPasswordField createStyledPasswordField(Font font) {
        JPasswordField field = new JPasswordField(15);
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPanel createFormField(String labelText, JComponent field, Font font) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(400, 65));

        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(DARK_RED);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    // Classe pour créer une bordure avec ombre
    private class ShadowBorder extends AbstractBorder {
        private int shadowSize;

        public ShadowBorder(int size) {
            this.shadowSize = size;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < shadowSize; i++) {
                float opacity = 0.1f - (i * 0.01f);
                if (opacity < 0)
                    opacity = 0;

                g2.setColor(new Color(0, 0, 0, opacity));
                g2.drawRoundRect(x + i, y + i, width - i * 2, height - i * 2, 10, 10);
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
    }

}
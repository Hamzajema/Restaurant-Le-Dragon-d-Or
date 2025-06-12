import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.print.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

// Improved Admin interface with modern design
public class AdminFrame extends JFrame {
    private static final Color RED_CHINESE = new Color(204, 46, 46);
    private static final Color DARK_RED = new Color(153, 0, 0);
    private static final Color GOLD_CHINESE = new Color(255, 215, 0);
    private static final Color LIGHT_GOLD = new Color(255, 239, 204);
    private static final Color BG_COLOR = new Color(248, 248, 248);
    private static final Color BORDER_GOLD = new Color(230, 211, 162);
    private final Color FIELD_BG = new Color(255, 253, 250);
    private final Color ACCENT_COLOR = new Color(230, 211, 162);
    private final Color BUTTON_BG_GREY = new Color(230, 211, 162);
    private static final Color PRIMARY_COLOR = new Color(230, 211, 162);
    private final Color BLACK_CHINESE = new Color(25, 25, 25);

    private static final Color TEXT_COLOR = new Color(25, 25, 25);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 12);

    private ClientManager clientManager;
    private MenuManager menuManager;
    private CommandeManager commandeManager;
    private JLabel statusLabel;
    private boolean isFullscreen = false;
    private Rectangle normalBounds;

    public AdminFrame(ClientManager clientManager, MenuManager menuManager, CommandeManager commandeManager) {
        this.clientManager = clientManager;
        this.menuManager = menuManager;
        this.commandeManager = commandeManager;

        setTitle("Restaurant Manager - Tableau de bord");
        setSize(900, 650);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 650, 20, 20));
        setLayout(new BorderLayout());
        UIUtils.centerOnScreen(this);

        getContentPane().setBackground(BG_COLOR);

        setupUIDefaults();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(RED_CHINESE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, GOLD_CHINESE),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        closePanel.setOpaque(false);

        JButton closeButton = createCloseButton();
        closeButton.addActionListener(e -> System.exit(0));

        JButton fullScrButton = fullScrenButton();
        fullScrButton.addActionListener(e -> toggleFullscreen());

        closePanel.add(fullScrButton);
        closePanel.add(closeButton);

        headerPanel.add(closePanel, BorderLayout.NORTH);

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);

        JLabel chineseSymbol = new JLabel("龙");
        chineseSymbol.setFont(new Font("SimSun", Font.BOLD, 48));
        chineseSymbol.setForeground(GOLD_CHINESE);
        chineseSymbol.setHorizontalAlignment(JLabel.CENTER);

        JLabel titleLabel = new JLabel("TABLEAU DE BORD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitle = new JLabel("Administration - Le Dragon d'Or");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(ACCENT_COLOR);
        subtitle.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitle);

        headerContent.add(chineseSymbol, BorderLayout.WEST);
        headerContent.add(titlePanel, BorderLayout.CENTER);
        JPanel headerMainContent = new JPanel(new BorderLayout());
        headerMainContent.setOpaque(false);

        headerMainContent.add(headerContent, BorderLayout.CENTER);
        JButton logoutBtn = createChineseStyleButton("Déconnexion", RED_CHINESE);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(clientManager, menuManager, commandeManager);
        });
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setOpaque(false);
        logoutBtn.setPreferredSize(new Dimension(120, 30));
        logoutPanel.add(logoutBtn);

        headerMainContent.add(logoutPanel, BorderLayout.EAST);

        headerPanel.add(headerMainContent, BorderLayout.CENTER);

        MouseAdapter dragAdapter = new MouseAdapter() {
            private int mouseX, mouseY;

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
        };
        headerPanel.addMouseListener(dragAdapter);
        headerPanel.addMouseMotionListener(dragAdapter);

        JPanel tabbedPane = createStyledTabbedPaneWrapper();
        add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel footerLabel = new JLabel("© 2025 LE DRAGON D'OR", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        footerLabel.setForeground(DARK_RED);

        footerPanel.add(footerLabel, BorderLayout.CENTER);

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setupUIDefaults() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        UIManager.put("Table.alternateRowColor", new Color(240, 240, 240));
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("RESTAURANT MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userIcon = new JLabel("\uD83D\uDC64");
        userIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        userIcon.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("Admin");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 14));
        adminLabel.setForeground(Color.WHITE);

        JButton logoutBtn = createChineseStyleButton("Déconnexion", RED_CHINESE);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(clientManager, menuManager, commandeManager);
        });

        userPanel.add(userIcon);
        userPanel.add(adminLabel);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(logoutBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(FIELD_BG);
        tabbedPane.setForeground(DARK_RED);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        UIManager.put("TabbedPane.selected", BG_COLOR);
        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("TabbedPane.tabsOverlapBorder", true);

        JPanel clientPanel = createClientPanel();
        tabbedPane.addTab("Clients", loadIcon("/resources/icons/user.png"), clientPanel);

        JPanel menuPanel = createMenuPanel();
        tabbedPane.addTab("Menu", loadIcon("/resources/icons/menu.png"), menuPanel);

        JPanel orderPanel = createOrderPanel();
        tabbedPane.addTab("Commandes", loadIcon("/resources/icons/commande.png"), orderPanel);

        JPanel dashboardPanel = new DashboardPanel(clientManager, commandeManager, menuManager);
        JScrollPane scrollPane = new JScrollPane(dashboardPanel);
        tabbedPane.addTab("Tableau de bord", loadIcon("/resources/icons/dashboard.png"), scrollPane);

        return tabbedPane;
    }

    private ImageIcon loadIcon(String path) {
        URL iconUrl = getClass().getResource(path);
        if (iconUrl == null) {
            System.err.println("Icon not found: " + path);
            return null;
        }
        Image image = new ImageIcon(iconUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(new Color(236, 240, 241));

        statusLabel = new JLabel("Connecté en tant qu'administrateur");
        statusLabel.setFont(NORMAL_FONT);

        JLabel timeLabel = new JLabel(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        timeLabel.setFont(NORMAL_FONT);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);

        return statusBar;
    }

    private JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(5),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD_CHINESE, 1),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(color, 2),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15)))));

        JLabel decorationLabel = new JLabel("龍");
        decorationLabel.setFont(new Font("SimSun", Font.BOLD, 18));
        decorationLabel.setForeground(new Color(230, 211, 162, 128));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 14));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Serif", Font.BOLD, 24));
        valueLabel.setForeground(color);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(decorationLabel, BorderLayout.EAST);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTextField searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher un client...");

        JButton searchBtn = createChineseStyleButton("Rechercher", DARK_RED);

        searchPanel.add(new JLabel("Recherche:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        String[] columns = { "Login", "Nom", "Prénom", "Téléphone", "Adresse" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable clientTable = new JTable(model);
        configureTable(clientTable);

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 211, 162)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton refreshBtn = createChineseStyleButton("Actualiser", ACCENT_COLOR);
        JButton detailsBtn = createChineseStyleButton("Détails", RED_CHINESE);
        JButton deleteBtn = createChineseStyleButton("Supprimer", DARK_RED);
        JButton registerButton = createChineseStyleButton("S'INSCRIRE", BUTTON_BG_GREY);

        controlPanel.add(refreshBtn);
        controlPanel.add(detailsBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(registerButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        loadClientData(model);

        refreshBtn.addActionListener(e -> loadClientData(model));
        registerButton.addActionListener(e -> {
            openRegistrationDialog();
            loadClientData(model);
        });

        searchBtn.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                loadClientData(model);
            } else {
                model.setRowCount(0);
                for (Client client : clientManager.getClients()) {
                    if (client.getLogin().toLowerCase().contains(searchTerm) ||
                            client.getNom().toLowerCase().contains(searchTerm) ||
                            client.getPrenom().toLowerCase().contains(searchTerm)) {

                        model.addRow(new Object[] {
                                client.getLogin(),
                                client.getNom(),
                                client.getPrenom(),
                                client.getTelephone(),
                                client.getAdresse()
                        });
                    }
                }
            }
        });

        detailsBtn.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow >= 0) {
                String login = (String) model.getValueAt(selectedRow, 0);
                Client client = clientManager.trouverClient(login);
                if (client != null) {
                    showClientDetails(client);
                }
            } else {
                showInfoMessage("Veuillez sélectionner un client");
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow >= 0) {
                String login = (String) model.getValueAt(selectedRow, 0);

                if ("admin".equals(login)) {
                    showErrorMessage("Impossible de supprimer le compte administrateur");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce client ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (clientManager.supprimerClient(login)) {
                        loadClientData(model);
                        showSuccessMessage("Client supprimé avec succès");
                    } else {
                        showErrorMessage("Erreur lors de la suppression du client");
                    }
                }
            } else {
                showInfoMessage("Veuillez sélectionner un client");
            }
        });

        return panel;
    }

    private void loadClientData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Client client : clientManager.getClients()) {
            model.addRow(new Object[] {
                    client.getLogin(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getTelephone(),
                    client.getAdresse()
            });
        }
    }

    private void showClientDetails(Client client) {
        // Colors for Chinese theme
        Color chineseRed = new Color(204, 46, 46); // Main red
        Color chineseGold = new Color(255, 215, 0); // Gold accent
        Color lightGold = new Color(255, 239, 204); // Light gold for fields
        Color darkRed = new Color(153, 0, 0); // Dark red for buttons hover

        JDialog dialog = new JDialog(this, "Détails du client", true);
        dialog.setSize(500, 420);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);
        UIUtils.centerOnScreen(dialog);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(chineseRed);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel dragonIcon = new JLabel("龍");
        dragonIcon.setFont(new Font("SimSun", Font.BOLD, 24));
        dragonIcon.setForeground(chineseGold);
        headerPanel.add(dragonIcon, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Client : " + client.getPrenom() + " " + client.getNom());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(true);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(8),
                BorderFactory.createLineBorder(chineseGold, 2)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 5, 5);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = client.getDateNaissance() != null ? sdf.format(client.getDateNaissance()) : "N/A";

        addFormField(formPanel, gc, "Login :", client.getLogin(), 0, chineseRed, lightGold);
        addFormField(formPanel, gc, "Nom :", client.getNom(), 1, chineseRed, lightGold);
        addFormField(formPanel, gc, "Prénom :", client.getPrenom(), 2, chineseRed, lightGold);
        addFormField(formPanel, gc, "Date de naissance :", dateStr, 3, chineseRed, lightGold);

        gc.gridx = 0;
        gc.gridy = 4;
        formPanel.add(createStyledLabel("Adresse :", chineseRed), gc);

        gc.gridx = 1;
        JTextField adresseField = new JTextField(client.getAdresse(), 20);
        adresseField.setFont(NORMAL_FONT);
        adresseField.setBackground(lightGold);
        adresseField.setBorder(BorderFactory.createLineBorder(chineseRed, 1));
        formPanel.add(adresseField, gc);

        gc.gridx = 0;
        gc.gridy = 5;
        formPanel.add(createStyledLabel("Téléphone :", chineseRed), gc);

        gc.gridx = 1;
        JTextField telField = new JTextField(client.getTelephone(), 20);
        telField.setFont(NORMAL_FONT);
        telField.setBackground(lightGold);
        telField.setBorder(BorderFactory.createLineBorder(chineseRed, 1));
        formPanel.add(telField, gc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JButton saveBtn = createChineseStyleButton("Enregistrer", chineseRed);
        JButton cancelBtn = createChineseStyleButton("Annuler", lightGold);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            String adresse = adresseField.getText().trim();
            String tel = telField.getText().trim();
            if (adresse.isEmpty() || tel.isEmpty()) {
                showErrorMessage("Veuillez remplir tous les champs");
                return;
            }

            if (clientManager.modifierClient(client.getLogin(), adresse, tel)) {
                showSuccessMessage("Client modifié avec succès");
                dialog.dispose();
            } else {
                showErrorMessage("Erreur lors de la modification du client");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // Méthode améliorée pour créer des labels stylisés
    private JLabel createStyledLabel(String text, Color textColor) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, 14));
        label.setForeground(textColor);
        return label;
    }

    // Méthode améliorée pour ajouter des champs avec style chinois
    private void addFormField(JPanel panel, GridBagConstraints gc, String labelText,
            String value, int row, Color borderColor, Color bgColor) {
        gc.gridx = 0;
        gc.gridy = row;
        panel.add(createStyledLabel(labelText, borderColor), gc);

        gc.gridx = 1;
        JTextField field = new JTextField(value, 20);
        field.setFont(NORMAL_FONT);
        field.setEditable(false);
        field.setBackground(bgColor);
        field.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        panel.add(field, gc);
    }

    // Méthode pour créer des boutons avec style chinois
    private JButton createChineseStyleButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_CHINESE, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Méthode pour afficher des messages d'erreur stylisés
    private void addFormField(JPanel panel, GridBagConstraints gc, String labelText, String value, int row) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(TITLE_FONT);
        panel.add(label, gc);

        gc.gridx = 1;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(NORMAL_FONT);
        panel.add(valueLabel, gc);
    }

    private JPanel createMenuPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel categoriesPanel = new JPanel(new BorderLayout());
        categoriesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 10),
                        BorderFactory.createLineBorder(BORDER_GOLD, 2)),
                "Catégories",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Serif", Font.BOLD, 16),
                RED_CHINESE));
        categoriesPanel.setBackground(Color.WHITE);

        DefaultListModel<String> categoriesModel = new DefaultListModel<>();
        JList<String> categoriesList = new JList<>(categoriesModel);
        categoriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriesList.setFont(new Font("Serif", Font.PLAIN, 14));
        categoriesList.setFixedCellHeight(30);
        categoriesList.setSelectionBackground(RED_CHINESE);
        categoriesList.setSelectionForeground(Color.WHITE);

        JScrollPane categoriesScroll = new JScrollPane(categoriesList);
        categoriesScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        categoriesPanel.add(categoriesScroll, BorderLayout.CENTER);

        JPanel categoryButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        categoryButtonsPanel.setOpaque(false);
        categoryButtonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JButton addCategoryBtn = createChineseStyleButton("Ajouter", RED_CHINESE);
        JButton deleteCategoryBtn = createChineseStyleButton("Supprimer", new Color(153, 0, 0));

        categoryButtonsPanel.add(addCategoryBtn);
        categoryButtonsPanel.add(deleteCategoryBtn);

        addCategoryBtn.addActionListener(e -> {
            String newCategory = JOptionPane.showInputDialog(this,
                    "Entrez le nom de la nouvelle catégorie:",
                    "Ajouter une catégorie", JOptionPane.QUESTION_MESSAGE);

            if (newCategory != null && !newCategory.trim().isEmpty()) {
                boolean exists = false;
                for (int i = 0; i < categoriesModel.size(); i++) {
                    if (categoriesModel.get(i).equalsIgnoreCase(newCategory.trim())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    int nextId = menuManager.getNextId();
                    Produit newProduct = new Produit(nextId,
                            "Nouveau produit",
                            "Description",
                            0.0,
                            newCategory.trim());
                    menuManager.ajouterProduit(newProduct);
                    loadCategories(categoriesModel);
                    categoriesList.setSelectedValue(newCategory.trim(), true);
                    showSuccessMessage("Catégorie ajoutée avec succès");
                } else {
                    showErrorMessage("Cette catégorie existe déjà");
                }
            }
        });

        // New delete category functionality
        deleteCategoryBtn.addActionListener(e -> {
            String selectedCategory = categoriesList.getSelectedValue();
            if (selectedCategory != null) {
                // Count products in this category
                int productCount = 0;
                for (Produit produit : menuManager.getProduits()) {
                    if (produit.getType().equals(selectedCategory)) {
                        productCount++;
                    }
                }

                String message;
                if (productCount > 0) {
                    message = "Cette catégorie contient " + productCount + " produit(s).\n" +
                            "Êtes-vous sûr de vouloir supprimer cette catégorie ?\n" +
                            "(Tous les produits de cette catégorie seront également supprimés)";
                } else {
                    message = "Êtes-vous sûr de vouloir supprimer cette catégorie ?";
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        message,
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove all products in this category
                    menuManager.getProduits().removeIf(produit -> produit.getType().equals(selectedCategory));

                    loadCategories(categoriesModel);
                    showSuccessMessage("Catégorie '" + selectedCategory + "' supprimée avec succès");
                    statusLabel.setText("Catégorie supprimée");

                    // Clear products table
                    DefaultTableModel productsModel = (DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) ((JSplitPane) panel
                            .getComponent(0)).getRightComponent())
                            .getComponent(0)).getViewport().getView()).getModel();
                    productsModel.setRowCount(0);
                }
            } else {
                showInfoMessage("Veuillez sélectionner une catégorie à supprimer");
            }
        });

        categoriesPanel.add(categoryButtonsPanel, BorderLayout.SOUTH);
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 10, 0, 0),
                        BorderFactory.createLineBorder(BORDER_GOLD, 2)),
                "Produits",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Serif", Font.BOLD, 16),
                RED_CHINESE));
        productsPanel.setBackground(Color.WHITE);

        String[] columns = { "ID", "Nom", "Prix", "Disponible" };
        DefaultTableModel productsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable productsTable = new JTable(productsModel);
        configureTable(productsTable);
        JTableHeader header = productsTable.getTableHeader();
        header.setBackground(RED_CHINESE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Serif", Font.BOLD, 12));

        JScrollPane productsScroll = new JScrollPane(productsTable);
        productsScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        productsPanel.add(productsScroll, BorderLayout.CENTER);

        JPanel productButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        productButtonsPanel.setOpaque(false);
        productButtonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JButton addProductBtn = createChineseStyleButton("Ajouter", RED_CHINESE);
        JButton editProductBtn = createChineseStyleButton("Modifier", DARK_RED);
        JButton deleteProductBtn = createChineseStyleButton("Supprimer", new Color(153, 0, 0));

        productButtonsPanel.add(addProductBtn);
        productButtonsPanel.add(editProductBtn);
        productButtonsPanel.add(deleteProductBtn);
        productsPanel.add(productButtonsPanel, BorderLayout.SOUTH);
        addProductBtn.addActionListener(e -> {
            String selectedCategory = categoriesList.getSelectedValue();
            if (selectedCategory != null) {
                showProductDialog(null, selectedCategory);
                loadProductsByCategory(productsModel, selectedCategory);
            } else {
                showInfoMessage("Veuillez sélectionner une catégorie");
            }
        });

        editProductBtn.addActionListener(e -> {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int productId = Integer.parseInt(productsModel.getValueAt(selectedRow, 0).toString());
                Produit product = menuManager.trouverProduit(productId);
                if (product != null) {
                    showProductDialog(product, product.getType());
                    String selectedCategory = categoriesList.getSelectedValue();
                    loadProductsByCategory(productsModel, selectedCategory);
                }
            } else {
                showInfoMessage("Veuillez sélectionner un produit");
            }
        });

        deleteProductBtn.addActionListener(e -> {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int productId = Integer.parseInt(productsModel.getValueAt(selectedRow, 0).toString());

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce produit ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (menuManager.supprimerProduit(productId)) {
                        String selectedCategory = categoriesList.getSelectedValue();
                        loadProductsByCategory(productsModel, selectedCategory);
                        showSuccessMessage("Produit supprimé avec succès");
                    } else {
                        showErrorMessage("Erreur lors de la suppression du produit");
                    }
                }
            } else {
                showInfoMessage("Veuillez sélectionner un produit");
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                categoriesPanel, productsPanel);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(5);
        categoriesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedCategory = categoriesList.getSelectedValue();
                if (selectedCategory != null) {
                    loadProductsByCategory(productsModel, selectedCategory);
                    statusLabel.setText("Catégorie sélectionnée : " + selectedCategory);
                }
            }
        });

        panel.add(splitPane, BorderLayout.CENTER);

        loadCategories(categoriesModel);

        return panel;
    }

    private void loadCategories(DefaultListModel<String> model) {
        model.removeAllElements();
        for (String type : menuManager.getTypes()) {
            model.addElement(type);
        }
    }

    private void loadProductsByCategory(DefaultTableModel model, String category) {
        model.setRowCount(0);
        for (Produit p : menuManager.getProduitsByType(category)) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getNom(),
                    UIUtils.formatPrice(p.getPrix()),
                    p.isDisponible() ? "Oui" : "Non"
            });
        }
    }

    private void showProductDialog(Produit product, String category) {
        boolean isNewProduct = (product == null);

        JDialog dialog = new JDialog(this,
                isNewProduct ? "Ajouter un produit" : "Modifier un produit", true);
        dialog.setSize(450, 400);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        UIUtils.centerOnScreen(dialog);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel(isNewProduct ? "Nouveau produit" : "Modifier le produit");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0;
        gc.gridy = 0;
        JLabel catLabel = new JLabel("Catégorie:");
        catLabel.setFont(TITLE_FONT);
        formPanel.add(catLabel, gc);

        gc.gridx = 1;
        JLabel catValue = new JLabel(category);
        catValue.setFont(NORMAL_FONT);
        formPanel.add(catValue, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(TITLE_FONT);
        formPanel.add(nomLabel, gc);

        gc.gridx = 1;
        JTextField nomField = new JTextField(isNewProduct ? "" : product.getNom(), 20);
        nomField.setFont(NORMAL_FONT);
        formPanel.add(nomField, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(TITLE_FONT);
        formPanel.add(descLabel, gc);

        gc.gridx = 1;
        gc.gridheight = 2;
        JTextArea descField = new JTextArea(isNewProduct ? "" : product.getDescription(), 3, 20);
        descField.setFont(NORMAL_FONT);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descField);
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 211, 162)));
        formPanel.add(descScroll, gc);
        gc.gridheight = 1;

        gc.gridx = 0;
        gc.gridy = 4;
        JLabel prixLabel = new JLabel("Prix:");
        prixLabel.setFont(TITLE_FONT);
        formPanel.add(prixLabel, gc);

        gc.gridx = 1;
        JTextField prixField = new JTextField(
                isNewProduct ? "0.00" : String.format("%.2f", product.getPrix()), 20);
        prixField.setFont(NORMAL_FONT);
        formPanel.add(prixField, gc);
        gc.gridx = 0;
        gc.gridy = 6;
        JLabel urlLabel = new JLabel("Image URL:");
        urlLabel.setFont(TITLE_FONT);
        formPanel.add(urlLabel, gc);

        gc.gridx = 1;
        JTextField urlField = new JTextField(
                isNewProduct ? "" : (product.getImageUrl() != null ? product.getImageUrl() : ""), 20);
        urlField.setFont(NORMAL_FONT);
        formPanel.add(urlField, gc);

        gc.gridx = 0;
        gc.gridy = 7;
        JLabel dispLabel = new JLabel("Disponible:");
        dispLabel.setFont(TITLE_FONT);
        formPanel.add(dispLabel, gc);

        gc.gridx = 1;
        JCheckBox dispCheck = new JCheckBox();
        dispCheck.setSelected(isNewProduct || product.isDisponible());
        dispCheck.setOpaque(false);
        formPanel.add(dispCheck, gc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton saveBtn = createChineseStyleButton("Enregistrer", DARK_RED);
        JButton cancelBtn = createChineseStyleButton("Annuler", new Color(230, 211, 162));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            try {
                String nom = nomField.getText().trim();
                String description = descField.getText().trim();
                double prix = Double.parseDouble(prixField.getText().replace(',', '.'));
                boolean disponible = dispCheck.isSelected();
                String imageUrl = urlField.getText().trim();

                if (nom.isEmpty() || description.isEmpty()) {
                    showErrorMessage("Veuillez remplir tous les champs");
                    return;
                }

                if (prix < 0) {
                    showErrorMessage("Le prix ne peut pas être négatif");
                    return;
                }

                if (isNewProduct) {
                    int nextId = menuManager.getNextId();
                    Produit newProduct = new Produit(nextId, nom, description, prix, category);
                    newProduct.setDisponible(disponible);
                    newProduct.setImageUrl(imageUrl);
                    menuManager.ajouterProduit(newProduct);
                    showSuccessMessage("Produit ajouté avec succès");
                } else {
                    menuManager.modifierProduit(
                            product.getId(),
                            nom,
                            description,
                            prix,
                            disponible,
                            imageUrl);
                    showSuccessMessage("Produit modifié avec succès");
                }

                dialog.dispose();
            } catch (NumberFormatException ex) {
                showErrorMessage("Format de prix invalide");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JComboBox<EtatCommande> etatFilter = new JComboBox<>(EtatCommande.values());
        etatFilter.insertItemAt(null, 0);
        etatFilter.setSelectedIndex(0);
        etatFilter.setFont(NORMAL_FONT);
        ((JLabel) etatFilter.getRenderer()).setFont(NORMAL_FONT);

        JButton filterBtn = createChineseStyleButton("Filtrer", DARK_RED);
        JButton clearBtn = createChineseStyleButton("Effacer", new Color(230, 211, 162));

        filterPanel.add(new JLabel("État:"));
        filterPanel.add(etatFilter);
        filterPanel.add(filterBtn);
        filterPanel.add(clearBtn);

        String[] columns = { "ID", "Client", "Type", "État", "Date", "Total" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable orderTable = new JTable(model);
        configureTable(orderTable);

        orderTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (value != null) {
                    EtatCommande etat = (EtatCommande) value;
                    switch (etat) {
                        case NON_TRAITEE:
                            setForeground(new Color(52, 152, 219));
                            break;
                        case EN_PREPARATION:
                            setForeground(new Color(243, 156, 18));
                            break;
                        case PRETE:
                            setForeground(new Color(46, 204, 113));
                            break;
                        case EN_ROUTE:
                            setForeground(new Color(155, 89, 182));
                            break;
                        case LIVREE:
                            setForeground(new Color(39, 174, 96));
                            break;
                        case ANNULEE:
                            setForeground(new Color(231, 76, 60));
                            break;
                        default:
                            setForeground(TEXT_COLOR);
                    }
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 211, 162)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton refreshBtn = createChineseStyleButton("Actualiser", DARK_RED);
        JButton detailsBtn = createChineseStyleButton("Détails", DARK_RED);
        JButton changeStateBtn = createChineseStyleButton("Changer l'état", DARK_RED);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(detailsBtn);
        buttonPanel.add(changeStateBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadOrderData(model, null);

        refreshBtn.addActionListener(e -> {
            EtatCommande etat = (EtatCommande) etatFilter.getSelectedItem();
            loadOrderData(model, etat);
        });

        filterBtn.addActionListener(e -> {
            EtatCommande etat = (EtatCommande) etatFilter.getSelectedItem();
            loadOrderData(model, etat);
            statusLabel.setText("Commandes filtrées par état: " +
                    (etat != null ? etat.toString() : "Tous"));
        });

        clearBtn.addActionListener(e -> {
            etatFilter.setSelectedIndex(0);
            loadOrderData(model, null);
            statusLabel.setText("Filtre effacé - Affichage de toutes les commandes");
        });

        detailsBtn.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0) {
                int orderId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                Commande commande = commandeManager.trouverCommande(orderId);
                if (commande != null) {
                    showOrderDetails(commande);
                }
            } else {
                showInfoMessage("Veuillez sélectionner une commande");
            }
        });

        changeStateBtn.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0) {
                int orderId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                Commande commande = commandeManager.trouverCommande(orderId);
                if (commande != null) {
                    showChangeOrderStateDialog(commande, model, (EtatCommande) etatFilter.getSelectedItem());
                }
            } else {
                showInfoMessage("Veuillez sélectionner une commande");
            }
        });

        return panel;
    }

    private void loadOrderData(DefaultTableModel model, EtatCommande filter) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Commande c : commandeManager.getCommandes()) {
            if (filter == null || c.getEtat() == filter) {
                model.addRow(new Object[] {
                        c.getId(),
                        c.getClient().getNom() + " " + c.getClient().getPrenom(),
                        c.getTypeCommande(),
                        c.getEtat(),
                        sdf.format(c.getDateCommande()),
                        UIUtils.formatPrice(c.getTotal())
                });
            }
        }
    }

    private void showChangeOrderStateDialog(Commande commande, DefaultTableModel model, EtatCommande filter) {
        JDialog dialog = new JDialog(this, "Changer l'état de la commande #" + commande.getId(), true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        UIUtils.centerOnScreen(dialog);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Sélectionnez le nouvel état");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel statePanel = new JPanel(new GridBagLayout());
        statePanel.setBackground(Color.WHITE);
        statePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 5, 5);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        JLabel currentStateLabel = new JLabel("État actuel: " + commande.getEtat());
        currentStateLabel.setFont(TITLE_FONT);
        statePanel.add(currentStateLabel, gc);

        gc.gridy = 1;
        gc.gridwidth = 1;
        JLabel newStateLabel = new JLabel("Nouvel état:");
        newStateLabel.setFont(TITLE_FONT);
        statePanel.add(newStateLabel, gc);

        gc.gridx = 1;
        JComboBox<EtatCommande> stateCombo = new JComboBox<>(EtatCommande.values());
        stateCombo.setSelectedItem(commande.getEtat());
        stateCombo.setFont(NORMAL_FONT);
        ((JLabel) stateCombo.getRenderer()).setFont(NORMAL_FONT);
        statePanel.add(stateCombo, gc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton saveBtn = createChineseStyleButton("Enregistrer", DARK_RED);
        JButton cancelBtn = createChineseStyleButton("Annuler", new Color(230, 211, 162));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(statePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            EtatCommande newState = (EtatCommande) stateCombo.getSelectedItem();
            if (newState != commande.getEtat()) {
                if (commandeManager.modifierEtatCommande(commande.getId(), newState)) {
                    loadOrderData(model, filter);
                    showSuccessMessage("État de la commande modifié avec succès");
                } else {
                    showErrorMessage("Erreur lors de la modification de l'état");
                }
            }
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showOrderDetails(Commande commande) {
        JDialog dialog = new JDialog(this, "Détails de la commande #" + commande.getId(), true);
        dialog.setSize(600, 500);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        UIUtils.centerOnScreen(dialog);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Commande #" + commande.getId());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        JLabel dateLabel = new JLabel(sdf.format(commande.getDateCommande()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        JPanel clientPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        clientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 211, 162)),
                "Client",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        clientPanel.setOpaque(false);

        clientPanel.add(createBoldLabel("Nom:"));
        clientPanel.add(new JLabel(commande.getClient().getNom() + " " + commande.getClient().getPrenom()));

        clientPanel.add(createBoldLabel("Téléphone:"));
        clientPanel.add(new JLabel(commande.getClient().getTelephone()));

        clientPanel.add(createBoldLabel("Adresse:"));
        clientPanel.add(new JLabel(commande.getAdresseLivraison()));

        JPanel orderInfoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 211, 162)),
                "Commande",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        orderInfoPanel.setOpaque(false);

        orderInfoPanel.add(createBoldLabel("Type:"));
        orderInfoPanel.add(new JLabel(commande.getTypeCommande().toString()));

        orderInfoPanel.add(createBoldLabel("État:"));
        JLabel stateLabel = new JLabel(commande.getEtat().toString());
        setStateColor(stateLabel, commande.getEtat());
        orderInfoPanel.add(stateLabel);

        orderInfoPanel.add(createBoldLabel("Date:"));
        orderInfoPanel.add(new JLabel(sdf.format(commande.getDateCommande())));

        orderInfoPanel.add(createBoldLabel("Total:"));
        JLabel totalLabel = new JLabel(UIUtils.formatPrice(commande.getTotal()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(new Color(46, 204, 113));
        orderInfoPanel.add(totalLabel);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setOpaque(false);
        topPanel.add(clientPanel);
        topPanel.add(orderInfoPanel);
        infoPanel.add(topPanel, BorderLayout.NORTH);

        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 211, 162)),
                "Produits",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        productsPanel.setOpaque(false);

        String[] columns = { "Produit", "Prix unitaire", "Quantité", "Total" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable productsTable = new JTable(model);
        configureTable(productsTable);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        productsPanel.add(scrollPane, BorderLayout.CENTER);

        for (Map.Entry<Produit, Integer> entry : commande.getProduits().entrySet()) {
            Produit p = entry.getKey();
            int quantity = entry.getValue();
            double total = p.getPrix() * quantity;

            model.addRow(new Object[] {
                    p.getNom(),
                    UIUtils.formatPrice(p.getPrix()),
                    quantity,
                    UIUtils.formatPrice(total)
            });
        }

        infoPanel.add(productsPanel, BorderLayout.CENTER);

        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 211, 162)),
                "Commentaires",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        commentPanel.setOpaque(false);

        JTextArea commentArea = new JTextArea(commande.getCommentaire(), 3, 30);
        commentArea.setFont(NORMAL_FONT);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        commentPanel.add(commentScroll, BorderLayout.CENTER);

        JButton saveCommentBtn = createChineseStyleButton("Enregistrer le commentaire", DARK_RED);
        JPanel commentBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        commentBtnPanel.setOpaque(false);
        commentBtnPanel.add(saveCommentBtn);
        commentPanel.add(commentBtnPanel, BorderLayout.SOUTH);

        infoPanel.add(commentPanel, BorderLayout.SOUTH);
        contentPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton printBtn = createChineseStyleButton("Imprimer", DARK_RED);
        JButton closeBtn = createChineseStyleButton("Fermer", new Color(230, 211, 162));
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveCommentBtn.addActionListener(e -> {
            String comment = commentArea.getText();
            if (commandeManager.modifierCommentaireCommande(commande.getId(), comment)) {
                showSuccessMessage("Commentaire enregistré avec succès");
            } else {
                showErrorMessage("Erreur lors de l'enregistrement du commentaire");
            }
        });

        printBtn.addActionListener(e -> {
            printOrder(commande);
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    /**
     * Print the order details with better PDF support
     */
    private void printOrder(Commande commande) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Commande #" + commande.getId());

            job.setPrintable(new OrderPrintable(commande));

            if (job.printDialog()) {
                showInfoMessage("Impression en cours...");
                job.print();
                showSuccessMessage("Impression terminée avec succès");
            }

        } catch (PrinterException ex) {
            showErrorMessage("Erreur lors de l'impression: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Erreur inattendue lors de l'impression: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Create a printable panel for the order
     */
    private JPanel createPrintableOrderPanel(Commande commande) {
        JPanel printPanel = new JPanel();
        printPanel.setLayout(new BoxLayout(printPanel, BoxLayout.Y_AXIS));
        printPanel.setBackground(Color.WHITE);
        printPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);

        JLabel restaurantName = new JLabel("RESTAURANT CHINOIS", JLabel.CENTER);
        restaurantName.setFont(new Font("Arial", Font.BOLD, 18));
        restaurantName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel orderTitle = new JLabel("COMMANDE #" + commande.getId(), JLabel.CENTER);
        orderTitle.setFont(new Font("Arial", Font.BOLD, 16));
        orderTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dateLabel = new JLabel("Date: " + sdf.format(commande.getDateCommande()), JLabel.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(restaurantName);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(orderTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(dateLabel);

        printPanel.add(headerPanel);
        printPanel.add(Box.createVerticalStrut(20));

        JSeparator separator1 = new JSeparator();
        printPanel.add(separator1);
        printPanel.add(Box.createVerticalStrut(15));

        JPanel clientInfoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        clientInfoPanel.setBackground(Color.WHITE);

        JLabel clientTitleLabel = new JLabel("INFORMATIONS CLIENT");
        clientTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        clientInfoPanel.add(clientTitleLabel);
        clientInfoPanel.add(new JLabel(""));

        clientInfoPanel.add(new JLabel("Nom:"));
        clientInfoPanel.add(new JLabel(commande.getClient().getNom() + " " + commande.getClient().getPrenom()));

        clientInfoPanel.add(new JLabel("Téléphone:"));
        clientInfoPanel.add(new JLabel(commande.getClient().getTelephone()));

        clientInfoPanel.add(new JLabel("Adresse:"));
        clientInfoPanel.add(new JLabel(commande.getAdresseLivraison()));

        clientInfoPanel.add(new JLabel("Type de commande:"));
        clientInfoPanel.add(new JLabel(commande.getTypeCommande().toString()));

        clientInfoPanel.add(new JLabel("État:"));
        clientInfoPanel.add(new JLabel(commande.getEtat().toString()));

        printPanel.add(clientInfoPanel);
        printPanel.add(Box.createVerticalStrut(15));

        JSeparator separator2 = new JSeparator();
        printPanel.add(separator2);
        printPanel.add(Box.createVerticalStrut(15));

        JLabel productsTitle = new JLabel("PRODUITS COMMANDÉS");
        productsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        printPanel.add(productsTitle);
        printPanel.add(Box.createVerticalStrut(10));

        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBackground(Color.WHITE);

        String[] columns = { "Produit", "Prix unitaire", "Quantité", "Total" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        for (Map.Entry<Produit, Integer> entry : commande.getProduits().entrySet()) {
            Produit p = entry.getKey();
            int quantity = entry.getValue();
            double total = p.getPrix() * quantity;

            model.addRow(new Object[] {
                    p.getNom(),
                    UIUtils.formatPrice(p.getPrix()),
                    String.valueOf(quantity),
                    UIUtils.formatPrice(total)
            });
        }

        JTable productsTable = new JTable(model);
        productsTable.setFont(new Font("Arial", Font.PLAIN, 10));
        productsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 10));
        productsTable.setRowHeight(20);
        productsTable.setShowGrid(true);
        productsTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane tableScrollPane = new JScrollPane(productsTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 150));
        productsPanel.add(tableScrollPane, BorderLayout.CENTER);

        printPanel.add(productsPanel);
        printPanel.add(Box.createVerticalStrut(15));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);

        JLabel totalLabel = new JLabel("TOTAL: " + UIUtils.formatPrice(commande.getTotal()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);

        printPanel.add(totalPanel);
        printPanel.add(Box.createVerticalStrut(15));

        if (commande.getCommentaire() != null && !commande.getCommentaire().trim().isEmpty()) {
            JSeparator separator3 = new JSeparator();
            printPanel.add(separator3);
            printPanel.add(Box.createVerticalStrut(10));

            JLabel commentsTitle = new JLabel("COMMENTAIRES:");
            commentsTitle.setFont(new Font("Arial", Font.BOLD, 12));
            printPanel.add(commentsTitle);
            printPanel.add(Box.createVerticalStrut(5));

            JTextArea commentsArea = new JTextArea(commande.getCommentaire());
            commentsArea.setFont(new Font("Arial", Font.PLAIN, 10));
            commentsArea.setLineWrap(true);
            commentsArea.setWrapStyleWord(true);
            commentsArea.setEditable(false);
            commentsArea.setBackground(Color.WHITE);
            commentsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            printPanel.add(commentsArea);
            printPanel.add(Box.createVerticalStrut(15));
        }

        JSeparator separator4 = new JSeparator();
        printPanel.add(separator4);
        printPanel.add(Box.createVerticalStrut(10));

        JLabel footerLabel = new JLabel("Merci de votre visite!", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        printPanel.add(footerLabel);

        return printPanel;
    }

    /**
     * Enhanced Printable class for better PDF and print support
     */
    private class OrderPrintable implements Printable {
        private Commande commande;
        private Font titleFont = new Font("Arial", Font.BOLD, 16);
        private Font headerFont = new Font("Arial", Font.BOLD, 12);
        private Font normalFont = new Font("Arial", Font.PLAIN, 10);
        private Font boldFont = new Font("Arial", Font.BOLD, 10);

        public OrderPrintable(Commande commande) {
            this.commande = commande;
        }

        @Override
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
            if (page > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int x = (int) pf.getImageableX() + 20;
            int y = (int) pf.getImageableY() + 30;
            int width = (int) pf.getImageableWidth() - 40;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            y = drawHeader(g2d, x, y, width);
            y += 20;
            y = drawSeparator(g2d, x, y, width);
            y += 15;
            y = drawClientInfo(g2d, x, y, width);
            y += 15;
            y = drawSeparator(g2d, x, y, width);
            y += 15;
            y = drawProductsTable(g2d, x, y, width);
            y += 20;
            y = drawTotal(g2d, x, y, width);
            y += 15;

            if (commande.getCommentaire() != null && !commande.getCommentaire().trim().isEmpty()) {
                y = drawSeparator(g2d, x, y, width);
                y += 10;
                y = drawComments(g2d, x, y, width);
                y += 15;
            }

            y = drawSeparator(g2d, x, y, width);
            y += 10;
            drawFooter(g2d, x, y, width);

            return PAGE_EXISTS;
        }

        private int drawHeader(Graphics2D g2d, int x, int y, int width) {
            g2d.setFont(titleFont);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            String restaurantName = "RESTAURANT CHINOIS";
            int textWidth = fm.stringWidth(restaurantName);
            g2d.drawString(restaurantName, x + (width - textWidth) / 2, y);
            y += fm.getHeight() + 5;

            g2d.setFont(headerFont);
            fm = g2d.getFontMetrics();
            String orderTitle = "COMMANDE #" + commande.getId();
            textWidth = fm.stringWidth(orderTitle);
            g2d.drawString(orderTitle, x + (width - textWidth) / 2, y);
            y += fm.getHeight() + 5;

            g2d.setFont(normalFont);
            fm = g2d.getFontMetrics();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateText = "Date: " + sdf.format(commande.getDateCommande());
            textWidth = fm.stringWidth(dateText);
            g2d.drawString(dateText, x + (width - textWidth) / 2, y);
            y += fm.getHeight();

            return y;
        }

        private int drawSeparator(Graphics2D g2d, int x, int y, int width) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y, x + width, y);
            return y;
        }

        private int drawClientInfo(Graphics2D g2d, int x, int y, int width) {
            g2d.setFont(headerFont);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();

            g2d.drawString("INFORMATIONS CLIENT", x, y);
            y += fm.getHeight() + 10;

            g2d.setFont(normalFont);
            fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight() + 3;

            g2d.drawString("Nom: " + commande.getClient().getNom() + " " + commande.getClient().getPrenom(), x, y);
            y += lineHeight;

            g2d.drawString("Téléphone: " + commande.getClient().getTelephone(), x, y);
            y += lineHeight;

            g2d.drawString("Adresse: " + commande.getAdresseLivraison(), x, y);
            y += lineHeight;

            g2d.drawString("Type: " + commande.getTypeCommande().toString(), x, y);
            y += lineHeight;

            g2d.drawString("État: " + commande.getEtat().toString(), x, y);
            y += lineHeight;

            return y;
        }

        private int drawProductsTable(Graphics2D g2d, int x, int y, int width) {
            g2d.setFont(headerFont);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();

            g2d.drawString("PRODUITS COMMANDÉS", x, y);
            y += fm.getHeight() + 10;
            g2d.setFont(boldFont);
            fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight() + 8;

            int col1 = x;
            int col2 = x + width * 2 / 5;
            int col3 = x + width * 3 / 5;
            int col4 = x + width * 4 / 5;
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(x, y - fm.getAscent(), width, lineHeight);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y - fm.getAscent(), width, lineHeight);

            g2d.drawString("Produit", col1 + 5, y);
            g2d.drawString("Prix unit.", col2 + 5, y);
            g2d.drawString("Qté", col3 + 5, y);
            g2d.drawString("Total", col4 + 5, y);

            y += lineHeight;
            g2d.setFont(normalFont);
            fm = g2d.getFontMetrics();
            lineHeight = fm.getHeight() + 6;

            for (Map.Entry<Produit, Integer> entry : commande.getProduits().entrySet()) {
                Produit p = entry.getKey();
                int quantity = entry.getValue();
                double total = p.getPrix() * quantity;

                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y - fm.getAscent(), width, lineHeight);

                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y - fm.getAscent(), width, lineHeight);

                String productName = p.getNom();
                if (productName.length() > 25) {
                    productName = productName.substring(0, 22) + "...";
                }

                g2d.drawString(productName, col1 + 5, y);
                g2d.drawString(UIUtils.formatPrice(p.getPrix()), col2 + 5, y);
                g2d.drawString(String.valueOf(quantity), col3 + 5, y);
                g2d.drawString(UIUtils.formatPrice(total), col4 + 5, y);

                y += lineHeight;
            }

            return y;
        }

        private int drawTotal(Graphics2D g2d, int x, int y, int width) {
            g2d.setFont(headerFont);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();

            String totalText = "TOTAL: " + UIUtils.formatPrice(commande.getTotal());
            int textWidth = fm.stringWidth(totalText);
            g2d.drawString(totalText, x + width - textWidth, y);

            return y + fm.getHeight();
        }

        private int drawComments(Graphics2D g2d, int x, int y, int width) {
            g2d.setFont(headerFont);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();

            g2d.drawString("COMMENTAIRES:", x, y);
            y += fm.getHeight() + 5;

            g2d.setFont(normalFont);
            fm = g2d.getFontMetrics();

            String[] words = commande.getCommentaire().split("\\s+");
            StringBuilder line = new StringBuilder();
            int lineHeight = fm.getHeight() + 2;

            for (String word : words) {
                String testLine = line.length() == 0 ? word : line + " " + word;
                if (fm.stringWidth(testLine) > width - 10) {
                    if (line.length() > 0) {
                        g2d.drawString(line.toString(), x + 5, y);
                        y += lineHeight;
                        line = new StringBuilder(word);
                    } else {
                        g2d.drawString(word, x + 5, y);
                        y += lineHeight;
                    }
                } else {
                    line = new StringBuilder(testLine);
                }
            }

            if (line.length() > 0) {
                g2d.drawString(line.toString(), x + 5, y);
                y += lineHeight;
            }

            return y;
        }

        private int drawFooter(Graphics2D g2d, int x, int y, int width) {
            g2d.setFont(normalFont);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();

            String footerText = "Merci de votre visite!";
            int textWidth = fm.stringWidth(footerText);
            g2d.drawString(footerText, x + (width - textWidth) / 2, y);

            return y + fm.getHeight();
        }
    }

    private void setStateColor(JLabel label, EtatCommande etat) {
        switch (etat) {
            case NON_TRAITEE:
                label.setForeground(new Color(52, 152, 219));
                break;
            case EN_PREPARATION:
                label.setForeground(new Color(243, 156, 18));
                break;
            case PRETE:
                label.setForeground(new Color(46, 204, 113));
                break;
            case EN_ROUTE:
                label.setForeground(new Color(155, 89, 182));
                break;
            case LIVREE:
                label.setForeground(new Color(39, 174, 96));
                break;
            case ANNULEE:
                label.setForeground(new Color(231, 76, 60));
                break;
            default:
                label.setForeground(TEXT_COLOR);
        }
    }

    private Color darken(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }

    private void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(NORMAL_FONT);
        table.getTableHeader().setFont(TITLE_FONT);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(255, 215, 0, 128));
        table.setSelectionForeground(DARK_RED);
        table.setGridColor(new Color(230, 211, 162));
        JTableHeader header = table.getTableHeader();
        header.setBackground(RED_CHINESE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Serif", Font.BOLD, 12));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(255, 249, 230));
                }

                return c;
            }
        });
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        return label;
    }

    private void showSuccessMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_CHINESE, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel iconLabel = new JLabel("✓");
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        iconLabel.setForeground(new Color(46, 204, 113));

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Serif", Font.PLAIN, 14));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(msgLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Succès",
                JOptionPane.PLAIN_MESSAGE, null);
    }

    private void showErrorMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RED_CHINESE, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel iconLabel = new JLabel("✗");
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        iconLabel.setForeground(RED_CHINESE);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Serif", Font.PLAIN, 14));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(msgLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Erreur",
                JOptionPane.PLAIN_MESSAGE, null);
    }

    private void showInfoMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GOLD, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel iconLabel = new JLabel("ℹ");
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        iconLabel.setForeground(DARK_RED);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Serif", Font.PLAIN, 14));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(msgLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Information",
                JOptionPane.PLAIN_MESSAGE, null);
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(GOLD_CHINESE);
        closeButton.setBackground(RED_CHINESE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton fullScrenButton = new JButton("[]");
        fullScrenButton.setFont(new Font("Arial", Font.BOLD, 20));
        fullScrenButton.setForeground(GOLD_CHINESE);
        fullScrenButton.setBackground(RED_CHINESE);
        fullScrenButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
        fullScrenButton.setFocusPainted(false);
        fullScrenButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        fullScrenButton.addMouseListener(new MouseAdapter() {
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

    private JButton fullScrenButton() {
        JButton fullScrenButton = new JButton("[]");
        fullScrenButton.setFont(new Font("Arial", Font.BOLD, 20));
        fullScrenButton.setForeground(GOLD_CHINESE);
        fullScrenButton.setBackground(PRIMARY_COLOR);
        fullScrenButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
        fullScrenButton.setFocusPainted(false);
        fullScrenButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fullScrenButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                fullScrenButton.setBackground(DARK_RED);
                fullScrenButton.setForeground(Color.WHITE);
                fullScrenButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD_CHINESE, 1),
                        BorderFactory.createEmptyBorder(1, 11, 1, 11)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fullScrenButton.setBackground(RED_CHINESE);
                fullScrenButton.setForeground(GOLD_CHINESE);
                fullScrenButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                fullScrenButton.setBackground(new Color(100, 0, 0));
            }
        });

        return fullScrenButton;
    }

    private void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (!isFullscreen) {
            normalBounds = getBounds();
            dispose();
            setUndecorated(true);
            setShape(null);
            setVisible(true);
            device.setFullScreenWindow(this);
            isFullscreen = true;
        } else {
            device.setFullScreenWindow(null);
            dispose();
            setUndecorated(true);
            setBounds(normalBounds);
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            setVisible(true);
            isFullscreen = false;
        }
    }

    private JPanel createStyledTabbedPaneWrapper() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40),
                BorderFactory.createCompoundBorder(
                        new ShadowBorder(10),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                                BorderFactory.createEmptyBorder(20, 20, 20, 20)))));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setForeground(DARK_RED);
        tabbedPane.setOpaque(false);
        JPanel dashboardPanel = new DashboardPanel(clientManager, commandeManager, menuManager);
        JScrollPane scrollPane = new JScrollPane(dashboardPanel);
        tabbedPane.addTab("Clients", loadIcon("/resources/icons/user.png"), createClientPanel());
        tabbedPane.addTab("Menu", loadIcon("/resources/icons/menu.png"), createMenuPanel());
        tabbedPane.addTab("Commandes", loadIcon("/resources/icons/commande.png"), createOrderPanel());
        tabbedPane.addTab("Tableau de bord", loadIcon("/resources/icons/dashboard.png"), scrollPane);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(DARK_RED);
        return label;
    }

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
                    String[] columns = { "Login", "Nom", "Prénom", "Téléphone", "Adresse" };
                    DefaultTableModel model = new DefaultTableModel(columns, 0) {
                        @Override
                        public boolean isCellEditable(int row, int col) {
                            return false;
                        }
                    };
                    loadClientData(model);
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

}
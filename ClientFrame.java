import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import java.awt.*;

/**
 * FlowLayout subclass that fully supports wrapping of components.
 */
class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Returns the minimum or preferred dimension needed to layout the target
     * container.
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            Container container = target;

            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }

            targetWidth = container.getSize().width;

            if (targetWidth == 0)
                targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

            if (scrollPane != null && target.isValid()) {
                dim.width -= (hgap + 1);
            }

            return dim;
        }
    }

    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}

public class ClientFrame extends JFrame {
    private final Color RED_CHINESE = new Color(190, 0, 0);
    private final Color GOLD_CHINESE = new Color(212, 175, 55);
    private final Color DARK_RED = new Color(128, 0, 0);
    private final Color BLACK_CHINESE = new Color(25, 25, 25);
    private final Color BG_COLOR = new Color(251, 246, 240);
    private final Color FIELD_BG = new Color(255, 253, 250);
    private final Color ACCENT_COLOR = new Color(230, 211, 162);
    private final Color BUTTON_BG_GREY = new Color(230, 211, 162);
    private Client client;
    private MenuManager menuManager;
    private CommandeManager commandeManager;
    private Map<Produit, Integer> panier = new HashMap<>();
    private RestaurantNutritionalAnalyzer analyzer;
    private boolean isFullscreen = false;
    private Rectangle normalBounds;

    // Add these imports at the top
    public ClientFrame(Client client, MenuManager menuManager, CommandeManager commandeManager) {
        analyzer = new RestaurantNutritionalAnalyzer();
        this.client = client;
        this.menuManager = menuManager;
        this.commandeManager = commandeManager;
        this.panier = new HashMap<>();

        setupModernLookAndFeel();

        setTitle("Le Dragon d'Or - Espace Client");
        setSize(1000, 700);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 700, 20, 20));
        setLayout(new BorderLayout());
        setLocation(200, 10);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(251, 246, 240));

        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(251, 246, 240));

        JPanel bannerPanel = createAutoScrollingBanner(menuManager);
        centerPanel.add(bannerPanel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setName("contentPanel");

        JPanel menuPanel = createMenuPanel();
        JPanel cartPanel = createCartPanel();
        JPanel ordersPanel = createOrdersPanel();

        cardPanel.add(menuPanel, "menu");
        cardPanel.add(cartPanel, "cart");
        cardPanel.add(ordersPanel, "orders");

        centerPanel.add(cardPanel, BorderLayout.CENTER);

        JPanel navPanel = createNavigationPanel(cardPanel);
        centerPanel.add(navPanel, BorderLayout.SOUTH);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        JPanel footerPanel = createFooterPanel();
        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "menu");

        setVisible(true);
    }

    private JPanel createNavigationPanel(JPanel cardPanel) {
        Color DARK_RED = new Color(128, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(new Color(245, 242, 235));
        navPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GOLD_CHINESE),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        ImageIcon icon = new ImageIcon("resources/icons/menu.png");
        Image scaledIcon = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton menuButton = createNavButton("Menu", new ImageIcon(scaledIcon));

        menuButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "menu");
        });
        ImageIcon icon1 = new ImageIcon("resources/icons/cart.png");
        Image scaledIcon1 = icon1.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton cartButton = createNavButton("Panier", new ImageIcon(scaledIcon1));
        cartButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "cart");
        });
        ImageIcon icon2 = new ImageIcon("resources/icons/commande.png");
        Image scaledIcon2 = icon2.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton ordersButton = createNavButton("Mes commandes", new ImageIcon(scaledIcon2));
        ordersButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "orders");
        });

        navPanel.add(menuButton);
        navPanel.add(cartButton);
        navPanel.add(ordersButton);

        return navPanel;
    }

    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem profileItem = new JMenuItem("Mon profil");
        JMenuItem logoutItem = new JMenuItem("Déconnexion");
        JMenuItem exitItem = new JMenuItem("Quitter");
        JMenuItem fullScren = new JMenuItem("Full Screnn");

        fileMenu.add(profileItem);
        fileMenu.add(logoutItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);
        fileMenu.add(fullScren);
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        JMenuItem helpItem = new JMenuItem("Aide");

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        profileItem.addActionListener(e -> showProfileDialog());
        fullScren.addActionListener(e -> toggleFullscreen());
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir vous déconnecter?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame(new ClientManager(), menuManager, commandeManager);
            }
        });

        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir quitter l'application?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Restaurant App v1.0\n© 2025 Your Company\nTous droits réservés.",
                    "À propos", JOptionPane.INFORMATION_MESSAGE);
        });

        helpItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Pour obtenir de l'aide, contactez le service client au +216 51075734\n" +
                            "ou par email à support@votrerestaurant.com",
                    "Aide", JOptionPane.INFORMATION_MESSAGE);
        });

        return menuBar;
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

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        footerPanel.setBackground(new Color(251, 246, 240)); // BG_COLOR

        JLabel statusLabel = new JLabel("Connecté en tant que " + client.getPrenom() + " " + client.getNom());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setHorizontalAlignment(JLabel.RIGHT);
        statusLabel.setForeground(new Color(128, 0, 0)); // DARK_RED
        timeLabel.setForeground(new Color(128, 0, 0)); // DARK_RED
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            timeLabel.setText(sdf.format(new Date()));
        });
        timer.start();

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(timeLabel, BorderLayout.EAST);

        return footerPanel;
    }

    private JPanel createMenuPanel() {
        Color DARK_RED = new Color(128, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(BG_COLOR);

        JPanel categoriesBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        categoriesBar.setOpaque(false);

        ButtonGroup categoryGroup = new ButtonGroup();

        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20)); // Nécessite WrapLayout.java
        productsPanel.setOpaque(false);

        for (String category : menuManager.getTypes()) {
            JToggleButton categoryBtn = new JToggleButton(category);
            categoryBtn.setFocusPainted(false);
            categoryBtn.setFont(new Font("Arial", Font.BOLD, 13));
            categoryBtn.setBackground(Color.WHITE);
            categoryBtn.setForeground(DARK_RED);
            categoryBtn.setBorder(BorderFactory.createLineBorder(GOLD_CHINESE, 2, true));
            categoryBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            categoryBtn.setPreferredSize(new Dimension(120, 35));

            categoryBtn.addChangeListener(e -> {
                if (categoryBtn.isSelected()) {
                    categoryBtn.setBackground(DARK_RED);
                    categoryBtn.setForeground(Color.WHITE);
                } else {
                    categoryBtn.setBackground(Color.WHITE);
                    categoryBtn.setForeground(DARK_RED);
                }
            });

            categoryGroup.add(categoryBtn);
            categoriesBar.add(categoryBtn);

            categoryBtn.addActionListener(e -> {
                if (categoryBtn.isSelected()) {
                    loadProductsGrid(category, productsPanel);
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(categoriesBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        if (categoriesBar.getComponentCount() > 0) {
            JToggleButton firstBtn = (JToggleButton) categoriesBar.getComponent(0);
            firstBtn.setSelected(true);
            for (ActionListener l : firstBtn.getActionListeners()) {
                l.actionPerformed(new ActionEvent(firstBtn, ActionEvent.ACTION_PERFORMED, ""));
            }
        }

        return panel;
    }

    private void loadProductsGrid(String category, JPanel productsPanel) {
        productsPanel.removeAll();

        for (Produit produit : menuManager.getProduitsByType(category)) {
            JPanel productCard = createProductCard(produit);
            productCard.setPreferredSize(new Dimension(350, 200));
            productCard.setMinimumSize(new Dimension(350, 200));
            productCard.setMaximumSize(new Dimension(350, 200));
            productsPanel.add(productCard);
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Produit produit) {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 20;

                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, arc, arc);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                g2.setColor(GOLD_CHINESE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(270, 200));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(245, 245, 245));
        imagePanel.setPreferredSize(new Dimension(130, 200));
        imagePanel.setBorder(BorderFactory.createLineBorder(GOLD_CHINESE, 1, true));

        try {
            if (produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
                URL url = new URL(produit.getImageUrl());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);

                Image img = ImageIO.read(connection.getInputStream());
                if (img != null) {
                    img = img.getScaledInstance(120, 100, Image.SCALE_SMOOTH);
                    JLabel imgLabel = new JLabel(new ImageIcon(img));
                    imgLabel.setHorizontalAlignment(JLabel.CENTER);
                    imagePanel.add(imgLabel, BorderLayout.CENTER);
                } else {
                    throw new IOException("Image null");
                }
            } else {
                JLabel placeholder = new JLabel("🍽️", JLabel.CENTER);
                placeholder.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                imagePanel.add(placeholder, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel error = new JLabel("Image indisponible", JLabel.CENTER);
            error.setFont(new Font("Arial", Font.ITALIC, 11));
            error.setForeground(Color.GRAY);
            imagePanel.add(error, BorderLayout.CENTER);
        }

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel nameLabel = new JLabel(produit.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(DARK_RED);

        JTextArea descArea = new JTextArea(produit.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setMaximumSize(new Dimension(200, 100));

        JLabel priceLabel = new JLabel(UIUtils.formatPrice(produit.getPrix()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 128, 0));

        JButton addBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (produit.isDisponible()) {
                    g2.setColor(RED_CHINESE);
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };
        JButton iaBtn = new JButton();
        iaBtn.setFocusPainted(false);
        iaBtn.setBackground(new Color(57, 105, 138));
        iaBtn.setForeground(Color.WHITE);
        iaBtn.setFont(new Font("Arial", Font.BOLD, 14));
        iaBtn.setPreferredSize(new Dimension(100, 30));
        iaBtn.setMaximumSize(new Dimension(100, 30));
        if (produit.isDisponible()) {
            ImageIcon iconIA = new ImageIcon("resources/icons/ia.png");
            Image scaledIA = iconIA.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            iaBtn.setIcon(new ImageIcon(scaledIA));
            ImageIcon icon = new ImageIcon("resources/icons/cart.png");
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            addBtn.setIcon(new ImageIcon(scaled));
        } else {
            // ImageIcon icon = new ImageIcon("resources/icons/not_available.png");
            // Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            // addBtn.setIcon(new ImageIcon(scaled));
            addBtn.setText(" Indisponible");
            addBtn.setEnabled(false); // optional if you want it grayed out
        }
        addBtn.setEnabled(produit.isDisponible());
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorderPainted(false);
        addBtn.setContentAreaFilled(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.setMaximumSize(new Dimension(100, 30));

        addBtn.addActionListener(e -> {
            panier.put(produit, panier.getOrDefault(produit, 0) + 1);
            JOptionPane.showMessageDialog(
                    this,
                    produit.getNom() + " ajouté au panier",
                    "Produit ajouté",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        iaBtn.addActionListener(e -> {
            analyzer.setVisible(true);
            analyzer.analyzeExternalDish(produit.getNom(), produit.getDescription());
        });
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(descArea);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(addBtn);
        detailsPanel.add(iaBtn);
        card.add(imagePanel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(190, 0, 0)); // Rouge chinois
        headerPanel.setPreferredSize(new Dimension(800, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(212, 175, 55))); // Bordure dorée

        JLabel logoLabel = new JLabel("LE DRAGON D'OR");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userIcon = new JLabel("福"); // Symbole chinois porte-bonheur
        userIcon.setFont(new Font("SimSun", Font.BOLD, 32));
        userIcon.setForeground(new Color(212, 175, 55)); // Or

        JLabel userLabel = new JLabel(client.getPrenom() + " " + client.getNom());
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 20));

        userPanel.add(userIcon);
        userPanel.add(userLabel);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCartPanel() {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonsPanel.setOpaque(false);

        JButton refreshBtn = createOutlinedButton("Actualiser", new Font("Arial", Font.BOLD, 13));
        refreshBtn.setPreferredSize(new Dimension(120, 40));
        JButton deleteAllBtn = createRedButton("Supprimer tout", new Font("Arial", Font.BOLD, 13));
        deleteAllBtn.setPreferredSize(new Dimension(120, 40));

        JButton checkoutBtn = createRedButton("Commander", new Font("Arial", Font.BOLD, 13));
        checkoutBtn.setPreferredSize(new Dimension(120, 40));
        tableButtonsPanel.add(deleteAllBtn);
        tableButtonsPanel.add(refreshBtn);

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_CHINESE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel priceBreakdownPanel = new JPanel(new GridLayout(0, 2));
        priceBreakdownPanel.setOpaque(false);

        JLabel subtotalLabel = new JLabel("0.00 TND");
        JLabel deliveryLabel = new JLabel("0.00 TND");
        JLabel totalLabel = new JLabel("0.00 TND");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(RED_CHINESE);

        priceBreakdownPanel.add(new JLabel("Sous-total:"));
        priceBreakdownPanel.add(subtotalLabel);

        priceBreakdownPanel.add(new JLabel("Frais de livraison:"));
        priceBreakdownPanel.add(deliveryLabel);

        priceBreakdownPanel.add(new JLabel("Total:"));
        priceBreakdownPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(deleteAllBtn);
        buttonPanel.add(refreshBtn);

        summaryPanel.add(priceBreakdownPanel, BorderLayout.CENTER);
        summaryPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> updateCartItems(cartItemsPanel, subtotalLabel, deliveryLabel, totalLabel));
        deleteAllBtn.addActionListener(e -> {
            panier.clear();
            updateCartItems(cartItemsPanel, subtotalLabel, deliveryLabel, totalLabel);
        });

        checkoutBtn.addActionListener(e -> {
            if (panier.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Votre panier est vide",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showOrderDialog();
        });

        updateCartItems(cartItemsPanel, subtotalLabel, deliveryLabel, totalLabel);

        return panel;
    }

    private void updateCartItems(JPanel cartItemsPanel, JLabel subtotalLabel, JLabel deliveryLabel, JLabel totalLabel) {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        cartItemsPanel.removeAll();

        if (panier.isEmpty()) {
            JPanel emptyCartPanel = new JPanel(new BorderLayout());
            emptyCartPanel.setPreferredSize(new Dimension(600, 300));
            emptyCartPanel.setBackground(BG_COLOR);

            JLabel emptyLabel = new JLabel("Votre panier est vide");
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 18));
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
            emptyLabel.setForeground(DARK_RED);

            JLabel symbolLabel = new JLabel("🛒", JLabel.CENTER);
            symbolLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
            symbolLabel.setForeground(GOLD_CHINESE);

            emptyCartPanel.add(symbolLabel, BorderLayout.NORTH);
            emptyCartPanel.add(emptyLabel, BorderLayout.CENTER);

            cartItemsPanel.add(emptyCartPanel);
        } else {
            double subtotal = 0;
            double deliveryFee = 0;
            for (Map.Entry<Produit, Integer> entry : panier.entrySet()) {
                Produit produit = entry.getKey();
                int quantity = entry.getValue();
                double itemTotal = produit.getPrix() * quantity;
                subtotal += itemTotal;

                JPanel itemPanel = createCartItemPanel(produit, quantity, cartItemsPanel, subtotalLabel, deliveryLabel,
                        totalLabel);
                cartItemsPanel.add(itemPanel);

                JSeparator separator = new JSeparator();
                separator.setForeground(GOLD_CHINESE);
                cartItemsPanel.add(separator);
            }
            double total = subtotal + deliveryFee;

            subtotalLabel.setText(UIUtils.formatPrice(subtotal));
            deliveryLabel.setText(UIUtils.formatPrice(deliveryFee));
            totalLabel.setText(UIUtils.formatPrice(total));
        }

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createCartItemPanel(Produit produit, int quantity, JPanel cartItemsPanel, JLabel subtotalLabel,
            JLabel deliveryLabel, JLabel totalLabel) {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        JPanel itemPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(0, 0, 0, 20)); // Ombre légère
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                super.paintComponent(g);
            }
        };
        itemPanel.setOpaque(false);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(produit.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_RED);

        JLabel priceLabel = new JLabel(UIUtils.formatPrice(produit.getPrix()));
        priceLabel.setForeground(RED_CHINESE);

        detailsPanel.add(nameLabel, BorderLayout.NORTH);
        detailsPanel.add(priceLabel, BorderLayout.CENTER);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quantityPanel.setOpaque(false);

        JButton minusBtn = createCircleButton("", GOLD_CHINESE, RED_CHINESE);
        minusBtn.setIcon(loadIcon("resources/icons/minus.png", 35, 35));
        minusBtn.setToolTipText("Réduire");

        JButton plusBtn = createCircleButton("", GOLD_CHINESE, RED_CHINESE);
        plusBtn.setIcon(loadIcon("resources/icons/plus.png", 35, 35));
        plusBtn.setToolTipText("Augmenter");

        JButton removeBtn = createCircleButton("", Color.WHITE, RED_CHINESE);
        removeBtn.setIcon(loadIcon("resources/icons/remove.png", 35, 35));
        removeBtn.setToolTipText("Supprimer");

        JLabel quantityLabel = new JLabel(String.valueOf(quantity));
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        quantityLabel.setHorizontalAlignment(JLabel.CENTER);
        quantityLabel.setPreferredSize(new Dimension(35, 35));

        quantityPanel.add(minusBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusBtn);
        quantityPanel.add(Box.createHorizontalStrut(10));
        quantityPanel.add(removeBtn);

        detailsPanel.add(quantityPanel, BorderLayout.SOUTH);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);

        JLabel itemTotalLabel = new JLabel(UIUtils.formatPrice(produit.getPrix() * quantity));
        itemTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        itemTotalLabel.setForeground(DARK_RED);
        itemTotalLabel.setHorizontalAlignment(JLabel.RIGHT);

        totalPanel.add(itemTotalLabel, BorderLayout.CENTER);

        itemPanel.add(detailsPanel, BorderLayout.CENTER);
        itemPanel.add(totalPanel, BorderLayout.EAST);

        minusBtn.addActionListener(e -> {
            if (quantity > 1) {
                panier.put(produit, quantity - 1);
                updateCartItems(cartItemsPanel, subtotalLabel, deliveryLabel, totalLabel);
            }
        });

        plusBtn.addActionListener(e -> {
            panier.put(produit, quantity + 1);
            updateCartItems(cartItemsPanel, subtotalLabel, deliveryLabel, totalLabel);
        });

        removeBtn.addActionListener(e -> {
            panier.remove(produit);
            updateCartItems(cartItemsPanel, subtotalLabel, deliveryLabel, totalLabel);
        });

        return itemPanel;
    }

    private JPanel createOrdersPanel() {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel ordersListPanel = new JPanel();
        ordersListPanel.setLayout(new BoxLayout(ordersListPanel, BoxLayout.Y_AXIS));
        ordersListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(ordersListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(GOLD_CHINESE, 2)); // Bordure dorée
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        loadClientOrdersCards(ordersListPanel);

        JButton refreshBtn = createRedButton("Actualiser", new Font("Arial", Font.BOLD, 13));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadClientOrdersCards(ordersListPanel));

        return panel;
    }

    private void loadClientOrdersCards(JPanel ordersListPanel) {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        ordersListPanel.removeAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (commandeManager.getCommandesClient(client).isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setPreferredSize(new Dimension(600, 300));
            emptyPanel.setBackground(BG_COLOR);

            JLabel emptyLabel = new JLabel("Vous n'avez pas encore passé de commande");
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 18));
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
            emptyLabel.setForeground(DARK_RED);

            JLabel symbolLabel = new JLabel("🐉", JLabel.CENTER);
            symbolLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
            symbolLabel.setForeground(GOLD_CHINESE);

            emptyPanel.add(symbolLabel, BorderLayout.NORTH);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);

            ordersListPanel.add(emptyPanel);
        } else {
            for (Commande commande : commandeManager.getCommandesClient(client)) {
                JPanel orderCard = createOrderCard(commande, sdf);
                ordersListPanel.add(orderCard);
                ordersListPanel.add(Box.createVerticalStrut(10));
            }
        }

        ordersListPanel.revalidate();
        ordersListPanel.repaint();
    }

    private JPanel createOrderCard(Commande commande, SimpleDateFormat sdf) {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(GOLD_CHINESE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        ImageIcon orderIcon = new ImageIcon("resources/icons/commande.png");
        Image scaled = orderIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel orderIdLabel = new JLabel(" Commande #" + commande.getId(), new ImageIcon(scaled), JLabel.LEFT);
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orderIdLabel.setForeground(DARK_RED);

        JLabel dateLabel = new JLabel(sdf.format(commande.getDateCommande()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);

        headerPanel.add(orderIdLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        detailsPanel.add(new JLabel("Type:"));
        JLabel typeLabel = new JLabel(commande.getTypeCommande().toString());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        typeLabel.setForeground(RED_CHINESE);
        detailsPanel.add(typeLabel);

        detailsPanel.add(new JLabel("État:"));
        JLabel statusLabel = new JLabel(commande.getEtat().toString());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        switch (commande.getEtat()) {
            case NON_TRAITEE:
                statusLabel.setForeground(new Color(255, 140, 0)); // Orange
                break;
            case EN_PREPARATION:
                statusLabel.setForeground(new Color(0, 102, 204)); // Bleu doux
                break;
            case PRETE:
                statusLabel.setForeground(new Color(0, 153, 0)); // Vert doux
                break;
            case EN_ROUTE:
                statusLabel.setForeground(new Color(153, 51, 255)); // Violet doux
                break;
            case LIVREE:
                statusLabel.setForeground(new Color(0, 153, 0)); // Vert doux
                break;
            case ANNULEE:
                statusLabel.setForeground(RED_CHINESE);
                break;
        }

        detailsPanel.add(statusLabel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel totalLabel = new JLabel("Total: " + UIUtils.formatPrice(commande.getTotal()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(DARK_RED);

        ImageIcon icon = new ImageIcon("resources/icons/view.png");
        Image scaled1 = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton detailsBtn = createRedButton("", new Font("Arial", Font.BOLD, 12));
        detailsBtn.setIcon(new ImageIcon(scaled1));
        detailsBtn.setToolTipText("Voir détails");
        detailsBtn.setPreferredSize(new Dimension(30, 30));

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(detailsBtn, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Action listener bouton détails
        detailsBtn.addActionListener(e -> showOrderDetailsDialog(commande));

        return card;
    }

    private void showOrderDialog() {
        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        JDialog dialog = new JDialog(this, "Finaliser la commande", true);
        dialog.setSize(600, 650);
        dialog.setLayout(new BorderLayout());
        UIUtils.centerOnScreen(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_CHINESE, 2),
                BorderFactory.createTitledBorder("Résumé de la commande")));

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Produit");
        model.addColumn("Prix");
        model.addColumn("Quantité");
        model.addColumn("Total");

        JTable itemsTable = new JTable(model);
        itemsTable.setRowHeight(25);
        itemsTable.setShowGrid(false);
        itemsTable.setIntercellSpacing(new Dimension(0, 0));
        itemsTable.setFocusable(false);
        itemsTable.setSelectionBackground(Color.WHITE);
        itemsTable.getTableHeader().setBackground(RED_CHINESE);
        itemsTable.getTableHeader().setForeground(Color.WHITE);
        itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        double subtotal = 0;
        for (Map.Entry<Produit, Integer> entry : panier.entrySet()) {
            Produit p = entry.getKey();
            int quantity = entry.getValue();
            double itemTotal = p.getPrix() * quantity;
            subtotal += itemTotal;

            model.addRow(new Object[] {
                    p.getNom(),
                    UIUtils.formatPrice(p.getPrix()),
                    quantity,
                    UIUtils.formatPrice(itemTotal)
            });
        }

        JScrollPane tableScrollPane = new JScrollPane(itemsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(GOLD_CHINESE, 1));
        tableScrollPane.setPreferredSize(new Dimension(400, 100)); // Ajuste selon ta fenêtre

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel pricePanel = new JPanel(new GridLayout(0, 2));
        pricePanel.setOpaque(false);
        pricePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        pricePanel.add(new JLabel("Sous-total:")).setForeground(DARK_RED);
        JLabel subtotalLabel = new JLabel(UIUtils.formatPrice(subtotal));
        subtotalLabel.setHorizontalAlignment(JLabel.RIGHT);
        pricePanel.add(subtotalLabel);

        pricePanel.add(new JLabel("Frais de livraison:")).setForeground(DARK_RED);
        JLabel deliveryLabel = new JLabel("0.00 TND");
        deliveryLabel.setHorizontalAlignment(JLabel.RIGHT);
        pricePanel.add(deliveryLabel);

        pricePanel.add(new JLabel("Total:")).setForeground(DARK_RED);
        JLabel totalLabel = new JLabel(UIUtils.formatPrice(subtotal));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(RED_CHINESE);
        totalLabel.setHorizontalAlignment(JLabel.RIGHT);
        pricePanel.add(totalLabel);

        summaryPanel.add(tablePanel, BorderLayout.CENTER);
        summaryPanel.add(pricePanel, BorderLayout.SOUTH);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_CHINESE, 2),
                BorderFactory.createTitledBorder("Informations de livraison")));
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 10, 8, 10);
        gc.weightx = 1.0;

        gc.gridx = 0;
        gc.gridy = 0;
        formPanel.add(new JLabel("Type de commande:"), gc);

        gc.gridx = 1;
        JComboBox<TypeCommande> typeCombo = new JComboBox<>(TypeCommande.values());
        typeCombo.setPreferredSize(new Dimension(200, 30));
        formPanel.add(typeCombo, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        JLabel adresseLabel = new JLabel("Adresse de livraison:");
        formPanel.add(adresseLabel, gc);

        gc.gridx = 1;
        JTextField adresseField = new JTextField(client.getAdresse(), 20);
        adresseField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(adresseField, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        formPanel.add(new JLabel("Téléphone:"), gc);

        gc.gridx = 1;
        JTextField telField = new JTextField(client.getTelephone(), 20);
        telField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(telField, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        formPanel.add(new JLabel("Notes:"), gc);

        gc.gridx = 1;
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll, gc);

        typeCombo.addActionListener(e -> {
            TypeCommande type = (TypeCommande) typeCombo.getSelectedItem();
            boolean isDelivery = type == TypeCommande.DOMICILE;
            adresseLabel.setEnabled(isDelivery);
            adresseField.setEnabled(isDelivery);
        });

        adresseField.setEnabled(typeCombo.getSelectedItem() == TypeCommande.DOMICILE);
        adresseLabel.setEnabled(typeCombo.getSelectedItem() == TypeCommande.DOMICILE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_COLOR);

        JButton cancelBtn = createOutlinedButton("Annuler", new Font("Arial", Font.BOLD, 13));
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        JButton orderBtn = createRedButton("Commander", new Font("Arial", Font.BOLD, 14));
        orderBtn.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(cancelBtn);
        buttonPanel.add(orderBtn);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        orderBtn.addActionListener(e -> {
            String adresse = adresseField.getText().trim();
            String tel = telField.getText().trim();
            TypeCommande type = (TypeCommande) typeCombo.getSelectedItem();
            String notes = notesArea.getText().trim();

            if (tel.isEmpty() || (type == TypeCommande.DOMICILE && adresse.isEmpty())) {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir tous les champs requis",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (panier.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Votre panier est vide, impossible de passer commande",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!tel.equals(client.getTelephone()) || !adresse.equals(client.getAdresse())) {
                ClientManager clientManager = new ClientManager();
                clientManager.modifierClient(client.getLogin(), adresse, tel);
                client.setAdresse(adresse);
                client.setTelephone(tel);
            }

            int orderId = commandeManager.getNextId();
            Map<Produit, Integer> orderProducts = new HashMap<>(panier);
            Commande commande = new Commande(orderId, client, orderProducts, type, adresse);

            if (!notes.isEmpty()) {
                commande.setCommentaire(notes);
            }

            commandeManager.ajouterCommande(commande);
            panier.clear();

            dialog.dispose();
            showOrderSuccessDialog(orderId);
        });

        dialog.setVisible(true);
    }

    private void showOrderSuccessDialog(int orderId) {
        JDialog successDialog = new JDialog(this, "Commande Confirmée", true);
        successDialog.setSize(400, 300);
        successDialog.setLayout(new BorderLayout());
        UIUtils.centerOnScreen(successDialog);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        ImageIcon successIcon = new ImageIcon("resources/icons/chinese-lantern.png");
        Image scaled = successIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaled), JLabel.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Votre commande a été confirmée!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel detailLabel = new JLabel("Commande #" + orderId);
        detailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        detailLabel.setHorizontalAlignment(JLabel.CENTER);

        messagePanel.add(titleLabel, BorderLayout.NORTH);
        messagePanel.add(detailLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFocusPainted(false);
        okButton.setBackground(new Color(57, 105, 138));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(messagePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        successDialog.add(panel);

        okButton.addActionListener(e -> successDialog.dispose());

        successDialog.setVisible(true);
    }

    private void showProfileDialog() {
        JDialog dialog = new JDialog(this, "Mon profil", true);
        dialog.setSize(550, 450);
        dialog.setLayout(new BorderLayout());
        UIUtils.centerOnScreen(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new BorderLayout(15, 0));
        infoPanel.setOpaque(false);

        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(100, 100));

        ImageIcon avatarIcon = new ImageIcon("resources/icons/user.png");
        Image scaled = avatarIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        JLabel avatarLabel = new JLabel(new ImageIcon(scaled), JLabel.CENTER);

        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        JPanel basicInfoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        basicInfoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(client.getPrenom() + " " + client.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel loginLabel = new JLabel("Login: " + client.getLogin());
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = client.getDateNaissance() != null ? sdf.format(client.getDateNaissance()) : "N/A";
        JLabel dobLabel = new JLabel("Date de naissance: " + dateStr);
        dobLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        basicInfoPanel.add(nameLabel);
        basicInfoPanel.add(loginLabel);
        basicInfoPanel.add(dobLabel);

        infoPanel.add(avatarPanel, BorderLayout.WEST);
        infoPanel.add(basicInfoPanel, BorderLayout.CENTER);

        JPanel editPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        editPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Information de contact"));

        editPanel.add(new JLabel("Adresse:"));
        JTextField adresseField = new JTextField(client.getAdresse(), 20);
        adresseField.setFont(new Font("Arial", Font.PLAIN, 14));
        editPanel.add(adresseField);

        editPanel.add(new JLabel("Téléphone:"));
        JTextField telField = new JTextField(client.getTelephone(), 20);
        telField.setFont(new Font("Arial", Font.PLAIN, 14));
        editPanel.add(telField);

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Statistiques des commandes"));

        int totalOrders = commandeManager.getCommandesClient(client).size();
        double totalSpent = 0;
        for (Commande c : commandeManager.getCommandesClient(client)) {
            totalSpent += c.getTotal();
        }

        statsPanel.add(new JLabel("Nombre de commandes:"));
        JLabel ordersCountLabel = new JLabel(String.valueOf(totalOrders), JLabel.RIGHT);
        ordersCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(ordersCountLabel);

        statsPanel.add(new JLabel("Total dépensé:"));
        JLabel spentLabel = new JLabel(UIUtils.formatPrice(totalSpent), JLabel.RIGHT);
        spentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(spentLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton changePasswordBtn = new JButton("Changer le mot de passe");
        changePasswordBtn.setFocusPainted(false);

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFocusPainted(false);

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.setFocusPainted(false);
        saveBtn.setBackground(new Color(57, 105, 138));
        saveBtn.setForeground(Color.WHITE);

        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(editPanel, BorderLayout.NORTH);
        centerPanel.add(statsPanel, BorderLayout.CENTER);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String adresse = adresseField.getText().trim();
            String tel = telField.getText().trim();

            if (adresse.isEmpty() || tel.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir tous les champs",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClientManager clientManager = new ClientManager();
            if (clientManager.modifierClient(client.getLogin(), adresse, tel)) {
                client.setAdresse(adresse);
                client.setTelephone(tel);
                JOptionPane.showMessageDialog(dialog,
                        "Profil mis à jour avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la mise à jour du profil",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());

        dialog.setVisible(true);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Changement de mot de passe", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        UIUtils.centerOnScreen(dialog);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.weightx = 1.0;

        gc.gridx = 0;
        gc.gridy = 0;
        mainPanel.add(new JLabel("Mot de passe actuel:"), gc);

        gc.gridx = 1;
        JPasswordField currentPassField = new JPasswordField(20);
        mainPanel.add(currentPassField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        mainPanel.add(new JLabel("Nouveau mot de passe:"), gc);

        gc.gridx = 1;
        JPasswordField newPassField = new JPasswordField(20);
        mainPanel.add(newPassField, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        mainPanel.add(new JLabel("Confirmer mot de passe:"), gc);

        gc.gridx = 1;
        JPasswordField confirmPassField = new JPasswordField(20);
        mainPanel.add(confirmPassField, gc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFocusPainted(false);

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.setFocusPainted(false);
        saveBtn.setBackground(new Color(57, 105, 138));
        saveBtn.setForeground(Color.WHITE);

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir tous les champs",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog,
                        "Les nouveaux mots de passe ne correspondent pas",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClientManager clientManager = new ClientManager();
            if (clientManager.changerMotDePasse(client.getLogin(), newPass)) {
                JOptionPane.showMessageDialog(dialog,
                        "Mot de passe changé avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Mot de passe actuel incorrect ou erreur système",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // Create a custom look and feel setup
    public void setupModernLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Color RED_CHINESE = new Color(190, 0, 0);
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color BG_COLOR = new Color(251, 246, 240);
        Color DARK_RED = new Color(128, 0, 0);

        UIManager.put("control", BG_COLOR);
        UIManager.put("info", Color.WHITE);
        UIManager.put("nimbusBase", RED_CHINESE);
        UIManager.put("nimbusBlueGrey", GOLD_CHINESE);
        UIManager.put("nimbusSelectionBackground", RED_CHINESE);
        UIManager.put("nimbusFocus", GOLD_CHINESE);
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("text", DARK_RED);

        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 13));
        UIManager.put("TextArea.font", new Font("Arial", Font.PLAIN, 13));
        UIManager.put("ComboBox.font", new Font("Arial", Font.PLAIN, 13));
    }

    private void showOrderDetailsDialog(Commande commande) {
        JDialog dialog = new JDialog(this, "Détails de la commande #" + commande.getId(), true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        UIUtils.centerOnScreen(dialog);

        JPanel orderPanel = new JPanel(new GridLayout(4, 2));
        orderPanel.setBorder(BorderFactory.createTitledBorder("Commande"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        orderPanel.add(new JLabel("Type:"));
        orderPanel.add(new JLabel(commande.getTypeCommande().toString()));

        orderPanel.add(new JLabel("État:"));
        orderPanel.add(new JLabel(commande.getEtat().toString()));

        orderPanel.add(new JLabel("Date:"));
        orderPanel.add(new JLabel(sdf.format(commande.getDateCommande())));

        orderPanel.add(new JLabel("Total:"));
        orderPanel.add(new JLabel(UIUtils.formatPrice(commande.getTotal())));

        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(BorderFactory.createTitledBorder("Produits"));

        String[] columns = { "Produit", "Prix unitaire", "Quantité", "Total" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable productsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(productsTable);
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

        JPanel commentPanel = new JPanel(new BorderLayout());
        if (commande.getCommentaire() != null && !commande.getCommentaire().isEmpty()) {
            commentPanel.setBorder(BorderFactory.createTitledBorder("Commentaires"));
            JTextArea commentArea = new JTextArea(commande.getCommentaire(), 3, 30);
            commentArea.setEditable(false);
            commentArea.setLineWrap(true);
            commentArea.setWrapStyleWord(true);
            JScrollPane commentScroll = new JScrollPane(commentArea);
            commentPanel.add(commentScroll, BorderLayout.CENTER);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(orderPanel, BorderLayout.NORTH);
        mainPanel.add(productsPanel, BorderLayout.CENTER);
        if (commentPanel.getComponents().length != 0) {
            mainPanel.add(commentPanel, BorderLayout.SOUTH);
        }

        JPanel buttonPanel = new JPanel();
        JButton closeBtn = new JButton("Fermer");
        buttonPanel.add(closeBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

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
                new ClientFrame.RoundedBorder(RED_CHINESE, 10),
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

    private JButton createCircleButton(String text, Color bgColor, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(borderColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(35, 35));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillOval(0, 0, c.getWidth(), c.getHeight());
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(0, 0, c.getWidth() - 1, c.getHeight() - 1);

                super.paint(g2, c);
            }
        });

        return button;
    }

    private JButton createNavButton(String text, ImageIcon icon) {
        JButton btn = new JButton(text, icon);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private JPanel createAutoScrollingBanner(MenuManager menuManager) {
        Color GOLD_CHINESE = new Color(212, 175, 55);
        Color DARK_RED = new Color(128, 0, 0);
        Color BG_COLOR = new Color(251, 246, 240);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.setBackground(BG_COLOR);

        JPanel bannerPanel = new JPanel();
        bannerPanel.setLayout(new BoxLayout(bannerPanel, BoxLayout.X_AXIS));
        bannerPanel.setOpaque(false);

        bannerPanel.add(Box.createHorizontalStrut(20));

        for (Produit produit : menuManager.getProduits()) {
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 15, 15);

                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.setColor(GOLD_CHINESE);
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(160, 140)); // Reduced height
            card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            try {
                String urlStr = produit.getImageUrl();
                if (urlStr != null && !urlStr.isEmpty()) {
                    URL url = new URL(urlStr);
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);

                    Image img = ImageIO.read(connection.getInputStream());
                    if (img != null) {
                        img = img.getScaledInstance(130, 80, Image.SCALE_SMOOTH);
                        JLabel imgLabel = new JLabel(new ImageIcon(img));
                        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        imgLabel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
                        card.add(imgLabel);
                    } else {
                        throw new IOException("L'image n'a pas pu être chargée");
                    }
                } else {
                    JLabel fallback = new JLabel("🍜", JLabel.CENTER);
                    fallback.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                    fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
                    card.add(fallback);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JLabel fallback = new JLabel("Image ?", JLabel.CENTER);
                fallback.setFont(new Font("Arial", Font.ITALIC, 12));
                fallback.setForeground(Color.GRAY);
                fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(fallback);
            }

            JLabel nameLabel = new JLabel(produit.getNom());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
            nameLabel.setForeground(DARK_RED);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

            card.add(nameLabel);
            bannerPanel.add(Box.createHorizontalStrut(10));
            bannerPanel.add(card);
        }

        bannerPanel.add(Box.createHorizontalStrut(20));

        JScrollPane scrollPane = new JScrollPane(bannerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(960, 160));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(2);

        wrapper.add(scrollPane, BorderLayout.CENTER);

        Timer timer = new Timer(50, new ActionListener() {
            int scrollX = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                scrollX += 1;
                JScrollBar hBar = scrollPane.getHorizontalScrollBar();
                if (scrollX >= hBar.getMaximum() - hBar.getVisibleAmount()) {
                    scrollX = 0;
                }
                hBar.setValue(scrollX);
            }
        });
        timer.start();

        return wrapper;
    }
}
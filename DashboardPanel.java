import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import org.jfree.data.time.*;

/**
 * Updated Dashboard Panel with automatic refresh functionality
 */
public class DashboardPanel extends JPanel {
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);

    private final ClientManager clientManager;
    private final CommandeManager commandeManager;
    private final MenuManager menuManager;
    private JLabel lastUpdateLabel;
    private JPanel orderStatusChartPanel;
    private JPanel weeklyOrdersChartPanel;
    private JPanel revenueTrendChartPanel;
    private DefaultTableModel recentOrdersModel;
    private Date lastRefreshTime;
    private DashboardCard clientCard;
    private DashboardCard orderCard;
    private DashboardCard productCard;
    private Timer autoRefreshTimer;
    private JCheckBox autoRefreshToggle;
    private JComboBox<String> refreshIntervalCombo;

    /**
     * Constructor
     */
    public DashboardPanel(ClientManager clientManager, CommandeManager commandeManager, MenuManager menuManager) {
        this.clientManager = clientManager;
        this.commandeManager = commandeManager;
        this.menuManager = menuManager;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();

        refreshDashboard();
    }

    /**
     * Initialize all UI components
     */
    private void initComponents() {
        JPanel headerPanel = createHeaderPanel();
        JPanel summaryPanel = createSummaryPanel();
        JPanel chartsPanel = createChartsPanel();
        JPanel recentOrdersPanel = createRecentOrdersPanel();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(chartsPanel, BorderLayout.NORTH);
        contentPanel.add(recentOrdersPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(summaryPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
    }

    /**
     * Create header panel with refresh controls
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        lastUpdateLabel = new JLabel("Dernière mise à jour: Jamais");
        lastUpdateLabel.setFont(REGULAR_FONT);

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setFont(BUTTON_FONT);
        refreshButton.setFocusPainted(false);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/refresh.png"));
            refreshButton.setIcon(icon);
        } catch (Exception e) {
            refreshButton.setText("↻ Actualiser");
        }
        refreshButton.addActionListener(e -> refreshDashboard());

        autoRefreshToggle = new JCheckBox("Actualisation automatique");
        autoRefreshToggle.setFont(REGULAR_FONT);
        autoRefreshToggle.setOpaque(false);

        String[] refreshIntervals = { "30 secondes", "1 minute", "5 minutes", "15 minutes" };
        refreshIntervalCombo = new JComboBox<>(refreshIntervals);
        refreshIntervalCombo.setSelectedIndex(1);
        refreshIntervalCombo.setEnabled(false);

        autoRefreshTimer = new Timer(60000, e -> refreshDashboard());

        autoRefreshToggle.addActionListener(e -> {
            if (autoRefreshToggle.isSelected()) {
                updateRefreshInterval();
                autoRefreshTimer.start();
                refreshIntervalCombo.setEnabled(true);
            } else {
                autoRefreshTimer.stop();
                refreshIntervalCombo.setEnabled(false);
            }
        });

        refreshIntervalCombo.addActionListener(e -> {
            if (autoRefreshToggle.isSelected()) {
                updateRefreshInterval();
            }
        });

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(lastUpdateLabel);

        JPanel refreshControlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshControlsPanel.setOpaque(false);
        refreshControlsPanel.add(autoRefreshToggle);
        refreshControlsPanel.add(refreshIntervalCombo);
        refreshControlsPanel.add(refreshButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(refreshControlsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Update the auto-refresh timer interval based on combo box selection
     */
    private void updateRefreshInterval() {
        int interval;
        switch (refreshIntervalCombo.getSelectedIndex()) {
            case 0:
                interval = 30000;
                break;
            case 2:
                interval = 300000;
                break;
            case 3:
                interval = 900000;
                break;
            default:
                interval = 60000;
                break;
        }

        autoRefreshTimer.stop();
        autoRefreshTimer.setDelay(interval);
        autoRefreshTimer.start();
    }

    /**
     * Create summary cards panel
     */
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        clientCard = createDashboardCard("Clients",
                String.valueOf(clientManager.getClients().size()),
                new Color(46, 204, 113));

        int activeOrders = 0;
        for (Commande c : commandeManager.getCommandes()) {
            if (c.getEtat() != EtatCommande.LIVREE && c.getEtat() != EtatCommande.ANNULEE) {
                activeOrders++;
            }
        }
        orderCard = createDashboardCard("Commandes actives",
                String.valueOf(activeOrders),
                new Color(52, 152, 219));

        productCard = createDashboardCard("Produits au menu",
                String.valueOf(menuManager.getProduits().size()),
                new Color(155, 89, 182));

        summaryPanel.add(clientCard.getPanel());
        summaryPanel.add(orderCard.getPanel());
        summaryPanel.add(productCard.getPanel());

        return summaryPanel;
    }

    /**
     * Create charts panel with all three charts
     */
    private JPanel createChartsPanel() {
        JPanel chartsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        orderStatusChartPanel = createOrderStatusPieChart();
        weeklyOrdersChartPanel = createWeeklyOrdersBarChart();
        revenueTrendChartPanel = createRevenueTrendChart();

        chartsPanel.add(orderStatusChartPanel);
        chartsPanel.add(weeklyOrdersChartPanel);
        chartsPanel.add(revenueTrendChartPanel);

        return chartsPanel;
    }

    /**
     * Create recent orders panel with table
     */
    private JPanel createRecentOrdersPanel() {
        JPanel recentOrdersPanel = new JPanel(new BorderLayout());
        recentOrdersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(15, 0, 0, 0),
                        BorderFactory.createLineBorder(new Color(189, 195, 199))),
                "Commandes récentes",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        recentOrdersPanel.setBackground(Color.WHITE);

        recentOrdersModel = new DefaultTableModel(
                new String[] { "#", "Client", "Type", "État", "Date", "Total" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable recentOrdersTable = new JTable(recentOrdersModel);
        configureTable(recentOrdersTable);

        recentOrdersPanel.add(new JScrollPane(recentOrdersTable), BorderLayout.CENTER);

        return recentOrdersPanel;
    }

    /**
     * Configure table appearance and behavior
     */
    private void configureTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.getTableHeader().setFont(REGULAR_FONT.deriveFont(Font.BOLD));
        table.setRowHeight(25);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 1));
    }

    /**
     * Creates a dashboard summary card with a title and value
     */
    private DashboardCard createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel indicator = new JPanel();
        indicator.setBackground(color);
        indicator.setPreferredSize(new Dimension(10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(REGULAR_FONT);
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(color);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        card.add(indicator, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);

        return new DashboardCard(card, valueLabel);
    }

    /**
     * Creates a pie chart showing the distribution of order statuses
     */
    private JPanel createOrderStatusPieChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 0),
                        BorderFactory.createLineBorder(new Color(189, 195, 199))),
                "État des commandes",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        panel.setBackground(Color.WHITE);

        Map<EtatCommande, Integer> statusCounts = new HashMap<>();
        for (Commande c : commandeManager.getCommandes()) {
            statusCounts.put(c.getEtat(), statusCounts.getOrDefault(c.getEtat(), 0) + 1);
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<EtatCommande, Integer> entry : statusCounts.entrySet()) {
            dataset.setValue(entry.getKey().toString(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        plot.setSectionPaint(EtatCommande.NON_TRAITEE.toString(), new Color(241, 196, 15)); // Yellow
        plot.setSectionPaint(EtatCommande.EN_PREPARATION.toString(), new Color(230, 126, 34)); // Orange
        plot.setSectionPaint(EtatCommande.PRETE.toString(), new Color(52, 152, 219)); // Blue
        plot.setSectionPaint(EtatCommande.EN_ROUTE.toString(), new Color(155, 89, 182)); // Purple
        plot.setSectionPaint(EtatCommande.LIVREE.toString(), new Color(46, 204, 113)); // Green
        plot.setSectionPaint(EtatCommande.ANNULEE.toString(), new Color(231, 76, 60)); // Red

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a bar chart showing the number of orders for each day of the current
     * week
     */
    private JPanel createWeeklyOrdersBarChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 0),
                        BorderFactory.createLineBorder(new Color(189, 195, 199))),
                "Commandes de la semaine",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        panel.setBackground(Color.WHITE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date startOfWeek = calendar.getTime();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String[] daysOfWeek = { "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim" };

        Map<Integer, Integer> ordersByDay = new HashMap<>();
        Map<Integer, Double> revenueByDay = new HashMap<>();

        for (Commande c : commandeManager.getCommandes()) {
            Calendar orderCal = Calendar.getInstance();
            orderCal.setTime(c.getDateCommande());

            Calendar orderWeekStart = (Calendar) orderCal.clone();
            orderWeekStart.set(Calendar.DAY_OF_WEEK, orderWeekStart.getFirstDayOfWeek());

            if (isSameDay(orderWeekStart, calendar)) {
                int dayOfWeek = orderCal.get(Calendar.DAY_OF_WEEK);

                int index = (dayOfWeek - calendar.getFirstDayOfWeek() + 7) % 7;

                ordersByDay.put(index, ordersByDay.getOrDefault(index, 0) + 1);

                revenueByDay.put(index, revenueByDay.getOrDefault(index, 0.0) + c.getTotal());
            }
        }

        for (int i = 0; i < 7; i++) {
            dataset.addValue(ordersByDay.getOrDefault(i, 0), "Nombre", daysOfWeek[i]);
            dataset.addValue(revenueByDay.getOrDefault(i, 0.0) / 100, "CA (€)", daysOfWeek[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Jour",
                "Valeur",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(new Color(189, 195, 199));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setSeriesPaint(1, new Color(46, 204, 113));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a line chart showing revenue trend over time
     */
    private JPanel createRevenueTrendChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 0),
                        BorderFactory.createLineBorder(new Color(189, 195, 199))),
                "Évolution du chiffre d'affaires",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT));
        panel.setBackground(Color.WHITE);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("CA Quotidien");

        List<Commande> commandes = commandeManager.getCommandes();
        Collections.sort(commandes, Comparator.comparing(Commande::getDateCommande));

        Map<String, Double> dailyRevenue = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Commande c : commandes) {
            String day = dateFormat.format(c.getDateCommande());
            dailyRevenue.put(day, dailyRevenue.getOrDefault(day, 0.0) + c.getTotal());
        }

        for (Map.Entry<String, Double> entry : dailyRevenue.entrySet()) {
            try {
                Date date = dateFormat.parse(entry.getKey());
                series.add(new Day(date), entry.getValue() / 100);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null,
                "Date",
                "CA (€)",
                dataset,
                true,
                true,
                false);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(189, 195, 199));
        plot.setRangeGridlinePaint(new Color(189, 195, 199));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(231, 76, 60));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Helper method to check if two calendar dates are the same day
     */
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Update the dashboard with fresh data
     */
    public void refreshDashboard() {
        lastRefreshTime = new Date();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                JDialog dialog = new JDialog();
                dialog.setUndecorated(true);

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));

                JLabel label = new JLabel("Actualisation du tableau de bord...");
                label.setFont(REGULAR_FONT);

                panel.add(label, BorderLayout.CENTER);
                dialog.add(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(DashboardPanel.this);

                dialog.setVisible(true);

                try {
                    updateSummaryCards();

                    updateCharts();

                    updateRecentOrdersTable();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    lastUpdateLabel.setText("Dernière mise à jour: " + sdf.format(lastRefreshTime));
                    Thread.sleep(500);
                } finally {
                    dialog.dispose();
                }

                return null;
            }

            @Override
            protected void done() {
                revalidate();
                repaint();
            }
        };

        worker.execute();
    }

    /**
     * Update the summary cards with fresh data
     */
    private void updateSummaryCards() {
        clientCard.updateValue(String.valueOf(clientManager.getClients().size()));

        int activeOrders = 0;
        for (Commande c : commandeManager.getCommandes()) {
            if (c.getEtat() != EtatCommande.LIVREE && c.getEtat() != EtatCommande.ANNULEE) {
                activeOrders++;
            }
        }
        orderCard.updateValue(String.valueOf(activeOrders));

        productCard.updateValue(String.valueOf(menuManager.getProduits().size()));
    }

    /**
     * Update all charts with fresh data
     */
    private void updateCharts() {
        Container chartsContainer = orderStatusChartPanel.getParent();

        JPanel newOrderStatusChart = createOrderStatusPieChart();
        JPanel newWeeklyOrdersChart = createWeeklyOrdersBarChart();
        JPanel newRevenueTrendChart = createRevenueTrendChart();

        chartsContainer.remove(orderStatusChartPanel);
        chartsContainer.remove(weeklyOrdersChartPanel);
        chartsContainer.remove(revenueTrendChartPanel);

        chartsContainer.add(newOrderStatusChart);
        chartsContainer.add(newWeeklyOrdersChart);
        chartsContainer.add(newRevenueTrendChart);

        orderStatusChartPanel = newOrderStatusChart;
        weeklyOrdersChartPanel = newWeeklyOrdersChart;
        revenueTrendChartPanel = newRevenueTrendChart;
    }

    /**
     * Update the recent orders table with fresh data
     */
    private void updateRecentOrdersTable() {
        recentOrdersModel.setRowCount(0);

        List<Commande> commandes = commandeManager.getCommandes();
        Collections.sort(commandes, (c1, c2) -> c2.getDateCommande().compareTo(c1.getDateCommande()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        int count = 0;
        for (Commande c : commandes) {
            if (count++ >= 5)
                break;
            recentOrdersModel.addRow(new Object[] {
                    c.getId(),
                    c.getClient().getNom() + " " + c.getClient().getPrenom(),
                    c.getTypeCommande(),
                    c.getEtat(),
                    sdf.format(c.getDateCommande()),
                    UIUtils.formatPrice(c.getTotal())
            });
        }
    }

    /**
     * Class to hold a dashboard card panel and its value label for updating
     */

    private static class DashboardCard {
        private final JPanel panel;
        private final JLabel valueLabel;

        public DashboardCard(JPanel panel, JLabel valueLabel) {
            this.panel = panel;
            this.valueLabel = valueLabel;
        }

        public JPanel getPanel() {
            return panel;
        }

        public void updateValue(String value) {
            valueLabel.setText(value);
        }
    }

}
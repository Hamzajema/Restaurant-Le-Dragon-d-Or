import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONArray;

public class RestaurantNutritionalAnalyzer extends JFrame {
    private final JTextField dishNameField;
    private final JTextArea dishDescriptionArea;
    private final JTextArea responseArea;
    private final JButton analyzeButton;
    private final JButton exitButton;
    private final JButton chatButton;
    private final Color deepRedColor = new Color(178, 0, 0);
    private final Color goldColor = new Color(218, 183, 107);
    private final Color creamColor = new Color(255, 250, 227);
    private final Color darkTextColor = new Color(50, 30, 0);
    private final Color backgroundColor = new Color(250, 245, 225);
    private final Font mainFont = new Font("Serif", Font.PLAIN, 14);
    private final Font headerFont = new Font("Serif", Font.BOLD, 24);
    private final Font buttonFont = new Font("Serif", Font.BOLD, 14);
    private final Font titleFont = new Font("Serif", Font.BOLD, 28);

    // API key for the Groq LLM service
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions"; // Groq API endpoint

    public RestaurantNutritionalAnalyzer() {
        setTitle("Le Dragon D'Or - Analyse Nutritionnelle");
        setSize(850, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.background", goldColor);
            UIManager.put("Button.foreground", deepRedColor);
            UIManager.put("TextField.background", creamColor);
            UIManager.put("TextArea.background", creamColor);
            UIManager.put("Panel.background", backgroundColor);
            UIManager.put("Label.foreground", deepRedColor);
            UIManager.put("Label.font", mainFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);
        JPanel chineseCharPanel = new JPanel(new BorderLayout());
        chineseCharPanel.setPreferredSize(new Dimension(80, 80));
        chineseCharPanel.setBackground(deepRedColor);

        JLabel chineseCharLabel = new JLabel("福", SwingConstants.CENTER);
        chineseCharLabel.setFont(new Font("SimSun", Font.BOLD, 60));
        chineseCharLabel.setForeground(goldColor);
        chineseCharPanel.add(chineseCharLabel, BorderLayout.CENTER);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(deepRedColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(deepRedColor);

        JLabel titleLabel = new JLabel("LE DRAGON D'OR", SwingConstants.LEFT);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(goldColor);

        JLabel subtitleLabel = new JLabel("CUISINE RAFFINÉE CHINOISE", SwingConstants.LEFT);
        subtitleLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        subtitleLabel.setForeground(goldColor);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        headerPanel.add(chineseCharPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(backgroundColor);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(goldColor, 2),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(creamColor, 3),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        JLabel sectionLabel = new JLabel("Analyse Nutritionnelle", SwingConstants.CENTER);
        sectionLabel.setFont(headerFont);
        sectionLabel.setForeground(deepRedColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        inputPanel.add(sectionLabel, gbc);
        JPanel decorLine = new JPanel();
        decorLine.setPreferredSize(new Dimension(100, 2));
        decorLine.setBackground(goldColor);
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        inputPanel.add(decorLine, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);
        JLabel dishNameLabel = new JLabel("Nom du Plat:", SwingConstants.RIGHT);
        dishNameLabel.setFont(mainFont);
        dishNameLabel.setForeground(deepRedColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(dishNameLabel, gbc);

        dishNameField = new JTextField(20);
        dishNameField.setFont(mainFont);
        dishNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(goldColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(dishNameField, gbc);
        JLabel dishDescLabel = new JLabel("Description:", SwingConstants.RIGHT);
        dishDescLabel.setFont(mainFont);
        dishDescLabel.setForeground(deepRedColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        inputPanel.add(dishDescLabel, gbc);

        dishDescriptionArea = new JTextArea(3, 20);
        dishDescriptionArea.setFont(mainFont);
        dishDescriptionArea.setLineWrap(true);
        dishDescriptionArea.setWrapStyleWord(true);
        dishDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(goldColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        JScrollPane descScrollPane = new JScrollPane(dishDescriptionArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(goldColor));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(descScrollPane, gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(backgroundColor);

        analyzeButton = createStyledButton("ANALYSER", goldColor, deepRedColor);
        chatButton = createStyledButton("ASSISTANT", goldColor, deepRedColor);
        exitButton = createStyledButton("QUITTER", goldColor, deepRedColor);

        buttonPanel.add(analyzeButton);
        buttonPanel.add(chatButton);
        buttonPanel.add(exitButton);
        JPanel decorLeftPanel = new JPanel();
        decorLeftPanel.setPreferredSize(new Dimension(30, 30));
        decorLeftPanel.setBackground(backgroundColor);
        JLabel decorLeft = new JLabel("✧");
        decorLeft.setForeground(deepRedColor);
        decorLeftPanel.add(decorLeft);

        JPanel decorRightPanel = new JPanel();
        decorRightPanel.setPreferredSize(new Dimension(30, 30));
        decorRightPanel.setBackground(backgroundColor);
        JLabel decorRight = new JLabel("✧");
        decorRight.setForeground(deepRedColor);
        decorRightPanel.add(decorRight);

        buttonPanel.add(decorLeftPanel, 0);
        buttonPanel.add(decorRightPanel);
        responseArea = new JTextArea(15, 40);
        responseArea.setEditable(false);
        responseArea.setFont(mainFont);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        responseArea.setBackground(creamColor);
        responseArea.setForeground(darkTextColor);

        JScrollPane responseScrollPane = new JScrollPane(responseArea);
        responseScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(goldColor, 2),
                        BorderFactory.createLineBorder(creamColor, 3))));
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeNutritionalContent(dishNameField.getText(), dishDescriptionArea.getText());
            }
        });

        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openChatDialog();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        RestaurantNutritionalAnalyzer.this,
                        "Êtes-vous sûr de vouloir quitter l'application?",
                        "Confirmation de Sortie",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (option == JOptionPane.YES_OPTION) {
                    Window window = SwingUtilities.getWindowAncestor(exitButton);
                    window.dispose();
                }
            }
        });
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.NORTH);
        add(responseScrollPane, BorderLayout.CENTER);
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(backgroundColor);
        JLabel copyrightLabel = new JLabel("© 2025 LE DRAGON D'OR");
        copyrightLabel.setForeground(deepRedColor);
        copyrightLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        JLabel lanternLeft1 = new JLabel("🏮");
        JLabel lanternLeft2 = new JLabel("🏮");
        JLabel lanternRight1 = new JLabel("🏮");
        JLabel lanternRight2 = new JLabel("🏮");
        footerPanel.add(lanternLeft1);
        footerPanel.add(lanternLeft2);
        footerPanel.add(copyrightLabel);
        footerPanel.add(lanternRight1);
        footerPanel.add(lanternRight2);

        add(footerPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 36));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(deepRedColor, 1),
                BorderFactory.createEmptyBorder(6, 20, 6, 20)));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 200, 140));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void analyzeNutritionalContent(String dishName, String dishDescription) {
        if (dishName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez saisir un nom de plat à analyser.",
                    "Saisie Requise",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            responseArea.setText("Analyse nutritionnelle en cours, veuillez patienter...");
            analyzeButton.setEnabled(false);
            new Thread(() -> {
                try {
                    String result = callNutritionalAPI(dishName, dishDescription);
                    SwingUtilities.invokeLater(() -> {
                        responseArea.setText(result);
                        analyzeButton.setEnabled(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        responseArea.setText("Erreur lors de l'analyse nutritionnelle: " + ex.getMessage());
                        analyzeButton.setEnabled(true);
                    });
                }
            }).start();
        } catch (Exception e) {
            responseArea.setText("Erreur: " + e.getMessage());
            analyzeButton.setEnabled(true);
        }
    }

    // Create a dialog for chat interaction with the Dragon D'Or theme
    private void openChatDialog() {
        JDialog chatDialog = new JDialog(this, "Assistant Nutritionnel - Le Dragon D'Or", true);
        chatDialog.setSize(600, 500);
        chatDialog.setLayout(new BorderLayout(10, 10));
        JPanel chatHeaderPanel = new JPanel(new BorderLayout());
        chatHeaderPanel.setBackground(deepRedColor);
        chatHeaderPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel chatTitleLabel = new JLabel("ASSISTANT NUTRITIONNEL", SwingConstants.CENTER);
        chatTitleLabel.setFont(headerFont);
        chatTitleLabel.setForeground(goldColor);
        chatHeaderPanel.add(chatTitleLabel, BorderLayout.CENTER);

        // Chat history area
        JTextArea chatHistoryArea = new JTextArea();
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setFont(mainFont);
        chatHistoryArea.setBackground(creamColor);
        chatHistoryArea.setForeground(darkTextColor);
        chatHistoryArea.setLineWrap(true);
        chatHistoryArea.setWrapStyleWord(true);
        chatHistoryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane chatScrollPane = new JScrollPane(chatHistoryArea);
        chatScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                BorderFactory.createLineBorder(goldColor)));
        JTextArea userInputArea = new JTextArea(3, 20);
        userInputArea.setFont(mainFont);
        userInputArea.setBackground(Color.WHITE);
        userInputArea.setForeground(darkTextColor);
        userInputArea.setLineWrap(true);
        userInputArea.setWrapStyleWord(true);

        JScrollPane inputScrollPane = new JScrollPane(userInputArea);
        inputScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createLineBorder(goldColor)));
        JButton sendButton = createStyledButton("ENVOYER", goldColor, deepRedColor);
        sendButton.setPreferredSize(new Dimension(120, 36));

        JButton closeChatButton = createStyledButton("FERMER", goldColor, deepRedColor);
        closeChatButton.setPreferredSize(new Dimension(120, 36));

        JPanel chatButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        chatButtonPanel.setBackground(backgroundColor);
        chatButtonPanel.add(sendButton);
        chatButtonPanel.add(closeChatButton);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(backgroundColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JLabel messageLabel = new JLabel("Votre message:");
        messageLabel.setFont(mainFont);
        messageLabel.setForeground(deepRedColor);

        inputPanel.add(messageLabel, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(chatButtonPanel, BorderLayout.SOUTH);
        chatHistoryArea.setText("Assistant Nutritionnel: Bonjour et bienvenue au Dragon D'Or ! " +
                "Je suis votre assistant nutritionnel. Comment puis-je vous aider aujourd'hui avec vos questions diététiques ?\n\n");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userMessage = userInputArea.getText().trim();
                if (!userMessage.isEmpty()) {
                    chatHistoryArea.append("Vous: " + userMessage + "\n\n");
                    userInputArea.setText("");
                    new Thread(() -> {
                        try {
                            String response = callChatAPI(userMessage);
                            SwingUtilities.invokeLater(() -> {
                                chatHistoryArea.append("Assistant Nutritionnel: " + response + "\n\n");
                                // Scroll to the bottom
                                chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                chatHistoryArea
                                        .append("Assistant Nutritionnel: Je suis désolé, j'ai rencontré une erreur. " +
                                                "Veuillez réessayer plus tard.\n\n");
                            });
                        }
                    }).start();
                }
            }
        });
        userInputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    sendButton.doClick();
                    e.consume();
                }
            }
        });

        closeChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatDialog.dispose();
            }
        });
        chatDialog.add(chatHeaderPanel, BorderLayout.NORTH);
        chatDialog.add(chatScrollPane, BorderLayout.CENTER);
        chatDialog.add(inputPanel, BorderLayout.SOUTH);
        chatDialog.setLocationRelativeTo(this);
        chatDialog.setVisible(true);
    }

    private String callNutritionalAPI(String dishName, String dishDescription) {
        try {
            // Create connection to API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // Create the prompt for the LM API
            String prompt = "Veuillez analyser le plat chinois suivant et fournir:\n" +
                    "1. Estimation des calories par portion\n" +
                    "2. Répartition nutritionnelle (protéines, glucides, lipides)\n" +
                    "3. Bienfaits traditionnels chinois pour la santé\n" +
                    "4. Conseils santé pour le consommateur\n\n" +
                    "Nom du Plat: " + dishName + "\n" +
                    "Description: " + (dishDescription.isEmpty() ? "Aucune description fournie." : dishDescription);

            // Create request JSON for Groq API (OpenAI compatible format)
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "llama3-70b-8192");

            // Create messages array with system and user message
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "Vous êtes un expert en nutrition spécialisé dans la cuisine chinoise qui analyse les plats et fournit des estimations précises de calories, des informations nutritionnelles et des conseils de santé en mettant l'accent sur les principes de la médecine traditionnelle chinoise.");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            // Add messages to request
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(systemMessage);
            messagesArray.put(userMessage);

            requestBody.put("messages", messagesArray);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.2);

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            String nutritionalAnalysis = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return "🔍 ANALYSE NUTRITIONNELLE POUR: " + dishName.toUpperCase() + "\n\n" + nutritionalAnalysis;

        } catch (Exception e) {
            return "Erreur: Impossible d'analyser le contenu nutritionnel. " + e.getMessage();
        }
    }

    // Create connection to API , Create request JSON for Groq API
    private String callChatAPI(String userQuestion) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "llama3-70b-8192");
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "Vous êtes un expert en nutrition spécialisé dans la cuisine chinoise qui fournit des conseils utiles, précis et concis sur la nutrition, la cuisine chinoise et les choix alimentaires.");
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userQuestion);
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(systemMessage);
            messagesArray.put(userMessage);
            requestBody.put("messages", messagesArray);
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0.3);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            return "Je suis désolé, j'ai rencontré une erreur lors du traitement de votre question. Veuillez réessayer.";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            RestaurantNutritionalAnalyzer app = new RestaurantNutritionalAnalyzer();
            app.setVisible(true);
        });
    }

    // Method to analyze from external call (when a user clicks on a food item
    // elsewhere)
    public void analyzeExternalDish(String dishName, String dishDescription) {
        dishNameField.setText(dishName);
        dishDescriptionArea.setText(dishDescription);
        analyzeNutritionalContent(dishName, dishDescription);
    }
}
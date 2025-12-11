// ... (các import giữ nguyên)
package com.chess.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.chess.engine.ComputerPlayer;

public class GameSetupPanel extends JPanel {
    private ChessFrame frame;
    private boolean vsComputer;
    private int difficultyLevel;
    private int engineType;

    private JTextField whiteNameField;
    private JTextField blackNameField;
    private JComboBox<String> timeControlCombo;
    private JPanel customTimePanel;
    private JSpinner customTimeSpinner;

    public GameSetupPanel(ChessFrame frame, boolean vsComputer, int difficultyLevel, int engineType) {
        this.frame = frame;
        this.vsComputer = vsComputer;
        this.difficultyLevel = difficultyLevel;
        this.engineType = engineType;

        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        JLabel titleLabel = new JLabel("Game Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 220, 220));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(40, 40, 40));
        add(contentPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // White Name
        addLabel(contentPanel, "White Player Name:", gbc);
        gbc.gridy++;
        whiteNameField = new JTextField("Player 1", 20);
        styleTextField(whiteNameField);
        contentPanel.add(whiteNameField, gbc);

        // Black Name
        gbc.gridy++;
        addLabel(contentPanel, "Black Player Name:", gbc);
        gbc.gridy++;

        // Xác định tên Bot mặc định
        String defaultBlackName = "Player 2";
        if (vsComputer) {
            defaultBlackName = (engineType == ComputerPlayer.TYPE_STOCKFISH) ? "Stockfish" : "Java Bot";
        }
        blackNameField = new JTextField(defaultBlackName, 20);
        styleTextField(blackNameField);
        if (vsComputer) {
            blackNameField.setEditable(false);
            blackNameField.setEnabled(false);
        }
        contentPanel.add(blackNameField, gbc);

        gbc.gridy++;
        addLabel(contentPanel, "Time Control:", gbc);
        gbc.gridy++;
        String[] timeOptions = { "3 min", "5 min", "10 min", "15 min", "30 min", "Custom" };
        timeControlCombo = new JComboBox<>(timeOptions);
        timeControlCombo.setSelectedItem("10 min");
        styleComboBox(timeControlCombo);
        contentPanel.add(timeControlCombo, gbc);

        // Custom time panel
        gbc.gridy++;
        customTimePanel = new JPanel(new GridLayout(1, 4, 5, 5));
        customTimePanel.setBackground(new Color(40, 40, 40));
        customTimePanel.setVisible(false);
        JLabel timeLabel = new JLabel("Time (min):");
        timeLabel.setForeground(Color.LIGHT_GRAY);
        customTimePanel.add(timeLabel);
        customTimeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 180, 1));
        customTimePanel.add(customTimeSpinner);
        contentPanel.add(customTimePanel, gbc);

        timeControlCombo.addActionListener(e -> {
            customTimePanel.setVisible("Custom".equals(timeControlCombo.getSelectedItem()));
            revalidate();
            repaint();
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(40, 40, 40));
        JButton startButton = createStyledButton("Start Game");
        JButton backButton = createStyledButton("Back");

        startButton.addActionListener(e -> startGame());
        backButton.addActionListener(e -> frame.showMenu());

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(new Color(220, 220, 220));
        panel.add(label, gbc);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setBackground(new Color(60, 60, 60));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBackground(new Color(60, 60, 60));
        comboBox.setForeground(Color.WHITE);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void startGame() {
        String whiteName = whiteNameField.getText().trim();
        String blackName = blackNameField.getText().trim();

        if (whiteName.isEmpty())
            whiteName = "Player 1";
        if (blackName.isEmpty()) {
            if (vsComputer)
                blackName = (engineType == ComputerPlayer.TYPE_STOCKFISH) ? "Stockfish" : "Java Bot";
            else
                blackName = "Player 2";
        }

        int minutes;
        String selected = (String) timeControlCombo.getSelectedItem();
        if ("Custom".equals(selected)) {
            minutes = (Integer) customTimeSpinner.getValue();
        } else {
            minutes = Integer.parseInt(selected.split(" ")[0]);
        }

        // Truyền engineType vào startNewGame
        frame.startNewGame(vsComputer, difficultyLevel, engineType, whiteName, blackName, minutes);
    }
}
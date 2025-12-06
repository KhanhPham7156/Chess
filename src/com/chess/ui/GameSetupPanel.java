package com.chess.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class GameSetupPanel extends JPanel {
    private ChessFrame frame;
    private boolean vsComputer;
    private int difficultyLevel;

    private JTextField whiteNameField;
    private JTextField blackNameField;
    private JComboBox<String> timeControlCombo;
    private JPanel customTimePanel;
    private JSpinner customTimeSpinner;
    private JSpinner customIncrementSpinner;

    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color BUTTON_COLOR = new Color(70, 70, 70);
    private static final Color HOVER_COLOR = new Color(90, 90, 90);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 36);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public GameSetupPanel(ChessFrame frame, boolean vsComputer, int difficultyLevel) {
        this.frame = frame;
        this.vsComputer = vsComputer;
        this.difficultyLevel = difficultyLevel;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Game Setup", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        add(contentPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // White Player Name
        addLabel(contentPanel, "White Player Name:", gbc);
        gbc.gridy++;
        whiteNameField = new JTextField("Player 1", 20);
        styleTextField(whiteNameField);
        contentPanel.add(whiteNameField, gbc);

        // Black Player Name
        gbc.gridy++;
        addLabel(contentPanel, "Black Player Name:", gbc);
        gbc.gridy++;
        blackNameField = new JTextField(vsComputer ? "Computer" : "Player 2", 20);
        styleTextField(blackNameField);
        if (vsComputer) {
            blackNameField.setEditable(false);
            blackNameField.setEnabled(false);
        }
        contentPanel.add(blackNameField, gbc);

        // Time Control
        gbc.gridy++;
        addLabel(contentPanel, "Time Control:", gbc);
        gbc.gridy++;
        String[] timeOptions = { "3 min", "5 min", "10 min", "15 min", "30 min", "Custom" };
        timeControlCombo = new JComboBox<>(timeOptions);
        timeControlCombo.setSelectedItem("10 min");
        styleComboBox(timeControlCombo);
        timeControlCombo.addActionListener(e -> toggleCustomTime());
        contentPanel.add(timeControlCombo, gbc);

        // Custom Time Panel
        gbc.gridy++;
        customTimePanel = new JPanel(new GridLayout(1, 4, 5, 5));
        customTimePanel.setBackground(BACKGROUND_COLOR);
        customTimePanel.setVisible(false);

        JLabel timeLabel = new JLabel("Time (min):");
        timeLabel.setForeground(TEXT_COLOR);
        timeLabel.setFont(LABEL_FONT);
        customTimePanel.add(timeLabel);

        customTimeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 180, 1));
        customTimePanel.add(customTimeSpinner);

        JLabel incLabel = new JLabel("Inc (sec):");
        incLabel.setForeground(TEXT_COLOR);
        incLabel.setFont(LABEL_FONT);
        customTimePanel.add(incLabel);

        customIncrementSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 60, 1));
        customTimePanel.add(customIncrementSpinner);

        contentPanel.add(customTimePanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton startButton = createStyledButton("Start Game");
        startButton.addActionListener(e -> startGame());
        buttonPanel.add(startButton);

        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> frame.showMenu());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(INPUT_FONT);
        textField.setBackground(new Color(60, 60, 60));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(INPUT_FONT);
        comboBox.setBackground(new Color(60, 60, 60));
        comboBox.setForeground(Color.WHITE);

        // Use BasicComboBoxUI to allow custom coloring on Windows
        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(new Color(60, 60, 60));
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });

        // Style the list items
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(new Color(80, 80, 80));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(60, 60, 60));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? HOVER_COLOR : BUTTON_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }

    private void toggleCustomTime() {
        String selected = (String) timeControlCombo.getSelectedItem();
        customTimePanel.setVisible("Custom".equals(selected));
        revalidate();
        repaint();
    }

    private void startGame() {
        String whiteName = whiteNameField.getText().trim();
        String blackName = blackNameField.getText().trim();

        if (whiteName.isEmpty())
            whiteName = "Player 1";
        if (blackName.isEmpty())
            blackName = vsComputer ? "Computer" : "Player 2";

        int minutes;
        String selected = (String) timeControlCombo.getSelectedItem();
        if ("Custom".equals(selected)) {
            minutes = (Integer) customTimeSpinner.getValue();
            // Increment is ignored for now as Game doesn't support it yet
        } else {
            minutes = Integer.parseInt(selected.split(" ")[0]);
        }

        frame.startNewGame(vsComputer, difficultyLevel, whiteName, blackName, minutes);
    }
}

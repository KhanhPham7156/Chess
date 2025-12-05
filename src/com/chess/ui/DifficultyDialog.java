package com.chess.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

public class DifficultyDialog extends JDialog {
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color PANEL_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color BUTTON_COLOR = new Color(70, 70, 70);
    private static final Color HOVER_COLOR = new Color(90, 90, 90);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 16);

    private JSlider difficultySlider;
    private JLabel difficultyValueLabel;
    private JLabel descriptionLabel;
    private boolean confirmed = false;

    private static final String[] DIFFICULTY_DESCRIPTIONS = {
            "",
            "Beginner (800-1000 Elo)", // 1
            "Beginner (900-1100 Elo)", // 2
            "Beginner (1000-1200 Elo)", // 3
            "Casual (1200-1400 Elo)", // 4
            "Casual (1400-1500 Elo)", // 5
            "Intermediate (1500-1600 Elo)", // 6
            "Intermediate (1700-1800 Elo)", // 7
            "Advanced (1800-1900 Elo)", // 8
            "Advanced (1900-2000 Elo)", // 9
            "Expert (2000-2100 Elo)", // 10
            "Expert (2100-2200 Elo)", // 11
            "Expert (2200-2300 Elo)", // 12
            "Master (2300-2400 Elo)", // 13
            "Master (2400-2500 Elo)", // 14
            "International Master (2500-2550 Elo)", // 15
            "Grandmaster (2550-2600 Elo)", // 16
            "Grandmaster (2600-2650 Elo)", // 17
            "Super GM (2650-2700 Elo)", // 18
            "Super GM (2700-2800 Elo)", // 19
            "World Champion Level (2800+ Elo)" // 20
    };

    public DifficultyDialog(JFrame parent) {
        super(parent, "Select Difficulty", true);
        setUndecorated(true);
        setSize(500, 350);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Choose AI Difficulty", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Slider panel
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setBackground(PANEL_COLOR);
        sliderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Difficulty value label
        difficultyValueLabel = new JLabel("Level: 10", SwingConstants.CENTER);
        difficultyValueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        difficultyValueLabel.setForeground(TEXT_COLOR);
        difficultyValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description label
        descriptionLabel = new JLabel(DIFFICULTY_DESCRIPTIONS[10], SwingConstants.CENTER);
        descriptionLabel.setFont(LABEL_FONT);
        descriptionLabel.setForeground(new Color(180, 180, 180));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Slider
        difficultySlider = new JSlider(1, 20, 10);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setBackground(PANEL_COLOR);
        difficultySlider.setForeground(TEXT_COLOR);

        // Custom labels for slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 1; i <= 20; i += 1) {
            JLabel label = new JLabel(String.valueOf(i));
            label.setForeground(TEXT_COLOR);
            label.setFont(new Font("SansSerif", Font.PLAIN, 10));
            labelTable.put(i, label);
        }
        difficultySlider.setLabelTable(labelTable);
        difficultySlider.setPaintLabels(true);

        difficultySlider.addChangeListener(e -> {
            int value = difficultySlider.getValue();
            difficultyValueLabel.setText("Level: " + value);
            descriptionLabel.setText(DIFFICULTY_DESCRIPTIONS[value]);
        });

        sliderPanel.add(difficultyValueLabel);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sliderPanel.add(descriptionLabel);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sliderPanel.add(difficultySlider);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton startButton = createStyledButton("Start Game");
        JButton cancelButton = createStyledButton("Cancel");

        startButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        // Add all components
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(sliderPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? HOVER_COLOR : BUTTON_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(150, 40));

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

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getDifficultyLevel() {
        return difficultySlider.getValue();
    }
}

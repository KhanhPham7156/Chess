package com.chess.ui;

import com.chess.core.Game;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameInfoPanel extends JPanel {
    private JLabel whiteNameLabel;
    private JLabel blackNameLabel;
    private JLabel whiteTimeLabel;
    private JLabel blackTimeLabel;
    private JPanel whitePanel;
    private JPanel blackPanel;

    private static final Color ACTIVE_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color INACTIVE_COLOR = new Color(240, 240, 240); // Light Gray

    public GameInfoPanel() {
        setLayout(new GridLayout(2, 1, 10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(200, 0));

        // Black Player Panel (Top)
        blackNameLabel = new JLabel("Player");
        blackTimeLabel = new JLabel("00:00");
        blackPanel = createPlayerPanel(blackNameLabel, blackTimeLabel);
        add(blackPanel);

        // White Player Panel (Bottom)
        whiteNameLabel = new JLabel("Player");
        whiteTimeLabel = new JLabel("00:00");
        whitePanel = createPlayerPanel(whiteNameLabel, whiteTimeLabel);
        add(whitePanel);
    }

    private JPanel createPlayerPanel(JLabel nameLabel, JLabel timeLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(INACTIVE_COLOR);

        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(timeLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    public void update(Game game) {
        whiteNameLabel.setText(game.getWhitePlayerName());
        blackNameLabel.setText(game.getBlackPlayerName());

        if (game.isTimedGame()) {
            whiteTimeLabel.setText(formatTime(game.getWhiteTimeRemaining()));
            blackTimeLabel.setText(formatTime(game.getBlackTimeRemaining()));
        } else {
            whiteTimeLabel.setText("--:--");
            blackTimeLabel.setText("--:--");
        }

        if (game.isGameOver()) {
            whitePanel.setBackground(INACTIVE_COLOR);
            blackPanel.setBackground(INACTIVE_COLOR);
        } else {
            if (game.isWhiteTurn()) {
                whitePanel.setBackground(ACTIVE_COLOR);
                blackPanel.setBackground(INACTIVE_COLOR);
            } else {
                whitePanel.setBackground(INACTIVE_COLOR);
                blackPanel.setBackground(ACTIVE_COLOR);
            }
        }
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

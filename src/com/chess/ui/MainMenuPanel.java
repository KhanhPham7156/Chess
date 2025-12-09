package com.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;
import com.chess.engine.ComputerPlayer; // Import để lấy hằng số

public class MainMenuPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color BUTTON_COLOR = new Color(70, 70, 70);
    private static final Color HOVER_COLOR = new Color(90, 90, 90);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 48);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 20);

    public MainMenuPanel(ChessFrame frame) {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(50, 0, 30, 0));
        JLabel titleLabel = new JLabel("CHESS");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton[] buttons = {
                createStyledButton("Play vs Human"),
                createStyledButton("Vs Java Bot (Custom)"),
                createStyledButton("Vs Stockfish"),
                createStyledButton("Exit")
        };

        for (JButton button : buttons) {
            buttonPanel.add(button);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // 1. Play vs Human
        buttons[0].addActionListener(e -> frame.showGameSetup(false, 0, 0));

        // 2. Play vs Java Bot
        buttons[1].addActionListener(e -> {
            DifficultyDialog dialog = new DifficultyDialog(frame);
            dialog.setTitle("Java Bot Difficulty");
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                // engineType = 1
                frame.showGameSetup(true, dialog.getDifficultyLevel(), ComputerPlayer.TYPE_JAVA_BOT);
            }
        });

        // 3. Play vs Stockfish
        buttons[2].addActionListener(e -> {
            DifficultyDialog dialog = new DifficultyDialog(frame);
            dialog.setTitle("Stockfish Difficulty");
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                // engineType = 2
                frame.showGameSetup(true, dialog.getDifficultyLevel(), ComputerPlayer.TYPE_STOCKFISH);
            }
        });

        // 4. Exit
        buttons[3].addActionListener(e -> System.exit(0));

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
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
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.repaint(); }
            public void mouseExited(MouseEvent e) { button.repaint(); }
        });

        return button;
    }
}
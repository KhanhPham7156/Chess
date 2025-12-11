package com.chess.ui;

import javax.swing.*;
import java.awt.*;
import com.chess.core.Game;

public class ChessFrame extends JFrame {
    private MainMenuPanel menuPanel;
    private BoardPanel boardPanel;
    private JPanel currentPanel;
    private Timer gameTimer;

    public ChessFrame() {
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);
        ImageIcon icon = new ImageIcon("resources/image/other/Logo.png");
        setIconImage(icon.getImage());

        menuPanel = new MainMenuPanel(this);
        showMenu();
    }

    public void showMenu() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        getContentPane().removeAll();
        getContentPane().add(menuPanel);
        validate();
        repaint();
    }

    public void switchPanel(JPanel panel) {
        getContentPane().removeAll();
        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // CẬP NHẬT: Thêm tham số engineType
    public void showGameSetup(boolean vsComputer, int difficultyLevel, int engineType) {
        GameSetupPanel setupPanel = new GameSetupPanel(this, vsComputer, difficultyLevel, engineType);
        switchPanel(setupPanel);
    }

    // CẬP NHẬT: Thêm tham số engineType
    public void startNewGame(boolean vsComputer, int difficultyLevel, int engineType, String whiteName,
            String blackName, int minutes, boolean playerIsWhite) {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }

        JPanel gameContainer = new JPanel(new BorderLayout());

        // Truyền engineType vào BoardPanel
        boardPanel = new BoardPanel(vsComputer, difficultyLevel, engineType, whiteName, blackName, minutes,
                playerIsWhite);
        gameContainer.add(boardPanel, BorderLayout.CENTER);

        GameInfoPanel infoPanel = new GameInfoPanel(playerIsWhite);
        gameContainer.add(infoPanel, BorderLayout.EAST);

        // --- Bottom Control Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        // Button Style
        Color btnColor = new Color(118, 150, 86);
        Color txtColor = Color.WHITE;
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        // Reset Board Button
        JButton btnReset = new JButton("Reset Board");
        btnReset.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnReset.setBackground(btnColor);
        btnReset.setForeground(txtColor);
        btnReset.setFont(btnFont);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> {
            if (boardPanel != null) {
                boardPanel.resetBoard();
            }
        });

        // Main Menu Button
        JButton btnMenu = new JButton("Main Menu");
        btnMenu.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnMenu.setBackground(btnColor);
        btnMenu.setForeground(txtColor);
        btnMenu.setFont(btnFont);
        btnMenu.setFocusPainted(false);
        btnMenu.addActionListener(e -> showMenu());

        bottomPanel.add(btnReset);
        bottomPanel.add(btnMenu);

        gameContainer.add(bottomPanel, BorderLayout.SOUTH);

        gameTimer = new Timer(100, e -> {
            if (boardPanel != null && boardPanel.getGame() != null) {
                Game g = boardPanel.getGame();
                g.updateTime();
                infoPanel.update(g);

                if (g.isGameOver()) {
                    if (g.isTimedGame() && (g.getWhiteTimeRemaining() == 0 || g.getBlackTimeRemaining() == 0)) {
                        ((Timer) e.getSource()).stop();
                        String winner = g.getWhiteTimeRemaining() == 0 ? blackName : whiteName;
                        JOptionPane.showMessageDialog(this, "Time's up! " + winner + " wins!");
                    }
                }
            }
        });
        gameTimer.start();

        switchPanel(gameContainer);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            new ChessFrame().setVisible(true);
        });
    }
}
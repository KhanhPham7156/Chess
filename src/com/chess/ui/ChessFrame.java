package com.chess.ui;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {
    private MainMenuPanel menuPanel;
    private BoardPanel boardPanel;
    private JPanel currentPanel;
    private Timer gameTimer;

    public ChessFrame() {
        // Main frame setup
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Allow resizing to fit side panel
        setMinimumSize(new Dimension(1000, 800)); // Increased width
        setLocationRelativeTo(null);

        // Set logo icon
        ImageIcon icon = new ImageIcon("resources/image/other/Logo.png");
        setIconImage(icon.getImage());

        // Initialize panels
        menuPanel = new MainMenuPanel(this);

        // Show menu panel by default
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

    public void showGameSetup(boolean vsComputer, int difficultyLevel) {
        GameSetupPanel setupPanel = new GameSetupPanel(this, vsComputer, difficultyLevel);
        switchPanel(setupPanel);
    }

    public void startNewGame(boolean vsComputer, int difficultyLevel, String whiteName, String blackName, int minutes) {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }

        JPanel gameContainer = new JPanel(new BorderLayout());

        boardPanel = new BoardPanel(vsComputer, difficultyLevel, whiteName, blackName, minutes);
        gameContainer.add(boardPanel, BorderLayout.CENTER);

        GameInfoPanel infoPanel = new GameInfoPanel();
        gameContainer.add(infoPanel, BorderLayout.EAST);

        // Start Timer
        gameTimer = new Timer(100, e -> {
            if (boardPanel != null && boardPanel.getGame() != null) {
                boardPanel.getGame().updateTime();
                infoPanel.update(boardPanel.getGame());

                if (boardPanel.getGame().isGameOver()) {
                    // Check if it was a timeout
                    if (boardPanel.getGame().isTimedGame() &&
                            (boardPanel.getGame().getWhiteTimeRemaining() == 0
                                    || boardPanel.getGame().getBlackTimeRemaining() == 0)) {
                        ((Timer) e.getSource()).stop();
                        String winner = boardPanel.getGame().getWhiteTimeRemaining() == 0 ? blackName : whiteName;
                        JOptionPane.showMessageDialog(this, "Time's up! " + winner + " wins!");
                    }
                }
            }
        });
        gameTimer.start();

        switchPanel(gameContainer);
    }

    public static void main(String[] args) {
        // Create and display the chess frame
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ChessFrame().setVisible(true);
        });
    }
}

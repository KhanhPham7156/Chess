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
    public void startNewGame(boolean vsComputer, int difficultyLevel, int engineType, String whiteName, String blackName, int minutes) {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }

        JPanel gameContainer = new JPanel(new BorderLayout());

        // Truyền engineType vào BoardPanel
        boardPanel = new BoardPanel(vsComputer, difficultyLevel, engineType, whiteName, blackName, minutes);
        gameContainer.add(boardPanel, BorderLayout.CENTER);

        GameInfoPanel infoPanel = new GameInfoPanel();
        gameContainer.add(infoPanel, BorderLayout.EAST);

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
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
            new ChessFrame().setVisible(true);
        });
    }
}
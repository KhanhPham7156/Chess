package com.chess.ui;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {
    private MainMenuPanel menuPanel;
    private BoardPanel boardPanel;
    private JPanel currentPanel;

    public ChessFrame() {
        // Main frame setup
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(800, 800));
        setLocationRelativeTo(null);

        // Set logo icon
        ImageIcon icon = new ImageIcon("resources/image/other/Logo.png");
        setIconImage(icon.getImage());

        // Initialize panels
        menuPanel = new MainMenuPanel(this);
        boardPanel = new BoardPanel();

        // Show menu panel by default
        showMenu();
    }

    public void showMenu() {
        getContentPane().removeAll();
        getContentPane().add(menuPanel);
        validate();
        repaint();
    }

    public void switchPanel(JPanel panel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Bắt đầu game với chế độ được chọn
    public void startGame(boolean vsStockfish) {
        System.out.println("Starting game: " + (vsStockfish ? "vs Stockfish" : "vs Human"));
        boardPanel.resetBoard();
        switchPanel(boardPanel);
    }

    public void startNewGame(boolean vsComputer) {
        getContentPane().removeAll();
        boardPanel = new BoardPanel();
        getContentPane().add(boardPanel);
        validate();
        repaint();

        if (vsComputer) {
            // TODO: Initialize computer opponent
        }
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

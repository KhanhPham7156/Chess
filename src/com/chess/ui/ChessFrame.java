package com.chess.ui;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {
    private BoardPanel boardPanel;
    private MainMenuPanel menuPanel;
    private JPanel currentPanel;

    public ChessFrame() {
        // Main frame setup
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setSize(800, 800);

        // Set logo icon
        ImageIcon icon = new ImageIcon("resources/image/other/Logo.png");
        setIconImage(icon.getImage());

        // Initialize board panel
        boardPanel = new BoardPanel();
        menuPanel = new MainMenuPanel(this);

        switchPanel(menuPanel);

        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
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


    public static void main(String[] args) {
        // Create and display the chess frame
        SwingUtilities.invokeLater(() -> {
            new ChessFrame();
        });
    }
}

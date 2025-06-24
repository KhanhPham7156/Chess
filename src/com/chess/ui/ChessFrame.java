package com.chess.ui;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {
    private BoardPanel boardPanel;

    public ChessFrame() {
        // Main frame setup
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setSize(800, 800);

        // Set logo icon
        ImageIcon icon = new ImageIcon("resources/image/other/logo.png");
        setIconImage(icon.getImage());

        // Initialize board panel
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        // Initialize control panel with buttons
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton exitButton = new JButton("Exit");
        controlPanel.add(startButton);
        controlPanel.add(exitButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Handle exit button action
        exitButton.addActionListener(e -> System.exit(0));

        // Handle start button action
        startButton.addActionListener(e -> {
            // thêm logic xử lý vào đây
        });

        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    public static void main(String[] args) {
        // Create and display the chess frame
        SwingUtilities.invokeLater(() -> {
            new ChessFrame();
        });
    }
}

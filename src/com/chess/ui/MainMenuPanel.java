package com.chess.ui;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private Image backgroundImage;
    private ChessFrame frame;

    public MainMenuPanel(ChessFrame chessFrame) {
        this.frame = chessFrame;
        setLayout(null); 
        backgroundImage = new ImageIcon("resources/image/other/StartMenu.jpg").getImage();
        
        JButton vsHumanButton = new JButton();
        vsHumanButton.setBounds(300, 250, 200, 100);
        vsHumanButton.setIcon(new ImageIcon("resources/image/other/PlayVsHuman.png"));
        vsHumanButton.setBorderPainted(false);
        vsHumanButton.setContentAreaFilled(false);
        vsHumanButton.setFocusPainted(false);
        vsHumanButton.setOpaque(false);
        vsHumanButton.addActionListener(e -> frame.startGame(false));

        JButton vsStockfishButton = new JButton();
        vsStockfishButton.setBounds(300, 350, 200, 100);
        vsStockfishButton.setIcon(new ImageIcon("resources/image/other/PlayVsStockfish.png"));
        vsStockfishButton.setBorderPainted(false);
        vsStockfishButton.setContentAreaFilled(false);
        vsStockfishButton.setFocusPainted(false);
        vsStockfishButton.setOpaque(false);
        vsStockfishButton.addActionListener(e -> frame.startGame(true));

        JButton exitButton = new JButton();
        exitButton.setBounds(300, 450, 200, 100);
        exitButton.setIcon(new ImageIcon("resources/image/other/Exit.png"));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.setOpaque(false);
        exitButton.addActionListener(e -> System.exit(0));

        add(vsHumanButton);
        add(vsStockfishButton);
        add(exitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

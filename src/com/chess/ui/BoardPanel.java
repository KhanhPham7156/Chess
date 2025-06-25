package com.chess.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private JButton[][] squares = new JButton[8][8];
    private JButton selectedSquare = null;

    public BoardPanel() {
        setLayout(new GridLayout(8, 8));
        initializeBoard();
    }

    public void initializeBoard() {
        String[][] initialBoard = {
                { "bR", "bN", "bB", "bQ", "bK", "bB", "bN", "bR" },
                { "bP", "bP", "bP", "bP", "bP", "bP", "bP", "bP" },
                { "", "", "", "", "", "", "", "" },
                { "", "", "", "", "", "", "", "" },
                { "", "", "", "", "", "", "", "" },
                { "", "", "", "", "", "", "", "" },
                { "wP", "wP", "wP", "wP", "wP", "wP", "wP", "wP" },
                { "wR", "wN", "wB", "wQ", "wK", "wB", "wN", "wR" }
        };

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton(initialBoard[row][col]);
                squares[row][col].setFont(new Font("Arial", Font.PLAIN, 20));
                // Màu ô bàn cờ (trắng/xám xen kẽ)
                if ((row + col) % 2 == 0) {
                    squares[row][col].setBackground(Color.WHITE);
                } else {
                    squares[row][col].setBackground(new Color(0,153,0));
                }
                // Xử lý click
                final int r = row;
                final int c = col;
                //squares[row][col].addActionListener(e -> handleSquareClick(r, c));
                add(squares[row][col]);
            }
        }
    }

    public void resetBoard() {
        removeAll();
        initializeBoard();
        revalidate();
        repaint();
    }
}

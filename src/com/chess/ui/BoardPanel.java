package com.chess.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.chess.core.Piece;
import com.chess.core.King;
import com.chess.core.Bishop;
import com.chess.core.Knight;
import com.chess.core.Rook;
import com.chess.core.Pawn;
import com.chess.core.Queen;

public class BoardPanel extends JPanel {
    private JButton[][] squares = new JButton[8][8];
    private JButton selectedSquare = null;
    private Piece[][] board = new Piece[8][8];

    public BoardPanel() {
        setLayout(new GridLayout(8, 8));
        initializeBoard();
    }

    public void initializeBoard() {
        // Khởi tạo bàn cờ với các quân cờ
        // Hàng 0: Quân đen (Rook, Knight, Bishop, Queen, King, ...)
        board[0][0] = new Rook(false, 0, 0);
        board[0][1] = new Knight(false, 0, 1);
        board[0][2] = new Bishop(false, 0, 2);
        board[0][3] = new Queen(false, 0, 3); 
        board[0][4] = new King(false, 0, 4);
        board[0][5] = new Bishop(false, 0, 5);
        board[0][6] = new Knight(false, 0, 6);
        board[0][7] = new Rook(false, 0, 7);
        // Hàng 1: Tốt đen
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(false, 1, col);
        }
        // Hàng 6: Tốt trắng
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(true, 6, col);
        }
        // Hàng 7: Quân trắng
        board[7][0] = new Rook(true, 7, 0);
        board[7][1] = new Knight(true, 7, 1);
        board[7][2] = new Bishop(true, 7, 2);
        board[7][3] = new Queen(true, 7, 3);
        board[7][4] = new King(true, 7, 4);
        board[7][5] = new Bishop(true, 7, 5);
        board[7][6] = new Knight(true, 7, 6);
        board[7][7] = new Rook(true, 7, 7);
        // Hàng 2-5: Trống
        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setFont(new Font("Arial", Font.PLAIN, 20));
                // Màu ô bàn cờ (trắng/xanh lá xen kẽ)
                if ((row + col) % 2 == 0) {
                    squares[row][col].setBackground(Color.WHITE);
                } else {
                    squares[row][col].setBackground(new Color(0, 153, 0));
                }
                // Xử lý click
                final int r = row;
                final int c = col;
                squares[row][col].addActionListener(e -> handleSquareClick(r, c));

                add(squares[row][col]);
                updateSquare(row, col);
            }
        }
    }
    
    private void updateSquare(int row, int col) {
        Piece piece = board[row][col];
        if (piece != null && piece.getIcon() != null) {
            squares[row][col].setIcon(piece.getIcon());
            squares[row][col].setText(""); // Xóa text nếu có ảnh
        } else {
            squares[row][col].setIcon(null);
            squares[row][col].setText(piece != null ? piece.getSymbol() : "");
        }
    }

    public void handleSquareClick(int row, int col) {

    }
    
    public String getSquarePossition(JButton square) {
        
    }

    public void resetBoard() {
        removeAll();
        initializeBoard();
        revalidate();
        repaint();
    }
}

package com.chess.ui;

import java.awt.*;
import javax.swing.*;
import com.chess.core.Piece;
import com.chess.core.Game;
import com.chess.core.Move;

public class BoardPanel extends JPanel {
    private JButton[][] squares = new JButton[8][8];
    private JButton selectedSquare = null;
    private Game game = new Game();

    public BoardPanel() {
        setLayout(new GridLayout(8, 8));
        initializeBoard();
    }

    public void initializeBoard() {
        game = new Game(); // Create a new game with its initial board setup

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
        Piece piece = game.getBoard().getPiece(row, col);
        if (piece != null && piece.getIcon() != null) {
            squares[row][col].setIcon(piece.getIcon());
            squares[row][col].setText(""); // Xóa text nếu có ảnh
        } else {
            squares[row][col].setIcon(null);
            squares[row][col].setText(piece != null ? piece.getSymbol() : "");
        }
    }

    public void handleSquareClick(int row, int col) {
        Piece clickedPiece = game.getBoard().getPiece(row, col);
        if (selectedSquare == null && clickedPiece != null) {
            // Only allow selecting pieces of current player's color
            if (clickedPiece.isWhite() == game.isWhiteTurn()) {
                selectedSquare = squares[row][col];
                selectedSquare.setBackground(Color.YELLOW);
            }
        } else if (selectedSquare != null) {
            int fromRow = -1, fromCol = -1;

            // Find selected square's position
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (squares[r][c] == selectedSquare) {
                        fromRow = r;
                        fromCol = c;
                        break;
                    }
                }
            }

            Move move = new Move(fromRow, fromCol, row, col);
            if (game.makeMove(move)) {
                // Update the UI
                updateSquare(fromRow, fromCol);
                updateSquare(row, col);

                // Check game status
                if (game.isCheckmate()) {
                    JOptionPane.showMessageDialog(this,
                            (game.isWhiteTurn() ? "Black" : "White") + " wins by checkmate!");
                } else if (game.isStalemate()) {
                    JOptionPane.showMessageDialog(this, "Game drawn by stalemate!");
                } else if (game.isInCheck()) {
                    JOptionPane.showMessageDialog(this, "Check!");
                }
            }

            resetSquareColors();
            selectedSquare = null;
        }
    }

    private void resetSquareColors() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if ((r + c) % 2 == 0) {
                    squares[r][c].setBackground(Color.WHITE);
                } else {
                    squares[r][c].setBackground(new Color(0, 153, 0));
                }
            }
        }
    }

    public String getSquarePosition(JButton square) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (squares[row][col] == square) {
                    return "" + (char) ('a' + col) + (8 - row);
                }
            }
        }
        return "";
    }

    public void resetBoard() {
        removeAll();
        initializeBoard();
        revalidate();
        repaint();
    }
}

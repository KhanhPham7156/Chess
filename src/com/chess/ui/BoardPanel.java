package com.chess.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import com.chess.core.*;
import com.chess.core.Pawn;
import com.chess.core.King;

public class BoardPanel extends JPanel {
    private JButton[][] squares = new JButton[8][8];
    private JButton selectedSquare = null;
    private Game game = new Game();

    // Add color constants at the top of the class
    private static final Color LIGHT_SQUARE = Color.WHITE;
    private static final Color DARK_SQUARE = new Color(0, 153, 0); // Green color

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
                squares[row][col].setOpaque(true);
                squares[row][col].setBorderPainted(false);
                squares[row][col].setContentAreaFilled(true);

                // Fix the alternating pattern
                if ((row + col) % 2 == 1) {
                    squares[row][col].setBackground(DARK_SQUARE); // Green squares
                } else {
                    squares[row][col].setBackground(LIGHT_SQUARE); // White squares
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

                // Show valid moves
                List<Move> validMoves = clickedPiece.getValidMoves(game.getBoard());
                for (Move move : validMoves) {
                    squares[move.getToRow()][move.getToCol()].setBackground(Color.GREEN);
                }
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

            Piece piece = game.getBoard().getPiece(fromRow, fromCol);
            Move move;

            // Handle special moves
            if (piece instanceof Pawn) {
                if ((piece.isWhite() && row == 0) || (!piece.isWhite() && row == 7)) {
                    // Check if the move is valid before showing promotion dialog
                    List<Move> validMoves = piece.getValidMoves(game.getBoard());
                    boolean canPromote = validMoves.stream()
                            .anyMatch(m -> m.getToRow() == row && m.getToCol() == col);

                    if (canPromote) {
                        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
                        int choice = JOptionPane.showOptionDialog(this,
                                "Choose promotion piece:",
                                "Pawn Promotion",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]);

                        char promotionPiece = 'Q'; // Default to Queen
                        if (choice >= 0) {
                            promotionPiece = options[choice].charAt(0);
                        }
                        move = new Move(fromRow, fromCol, row, col, promotionPiece);
                    } else {
                        move = new Move(fromRow, fromCol, row, col);
                    }
                } else if (Math.abs(row - fromRow) == 1 && Math.abs(col - fromCol) == 1 &&
                        game.getBoard().getPiece(row, col) == null) {
                    // Possible en passant capture
                    move = new Move(fromRow, fromCol, row, col, 'E');
                } else {
                    move = new Move(fromRow, fromCol, row, col);
                }
            } else if (piece instanceof King && Math.abs(col - fromCol) == 2) {
                // Castling move
                move = new Move(fromRow, fromCol, row, col, 'C');
            } else {
                // Normal move
                move = new Move(fromRow, fromCol, row, col);
            }

            if (game.makeMove(move)) {
                // Update the UI
                updateSquare(fromRow, fromCol);
                updateSquare(row, col);

                // For en passant, update the captured pawn's square
                if (move.getSpecialMove() == 'E') {
                    updateSquare(fromRow, col); // Update the square where the captured pawn was
                }

                // For castling, update rook position
                if (piece instanceof King && Math.abs(col - fromCol) == 2) {
                    int rookFromCol = col > fromCol ? 7 : 0;
                    int rookToCol = col > fromCol ? col - 1 : col + 1;
                    updateSquare(row, rookFromCol);
                    updateSquare(row, rookToCol);
                }

                // Get and display game status
                String status = game.getGameStatus();
                if (!status.isEmpty()) {
                    JOptionPane.showMessageDialog(this, status);
                    if (status.contains("wins") || status.contains("draw")) {
                        resetBoard();
                    }
                }
            }

            resetSquareColors();
            selectedSquare = null;
        }
    }

    private void resetSquareColors() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 1) {
                    squares[row][col].setBackground(DARK_SQUARE); // Green squares
                } else {
                    squares[row][col].setBackground(LIGHT_SQUARE); // White squares
                }
                squares[row][col].setOpaque(true);
                squares[row][col].setBorderPainted(false);
                squares[row][col].setContentAreaFilled(true);
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

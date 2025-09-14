package com.chess.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import com.chess.core.*;
import com.chess.core.Pawn;
import com.chess.core.King;
import com.chess.engine.ComputerPlayer;

public class BoardPanel extends JPanel {
    private JButton[][] squares = new JButton[8][8];
    private JButton selectedSquare = null;
    private Game game = new Game();
    private ComputerPlayer computer;
    private boolean vsComputer;
    private Timer computerMoveTimer;

    // Add color constants at the top of the class
    private static final Color LIGHT_SQUARE = Color.WHITE;
    private static final Color DARK_SQUARE = new Color(100, 100, 100); // Green color
    private static final int DOT_SIZE = 24;
    private static final Color VALID_MOVE_DOT_COLOR = new Color(0, 0, 0, 200); // Semi-transparent green
    private static final Color LAST_MOVE_FROM = new Color(255, 255, 150); // Light yellow
    private static final Color LAST_MOVE_TO = new Color(255, 255, 0); // Bright yellow

    // Helper class for layered icons
    private static class LayeredIcon implements Icon {
        private final Icon baseIcon;
        private final Icon overlayIcon;

        public LayeredIcon(Icon base, Icon overlay) {
            this.baseIcon = base;
            this.overlayIcon = overlay;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (baseIcon != null) {
                baseIcon.paintIcon(c, g, x, y);
            }
            if (overlayIcon != null) {
                int centerX = x + (getIconWidth() - overlayIcon.getIconWidth()) / 2;
                int centerY = y + (getIconHeight() - overlayIcon.getIconHeight()) / 2;
                overlayIcon.paintIcon(c, g, centerX, centerY);
            }
        }

        @Override
        public int getIconWidth() {
            return baseIcon != null ? baseIcon.getIconWidth() : overlayIcon.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return baseIcon != null ? baseIcon.getIconHeight() : overlayIcon.getIconHeight();
        }
    }

    private Image createDotImage() {
        BufferedImage img = new BufferedImage(DOT_SIZE, DOT_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(VALID_MOVE_DOT_COLOR);
        g2d.fillOval(0, 0, DOT_SIZE - 1, DOT_SIZE - 1);
        g2d.dispose();
        return img;
    }

    public BoardPanel(boolean vsComputer) {
        this.vsComputer = vsComputer;
        if (vsComputer) {
            computer = new ComputerPlayer();
            // Create timer for computer moves with 500ms delay
            computerMoveTimer = new Timer(500, e -> makeComputerMove());
            computerMoveTimer.setRepeats(false);
        }
        setLayout(new GridLayout(8, 8));
        initializeBoard();
    }

    private void makeComputerMove() {
        if (!game.isGameOver() && !game.isWhiteTurn()) {
            Move computerMove = computer.getMove(game);
            if (computerMove != null) {
                game.makeMove(computerMove);
                updateSquare(computerMove.getFromRow(), computerMove.getFromCol());
                updateSquare(computerMove.getToRow(), computerMove.getToCol());

                // For special moves
                if (computerMove.getSpecialMove() == 'E') {
                    // Update en passant capture square
                    updateSquare(computerMove.getFromRow(), computerMove.getToCol());
                } else if (computerMove.getSpecialMove() == 'C') {
                    // Update rook position for castling
                    int rookFromCol = computerMove.getToCol() > computerMove.getFromCol() ? 7 : 0;
                    int rookToCol = computerMove.getToCol() > computerMove.getFromCol() ? computerMove.getToCol() - 1
                            : computerMove.getToCol() + 1;
                    updateSquare(computerMove.getFromRow(), rookFromCol);
                    updateSquare(computerMove.getFromRow(), rookToCol);
                }

                // After computer move, update colors to highlight the move
                resetSquareColors();

                String status = game.getGameStatus();
                if (!status.isEmpty()) {
                    JOptionPane.showMessageDialog(this, status);
                    if (status.contains("wins") || status.contains("draw")) {
                        resetBoard();
                    }
                }
            }
        }
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
        // Update piece icon/text
        Piece piece = game.getBoard().getPiece(row, col);
        if (piece != null && piece.getIcon() != null) {
            squares[row][col].setIcon(piece.getIcon());
            squares[row][col].setText(""); // Clear text if we have an icon
        } else {
            squares[row][col].setIcon(null);
            squares[row][col].setText(piece != null ? piece.getSymbol() : "");
        }

        // Ensure square color is correct
        Move lastMove = game.getBoard().getLastMove();
        Color squareColor;
        if (lastMove != null && (row == lastMove.getFromRow() && col == lastMove.getFromCol())) {
            squareColor = LAST_MOVE_FROM;
        } else if (lastMove != null && (row == lastMove.getToRow() && col == lastMove.getToCol())) {
            squareColor = LAST_MOVE_TO;
        } else {
            squareColor = (row + col) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE;
        }
        squares[row][col].setBackground(squareColor);
        squares[row][col].setOpaque(true);
        squares[row][col].setBorderPainted(false);
        squares[row][col].setContentAreaFilled(true);
    }

    public void handleSquareClick(int row, int col) {
        Piece clickedPiece = game.getBoard().getPiece(row, col);
        if (selectedSquare == null && clickedPiece != null) {
            // Only allow selecting pieces of current player's color
            if (clickedPiece.isWhite() == game.isWhiteTurn()) {
                selectedSquare = squares[row][col];
                selectedSquare.setBackground(Color.YELLOW);

                // Show valid moves with dots
                List<Move> validMoves = clickedPiece.getValidMoves(game.getBoard());
                for (Move move : validMoves) {
                    final int moveRow = move.getToRow();
                    final int moveCol = move.getToCol();

                    // Create dot icon
                    ImageIcon dotIcon = new ImageIcon(createDotImage());

                    // If there's already a piece on the square, create composite icon
                    Icon currentIcon = squares[moveRow][moveCol].getIcon();
                    if (currentIcon != null) {
                        squares[moveRow][moveCol].setIcon(new LayeredIcon(currentIcon, dotIcon));
                    } else {
                        squares[moveRow][moveCol].setIcon(dotIcon);
                    }
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
                        return;
                    }
                }

                // Start computer move timer if playing against computer
                if (vsComputer && !game.isGameOver()) {
                    computerMoveTimer.start();
                }
            }

            resetSquareColors();
            selectedSquare = null;
        }
    }

    private void removeValidMoveDots() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].removeAll();
                squares[row][col].revalidate();
                squares[row][col].repaint();
                updateSquare(row, col); // Restore the piece icon if there was one
            }
        }
    }

    private void resetSquareColors() {
        removeValidMoveDots();
        Move lastMove = game.getBoard().getLastMove();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor;
                if (lastMove != null) {
                    if (row == lastMove.getFromRow() && col == lastMove.getFromCol()) {
                        squareColor = LAST_MOVE_FROM;
                    } else if (row == lastMove.getToRow() && col == lastMove.getToCol()) {
                        squareColor = LAST_MOVE_TO;
                    } else {
                        squareColor = (row + col) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE;
                    }
                } else {
                    squareColor = (row + col) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE;
                }
                squares[row][col].setBackground(squareColor);
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

package com.chess.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import com.chess.core.*;
import com.chess.core.Pawn;
import com.chess.core.King;
import com.chess.engine.ComputerPlayer;

public class BoardPanel extends JPanel {
    // ... (Giữ nguyên các biến JButton[][], colors, icons...)
    private JButton[][] squares = new JButton[8][8];
    private JButton selectedSquare = null;
    private Game game;
    private ComputerPlayer computer;
    private boolean vsComputer;
    private int engineType; // BIẾN MỚI
    private Timer computerMoveTimer;
    private String whiteName;
    private String blackName;
    private int timeControlMinutes;

    // Check blinking variables
    private Timer checkBlinkTimer;
    private int blinkCount;
    private int kingRow, kingCol;
    private boolean isBlinkRed;

    // Drag and Drop variables
    private boolean isDragging = false;
    private int dragStartRow = -1, dragStartCol = -1;
    private JLabel dragLabel;
    private Point dragOffset;

    private static final Color LIGHT_SQUARE = new Color(238, 238, 210);
    private static final Color DARK_SQUARE = new Color(118, 150, 86);
    private static final int DOT_SIZE = 24;
    private static final int DOT_SIZE2 = 34;
    private static final Color VALID_MOVE_DOT_COLOR = new Color(0, 0, 0, 200);
    private static final Color LAST_MOVE_FROM = new Color(255, 255, 150);
    private static final Color LAST_MOVE_TO = new Color(255, 255, 0);

    // ... (Giữ nguyên class LayeredIcon và createDotImage) ...
    // Để tiết kiệm không gian tôi không paste lại helper class này, hãy giữ nguyên
    // code cũ.
    private static class LayeredIcon implements Icon {
        private final Icon baseIcon;
        private final Icon overlayIcon;

        public LayeredIcon(Icon base, Icon overlay) {
            this.baseIcon = base;
            this.overlayIcon = overlay;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (baseIcon != null)
                baseIcon.paintIcon(c, g, x, y);
            if (overlayIcon != null) {
                int centerX = x + (getIconWidth() - overlayIcon.getIconWidth()) / 2;
                int centerY = y + (getIconHeight() - overlayIcon.getIconHeight()) / 2;
                overlayIcon.paintIcon(c, g, centerX, centerY);
            }
        }

        public int getIconWidth() {
            return baseIcon != null ? baseIcon.getIconWidth() : overlayIcon.getIconWidth();
        }

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

    private Image createRingImage() {
        BufferedImage img = new BufferedImage(DOT_SIZE2, DOT_SIZE2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(100, 100, 100, 200)); // Gray ring
        g2d.setStroke(new BasicStroke(4));
        g2d.drawOval(2, 2, DOT_SIZE2 - 5, DOT_SIZE2 - 5);
        g2d.dispose();
        return img;
    }

    // CẬP NHẬT CONSTRUCTOR
    public BoardPanel(boolean vsComputer, int difficultyLevel, int engineType, String whiteName, String blackName,
            int timeControlMinutes) {
        this.vsComputer = vsComputer;
        this.engineType = engineType;
        this.whiteName = whiteName;
        this.blackName = blackName;
        this.timeControlMinutes = timeControlMinutes;

        if (vsComputer) {
            // Truyền engineType vào đây
            computer = new ComputerPlayer(engineType, difficultyLevel);
            computerMoveTimer = new Timer(500, e -> makeComputerMove());
            computerMoveTimer.setRepeats(false);
        }
        setLayout(new GridLayout(8, 8));
        initializeBoard();
    }

    // ... (Toàn bộ phần code còn lại của BoardPanel giữ nguyên không đổi) ...
    // initializeBoard(), updateSquare(), handleSquareClick(), makeComputerMove(),
    // v.v...
    // Chỉ cần đảm bảo hàm makeComputerMove gọi computer.getMove(game) như cũ.

    public Game getGame() {
        return game;
    }

    private void makeComputerMove() {
        if (!game.isGameOver() && !game.isWhiteTurn()) {
            SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
                @Override
                protected Move doInBackground() throws Exception {
                    return computer.getMove(game);
                }

                @Override
                protected void done() {
                    try {
                        Move computerMove = get();
                        if (computerMove != null) {
                            game.makeMove(computerMove);
                            updateSquare(computerMove.getFromRow(), computerMove.getFromCol());
                            updateSquare(computerMove.getToRow(), computerMove.getToCol());

                            if (computerMove.getSpecialMove() == 'E') {
                                updateSquare(computerMove.getFromRow(), computerMove.getToCol());
                            } else if (computerMove.getSpecialMove() == 'C') {
                                int rookFromCol = computerMove.getToCol() > computerMove.getFromCol() ? 7 : 0;
                                int rookToCol = computerMove.getToCol() > computerMove.getFromCol()
                                        ? computerMove.getToCol() - 1
                                        : computerMove.getToCol() + 1;
                                updateSquare(computerMove.getFromRow(), rookFromCol);
                                updateSquare(computerMove.getFromRow(), rookToCol);
                            }
                            resetSquareColors();
                            handleGameStatus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    public void initializeBoard() {
        game = new Game();
        game.setPlayerNames(whiteName, blackName);
        game.setTimeControl(timeControlMinutes);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setFont(new Font("Arial", Font.PLAIN, 20));
                squares[row][col].setOpaque(true);
                squares[row][col].setBorderPainted(false);
                squares[row][col].setContentAreaFilled(true);

                if ((row + col) % 2 == 1)
                    squares[row][col].setBackground(DARK_SQUARE);
                else
                    squares[row][col].setBackground(LIGHT_SQUARE);

                final int r = row;
                final int c = col;

                MouseAdapter mouseHandler = new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (vsComputer && !game.isWhiteTurn())
                            return;

                        dragStartRow = r;
                        dragStartCol = c;
                        dragOffset = e.getPoint();

                        // If clicking on a different square, select it immediately
                        // if (squares[r][c] != selectedSquare) {
                        // handleSquareClick(r, c);
                        // }
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (vsComputer && !game.isWhiteTurn())
                            return;

                        // Only start dragging if we have a piece and it's our turn
                        Piece piece = game.getBoard().getPiece(dragStartRow, dragStartCol);
                        if (piece == null || piece.isWhite() != game.isWhiteTurn())
                            return;

                        // If not yet selected, select it now
                        if (selectedSquare != squares[dragStartRow][dragStartCol]) {
                            handleSquareClick(dragStartRow, dragStartCol);
                        }

                        if (selectedSquare != squares[dragStartRow][dragStartCol])
                            return;

                        if (!isDragging) {
                            isDragging = true;
                            // Piece piece = game.getBoard().getPiece(dragStartRow, dragStartCol); //
                            // Removed duplicate declaration
                            if (piece != null && piece.getIcon() != null) {
                                dragLabel = new JLabel(piece.getIcon());
                                dragLabel.setSize(squares[dragStartRow][dragStartCol].getSize());

                                JLayeredPane layeredPane = getRootPane().getLayeredPane();
                                layeredPane.add(dragLabel, JLayeredPane.DRAG_LAYER);

                                // Hide piece on board
                                squares[dragStartRow][dragStartCol].setIcon(null);
                            }
                        }

                        if (isDragging && dragLabel != null) {
                            Point p = SwingUtilities.convertPoint(squares[r][c], e.getPoint(),
                                    getRootPane().getLayeredPane());
                            dragLabel.setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (vsComputer && !game.isWhiteTurn())
                            return;

                        if (isDragging) {
                            isDragging = false;
                            if (dragLabel != null) {
                                getRootPane().getLayeredPane().remove(dragLabel);
                                getRootPane().getLayeredPane().repaint();
                                dragLabel = null;
                            }

                            // Restore icon
                            updateSquare(dragStartRow, dragStartCol);

                            // Calculate target
                            Point p = SwingUtilities.convertPoint(squares[r][c], e.getPoint(), BoardPanel.this);
                            Component comp = BoardPanel.this.getComponentAt(p);

                            if (comp instanceof JButton) {
                                for (int targetRow = 0; targetRow < 8; targetRow++) {
                                    for (int targetCol = 0; targetCol < 8; targetCol++) {
                                        if (squares[targetRow][targetCol] == comp) {
                                            if (targetRow != dragStartRow || targetCol != dragStartCol) {
                                                attemptMove(targetRow, targetCol);
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        } else {
                            // Click logic: Handle normal click movement
                            // If we released on the same square we started, do nothing (wait for next
                            // click)
                            // If we released on a different square, try to move there

                            // Calculate target just like in drag
                            Point p = SwingUtilities.convertPoint(squares[r][c], e.getPoint(), BoardPanel.this);
                            Component comp = BoardPanel.this.getComponentAt(p);

                            if (comp instanceof JButton) {
                                for (int targetRow = 0; targetRow < 8; targetRow++) {
                                    for (int targetCol = 0; targetCol < 8; targetCol++) {
                                        if (squares[targetRow][targetCol] == comp) {
                                            // If clicked on the same square, just handle selection toggle
                                            if (targetRow == dragStartRow && targetCol == dragStartCol) {
                                                handleSquareClick(targetRow, targetCol);
                                            } else {
                                                // If clicked on a different square, try to move or select
                                                if (selectedSquare != null) {
                                                    // If we have a selection, try to move to the clicked square
                                                    // But first check if we clicked on our own piece (change selection)
                                                    Piece targetPiece = game.getBoard().getPiece(targetRow, targetCol);
                                                    if (targetPiece != null
                                                            && targetPiece.isWhite() == game.isWhiteTurn()) {
                                                        handleSquareClick(targetRow, targetCol);
                                                    } else {
                                                        attemptMove(targetRow, targetCol);
                                                    }
                                                } else {
                                                    handleSquareClick(targetRow, targetCol);
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                };

                squares[row][col].addMouseListener(mouseHandler);
                squares[row][col].addMouseMotionListener(mouseHandler);

                add(squares[row][col]);
                updateSquare(row, col);
            }
        }
    }

    // ... (Giữ nguyên các hàm updateSquare, handleSquareClick, selectSquare,
    // deselect, attemptMove, v.v...) ...
    // Copy lại từ code cũ của bạn vì logic không thay đổi.

    private void updateSquare(int row, int col) {
        Piece piece = game.getBoard().getPiece(row, col);
        if (piece != null && piece.getIcon() != null) {
            squares[row][col].setIcon(piece.getIcon());
            squares[row][col].setText("");
        } else {
            squares[row][col].setIcon(null);
            squares[row][col].setText(piece != null ? piece.getSymbol() : "");
        }
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
    }

    // ... (Các hàm còn lại giữ nguyên) ...
    public void handleSquareClick(int row, int col) {
        stopCheckBlink();
        if (vsComputer && !game.isWhiteTurn())
            return;
        Piece clickedPiece = game.getBoard().getPiece(row, col);
        boolean isMyPiece = clickedPiece != null && clickedPiece.isWhite() == game.isWhiteTurn();

        if (selectedSquare == null) {
            if (isMyPiece)
                selectSquare(row, col);
        } else {
            if (squares[row][col] == selectedSquare)
                deselect();
            else if (isMyPiece) {
                deselect();
                selectSquare(row, col);
            } else
                attemptMove(row, col);
        }
    }

    private void selectSquare(int row, int col) {
        selectedSquare = squares[row][col];
        selectedSquare.setBackground(Color.YELLOW);
        Piece piece = game.getBoard().getPiece(row, col);
        if (piece != null) {
            List<Move> validMoves = piece.getValidMoves(game.getBoard());
            for (Move move : validMoves) {
                int moveRow = move.getToRow();
                int moveCol = move.getToCol();

                boolean isCapture = game.getBoard().getPiece(moveRow, moveCol) != null || move.getSpecialMove() == 'E';
                ImageIcon overlayIcon = new ImageIcon(isCapture ? createRingImage() : createDotImage());

                Icon currentIcon = squares[moveRow][moveCol].getIcon();
                if (currentIcon != null)
                    squares[moveRow][moveCol].setIcon(new LayeredIcon(currentIcon, overlayIcon));
                else
                    squares[moveRow][moveCol].setIcon(overlayIcon);
            }
        }
    }

    private void deselect() {
        resetSquareColors();
        selectedSquare = null;
    }

    private void attemptMove(int row, int col) {
        int fromRow = -1, fromCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (squares[r][c] == selectedSquare) {
                    fromRow = r;
                    fromCol = c;
                    break;
                }
            }
        }
        if (fromRow == -1) {
            deselect();
            return;
        }

        Piece piece = game.getBoard().getPiece(fromRow, fromCol);
        Move move;

        if (piece instanceof Pawn) {
            if ((piece.isWhite() && row == 0) || (!piece.isWhite() && row == 7)) {
                List<Move> validMoves = piece.getValidMoves(game.getBoard());
                boolean canPromote = validMoves.stream().anyMatch(m -> m.getToRow() == row && m.getToCol() == col);
                if (canPromote) {
                    String[] options = { "Queen", "Rook", "Bishop", "Knight" };
                    int choice = JOptionPane.showOptionDialog(this, "Choose promotion piece:", "Pawn Promotion",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    char promotionPiece = 'Q';
                    if (choice >= 0) {
                        String selected = options[choice];
                        if (selected.equals("Knight"))
                            promotionPiece = 'N';
                        else
                            promotionPiece = selected.charAt(0);
                    }
                    move = new Move(fromRow, fromCol, row, col, promotionPiece);
                } else
                    move = new Move(fromRow, fromCol, row, col);
            } else if (Math.abs(row - fromRow) == 1 && Math.abs(col - fromCol) == 1
                    && game.getBoard().getPiece(row, col) == null) {
                move = new Move(fromRow, fromCol, row, col, 'E');
            } else
                move = new Move(fromRow, fromCol, row, col);
        } else if (piece instanceof King && Math.abs(col - fromCol) == 2) {
            move = new Move(fromRow, fromCol, row, col, 'C');
        } else
            move = new Move(fromRow, fromCol, row, col);

        if (game.makeMove(move)) {
            updateSquare(fromRow, fromCol);
            updateSquare(row, col);
            if (move.getSpecialMove() == 'E')
                updateSquare(fromRow, col);
            if (piece instanceof King && Math.abs(col - fromCol) == 2) {
                int rookFromCol = col > fromCol ? 7 : 0;
                int rookToCol = col > fromCol ? col - 1 : col + 1;
                updateSquare(row, rookFromCol);
                updateSquare(row, rookToCol);
            }
            resetSquareColors();
            handleGameStatus();
            if (vsComputer && !game.isGameOver())
                computerMoveTimer.start();
        } else
            deselect();
        selectedSquare = null;
    }

    private void resetSquareColors() {
        removeValidMoveDots();
        Move lastMove = game.getBoard().getLastMove();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor;
                if (lastMove != null) {
                    if (row == lastMove.getFromRow() && col == lastMove.getFromCol())
                        squareColor = LAST_MOVE_FROM;
                    else if (row == lastMove.getToRow() && col == lastMove.getToCol())
                        squareColor = LAST_MOVE_TO;
                    else
                        squareColor = (row + col) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE;
                } else
                    squareColor = (row + col) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE;
                squares[row][col].setBackground(squareColor);
            }
        }
    }

    private void removeValidMoveDots() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].removeAll();
                squares[row][col].revalidate();
                squares[row][col].repaint();
                updateSquare(row, col);
            }
        }
    }

    private void handleGameStatus() {
        String status = game.getGameStatus();
        if (status.isEmpty())
            return;
        if (status.contains("wins") || status.contains("draw")) {
            JOptionPane.showMessageDialog(this, status);
            resetBoard();
        } else if (status.contains("check")) {
            highlightKingInCheck();
        }
    }

    public void resetBoard() {
        stopCheckBlink();
        selectedSquare = null;
        removeAll();
        initializeBoard();
        revalidate();
        repaint();
    }

    private void stopCheckBlink() {
        if (checkBlinkTimer != null && checkBlinkTimer.isRunning())
            checkBlinkTimer.stop();
        if (kingRow >= 0 && kingRow < 8 && kingCol >= 0 && kingCol < 8) {
            Color squareColor = (kingRow + kingCol) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE;
            squares[kingRow][kingCol].setBackground(squareColor);
        }
    }

    private void highlightKingInCheck() {
        boolean whiteTurn = game.isWhiteTurn();
        Piece king = null;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = game.getBoard().getPiece(r, c);
                if (p instanceof King && p.isWhite() == whiteTurn) {
                    king = p;
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
            if (king != null)
                break;
        }
        if (king != null) {
            if (checkBlinkTimer != null && checkBlinkTimer.isRunning())
                checkBlinkTimer.stop();
            blinkCount = 0;
            isBlinkRed = true;
            checkBlinkTimer = new Timer(500, e -> {
                if (blinkCount >= 6) {
                    ((Timer) e.getSource()).stop();
                    return;
                }
                squares[kingRow][kingCol].setBackground(
                        isBlinkRed ? Color.RED : ((kingRow + kingCol) % 2 == 1 ? DARK_SQUARE : LIGHT_SQUARE));
                isBlinkRed = !isBlinkRed;
                blinkCount++;
            });
            checkBlinkTimer.start();
        }
    }
}
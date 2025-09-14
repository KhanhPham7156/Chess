package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class King extends Piece {
    private boolean hasMoved;

    public King(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
        loadIcon();
        this.hasMoved = false;
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    @Override
    protected void loadIcon() {
        String path = isWhite ? "resources/image/pieces/white_king.png" : "resources/image/pieces/black_king.png";
        icon = new ImageIcon(path);
    }

    public List<Move> getBasicMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board.getPiece(newRow, newCol);
                if (target == null || target.isWhite() != isWhite) {
                    // Test move for check
                    Piece originalPiece = board.getPiece(newRow, newCol);
                    int oldRow = row;
                    int oldCol = col;

                    // Actually move the king to test the new position
                    board.setPiece(newRow, newCol, this);
                    board.setPiece(oldRow, oldCol, null);
                    setPosition(newRow, newCol); // Update king's internal position

                    boolean inCheck = board.isSquareAttacked(newRow, newCol, isWhite);

                    // Restore everything
                    board.setPiece(oldRow, oldCol, this);
                    board.setPiece(newRow, newCol, originalPiece);
                    setPosition(oldRow, oldCol); // Restore king's position

                    if (!inCheck) {
                        validMoves.add(new Move(row, col, newRow, newCol));
                    }
                }
            }
        }
        return validMoves;
    }

    @Override
    protected List<Move> calculateValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>(getBasicMoves(board));

        // Check for castling
        if (!hasMoved && !board.isInCheck(isWhite)) {
            // Check kingside castling
            if (canCastle(board, true)) {
                validMoves.add(new Move(row, col, row, col + 2, 'C'));
            }
            // Check queenside castling
            if (canCastle(board, false)) {
                validMoves.add(new Move(row, col, row, col - 2, 'C'));
            }
        }

        return validMoves;
    }

    private boolean canCastle(Board board, boolean kingSide) {
        int rookCol = kingSide ? 7 : 0;
        int direction = kingSide ? 1 : -1;

        // Check if rook is in position and hasn't moved
        Piece rook = board.getPiece(row, rookCol);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // Check if squares between king and rook are empty
        for (int c = col + direction; kingSide ? (c < rookCol) : (c > rookCol); c += direction) {
            if (board.getPiece(row, c) != null) {
                return false;
            }
        }

        // Check if squares the king moves through are NOT under attack
        // Only need to check current square, one step and two steps
        for (int i = 0; i <= 2; i++) {
            int checkCol = kingSide ? col + i : col - i;
            if (isSquareUnderAttack(board, row, checkCol)) {
                return false;
            }
        }

        return true;
    }

    private boolean isSquareUnderAttack(Board board, int squareRow, int squareCol) {
        return ((Board) board).isSquareAttacked(squareRow, squareCol, isWhite);
    }

    @Override
    protected List<Move> calculatePinnedMoves(Board board) {
        // Kings cannot be pinned, so return normal moves
        return calculateValidMoves(board);
    }

    @Override
    public String getSymbol() {
        return isWhite ? "wK" : "bK";
    }
}

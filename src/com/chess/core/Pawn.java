package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece {
    private boolean hasMoved;
    private boolean canBeEnPassanted;

    public Pawn(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
        loadIcon();
        this.hasMoved = false;
        this.canBeEnPassanted = false;
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    public void setEnPassant(boolean canBeEnPassanted) {
        this.canBeEnPassanted = canBeEnPassanted;
    }

    public boolean canBeEnPassanted() {
        return canBeEnPassanted;
    }

    @Override
    protected void loadIcon() {
        String path = isWhite ? "resources/image/pieces/white_pawn.png" : "resources/image/pieces/black_pawn.png";
        icon = new ImageIcon(path);
    }

    @Override
    public List<Move> getValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        int direction = isWhite ? -1 : 1; // White moves up, Black moves down
        int startRow = isWhite ? 6 : 1; // Starting row for pawns

        // Check forward moves
        int newRow = row + direction;
        if (newRow >= 0 && newRow < 8 && board.getPiece(newRow, col) == null) {
            // Check for promotion
            if (newRow == 0 || newRow == 7) {
                // Add promotion moves for Queen, Rook, Bishop, and Knight
                validMoves.add(new Move(row, col, newRow, col, 'Q')); // Queen promotion
                validMoves.add(new Move(row, col, newRow, col, 'R')); // Rook promotion
                validMoves.add(new Move(row, col, newRow, col, 'B')); // Bishop promotion
                validMoves.add(new Move(row, col, newRow, col, 'N')); // Knight promotion
            } else {
                validMoves.add(new Move(row, col, newRow, col)); // Regular move
            }

            // Two square advance from starting position
            if (!hasMoved && board.getPiece(newRow + direction, col) == null) {
                validMoves.add(new Move(row, col, newRow + direction, col));
            }
        }

        // Check diagonal captures including promotion
        int[] captureCols = { col - 1, col + 1 };
        for (int c : captureCols) {
            if (c >= 0 && c < 8) {
                Piece target = board.getPiece(newRow, c);
                if (target != null && target.isWhite() != isWhite) {
                    if (newRow == 0 || newRow == 7) {
                        // Add promotion captures
                        validMoves.add(new Move(row, col, newRow, c, 'Q'));
                        validMoves.add(new Move(row, col, newRow, c, 'R'));
                        validMoves.add(new Move(row, col, newRow, c, 'B'));
                        validMoves.add(new Move(row, col, newRow, c, 'N'));
                    } else {
                        validMoves.add(new Move(row, col, newRow, c));
                    }
                }
            }
        }

        // Check en passant
        if ((isWhite && row == 3) || (!isWhite && row == 4)) { // Correct row for en passant
            for (int c : captureCols) {
                if (c >= 0 && c < 8) {
                    Piece target = board.getPiece(row, c);
                    if (target instanceof Pawn && target.isWhite() != isWhite && ((Pawn) target).canBeEnPassanted()) {
                        validMoves.add(new Move(row, col, newRow, c, 'E')); // 'E' indicates en passant
                    }
                }
            }
        }

        return validMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "wP" : "bP";
    }
}

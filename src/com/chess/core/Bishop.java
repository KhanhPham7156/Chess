package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
        loadIcon();
    }

    @Override
    protected void loadIcon() {
        String path = isWhite ? "resources/image/pieces/white_bishop.png" : "resources/image/pieces/black_bishop.png";
        icon = new ImageIcon(path);
    }

    @Override
    protected List<Move> calculateValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        int[][] directions = {
                { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 }
        };

        for (int[] dir : directions) {
            int newRow = row;
            int newCol = col;
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                    break; // Out of bounds
                }
                Piece target = board.getPiece(newRow, newCol);
                if (target == null) {
                    validMoves.add(new Move(row, col, newRow, newCol));
                } else {
                    if (target.isWhite() != isWhite) {
                        validMoves.add(new Move(row, col, newRow, newCol)); // Capture move
                    }
                    break;
                }
            }
        }
        return validMoves;
    }

    @Override
    protected List<Move> calculatePinnedMoves(Board board) {
        // Find the king
        King king = null;
        int kingRow = -1, kingCol = -1;

        outer: for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p instanceof King && p.isWhite() == this.isWhite()) {
                    king = (King) p;
                    kingRow = r;
                    kingCol = c;
                    break outer;
                }
            }
        }

        if (king == null)
            return null;

        // Only allow moves along the line of pin
        List<Move> pinnedMoves = new ArrayList<>();
        boolean isDiagonal = Math.abs(kingRow - row) == Math.abs(kingCol - col);

        if (isDiagonal) {
            int[] direction = {
                    (kingRow - row) / Math.abs(kingRow - row),
                    (kingCol - col) / Math.abs(kingCol - col)
            };

            // Check moves along the pin line
            int newRow = row;
            int newCol = col;
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8)
                    break;

                Piece target = board.getPiece(newRow, newCol);
                if (target == null) {
                    pinnedMoves.add(new Move(row, col, newRow, newCol));
                } else {
                    if (target.isWhite() != isWhite) {
                        pinnedMoves.add(new Move(row, col, newRow, newCol));
                    }
                    break;
                }
            }

            // Check moves in opposite direction
            newRow = row - direction[0];
            newCol = col - direction[1];
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board.getPiece(newRow, newCol);
                if (target == null) {
                    pinnedMoves.add(new Move(row, col, newRow, newCol));
                } else {
                    if (target.isWhite() != isWhite && !(target instanceof King)) {
                        pinnedMoves.add(new Move(row, col, newRow, newCol));
                    }
                    break;
                }
                newRow -= direction[0];
                newCol -= direction[1];
            }
        }

        return pinnedMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "wB" : "bB";
    }
}

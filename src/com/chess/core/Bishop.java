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

        List<Move> pinnedMoves = new ArrayList<>();

        int dx = row - kingRow;
        int dy = col - kingCol;

        // If pinned straight (Rook/Queen pin), Bishop cannot move
        if (Math.abs(dx) != Math.abs(dy)) {
            return pinnedMoves;
        }

        // Determine direction away from king
        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);

        // 1. Scan away from King (towards pinner)
        int currRow = row + stepX;
        int currCol = col + stepY;
        while (currRow >= 0 && currRow < 8 && currCol >= 0 && currCol < 8) {
            Piece target = board.getPiece(currRow, currCol);
            if (target == null) {
                pinnedMoves.add(new Move(row, col, currRow, currCol));
            } else {
                if (target.isWhite() != isWhite) {
                    pinnedMoves.add(new Move(row, col, currRow, currCol));
                }
                break;
            }
            currRow += stepX;
            currCol += stepY;
        }

        // 2. Scan towards King
        currRow = row - stepX;
        currCol = col - stepY;
        while (currRow >= 0 && currRow < 8 && currCol >= 0 && currCol < 8) {
            Piece target = board.getPiece(currRow, currCol);
            if (target == null) {
                pinnedMoves.add(new Move(row, col, currRow, currCol));
            } else {
                // Should be the King, stop.
                break;
            }
            currRow -= stepX;
            currCol -= stepY;
        }

        return pinnedMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "wB" : "bB";
    }
}

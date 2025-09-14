package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
        loadIcon();
    }

    @Override
    protected void loadIcon() {
        String path = isWhite ? "resources/image/pieces/white_knight.png" : "resources/image/pieces/black_knight.png";
        icon = new ImageIcon(path);
    }

    @Override
    protected List<Move> calculateValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        int directions[][] = {
                { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 },
                { 1, -2 }, { 1, 2 }, { 2, -1 }, { 2, 1 }
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board.getPiece(newRow, newCol);
                if (target == null || target.isWhite() != isWhite) {
                    validMoves.add(new Move(row, col, newRow, newCol));
                }
            }
        }
        return validMoves;
    }

    @Override
    public String getSymbol() {
        return isWhite ? "wN" : "bN";
    }
}

package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(boolean isWhite, int row, int col){
        super(isWhite, row, col);
        loadIcon();
    }

    @Override
    protected void loadIcon() {
        String path = isWhite ? "resources/image/pieces/white_bishop.png" : "resources/image/pieces/black_bishop.png";
        icon = new ImageIcon(path);
    }

    @Override
    public List<Move> getValidMoves(Board board) {
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
    public String getSymbol() {
        return isWhite ? "wB" : "bB";
    }
}

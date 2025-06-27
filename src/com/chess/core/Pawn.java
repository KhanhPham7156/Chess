package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Pawn extends Piece{
    public Paww(boolean isWhite,int row, int col){
        super(isWhite, row, col);
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
            validMoves.add(new Move(row, col, newRow, col)); // Move forward 1 square
            if (row == startRow && board.getPiece(newRow + direction, col) == null) {
                validMoves.add(new Move(row, col, newRow + direction, col)); // Move forward 2 square if on starting row
            }
        }

        // Check captures
        int[] captureCols = { col - 1, col + 1 };
        for (int c : captureCols) {
            if (c >= 0 && c < 8) {
                Piece target = board.getPiece(newRow, c);
                if (target != null && target.isWhite() != isWhite) {
                    validMoves.add(new Move(row, col, newRow, c));
                }
            }
        }

        // Check en passant (bắt tốt qua đường)

        // Check promotion 

        return validMoves;
    }
    
    @Override
    public String getSymbol() {
        return isWhite ? "wP" : "bP";
    }
}

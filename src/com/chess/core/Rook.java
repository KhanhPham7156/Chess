package com.chess.core;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Rook extends Piece{
    public Rook(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }

    @Override
    protected void loadIcon() {
        String path = isWhite ? "resources/image/pieces/white_rook.png" : "resources/image/pieces/black_rook.png";
        icon = new ImageIcon(path);
    }

    @Override
    public List<Move> getValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        int[][] directions = {
            { -1, 0 },
            { 1, 0 },  
            { 0, -1 }, 
            { 0, 1 }   
        };

        for (int[] dir : directions) {
            int newRow = row;
            int newCol = col;
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                    break;
                }
                Piece target = board.getPiece(newRowm newCol);
                if(target == null){
                    validMoves.add(new Move(row, col, newRow, newCol));
                } else{
                    if(target.isWhite()!=isWhite){
                        validMoves.add(new Move(row, col, newRow, newCol)); // Capture move
                    }
                    break;
                }
            }
            return validMoves;
        }
    }

    @Override
    public String getSymbol() {
        return isWhite ? "wR" : "bR";
    }
}

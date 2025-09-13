package com.chess.core;

import java.util.Objects;

public class Move {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private char specialMove; // 'P' for promotion (piece type), 'E' for en passant, 'C' for castling

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(fromRow, fromCol, toRow, toCol, '\0');
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, char specialMove) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.specialMove = specialMove;
    }

    public char getSpecialMove() {
        return specialMove;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToCol() {
        return toCol;
    }

    public int getToRow() {
        return toRow;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Move))
            return false;
        Move other = (Move) obj;
        return fromRow == other.fromRow && fromCol == other.fromCol &&
                toRow == other.toRow && toCol == other.toCol &&
                specialMove == other.specialMove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol);
    }
}

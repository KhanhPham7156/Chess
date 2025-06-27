package com.chess.core;

import java.util.List;
import javax.swing.*;

public abstract class Piece {
    protected boolean isWhite;
    protected int row;
    protected int col;
    protected ImageIcon icon;

    public Piece(boolean isWhite, int row, int col) {
        this.isWhite = isWhite;
        this.row = row;
        this.col = col;
    }

    public abstract List<Move> getValidMoves(Board board);

    protected abstract void loadIcon();

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public ImageIcon getIcon() {
        return icon;
    }
    
    public boolean isWhite() {
        return isWhite;
    }

    public void setPossition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract String getSymbol();
}

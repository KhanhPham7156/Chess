package com.chess.core;

import java.util.List;
import java.util.ArrayList;
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

    public List<Move> getValidMoves(Board board) {
        List<Move> moves = calculateValidMoves(board);
        List<Move> legalMoves = new ArrayList<>();

        // If piece is pinned, only allow moves that stay in line with the king
        if (board.isPinned(this)) {
            moves.clear();
            // Recalculate moves based on pin constraints
            // This will be overridden by each piece type
            List<Move> pinnedMoves = calculatePinnedMoves(board);
            if (pinnedMoves != null) {
                moves.addAll(pinnedMoves);
            }
        }

        // Filter moves based on check
        if (board.isInCheck(isWhite)) {
            // Try each move to see if it gets us out of check
            for (Move move : moves) {
                // Save current board state
                Piece capturedPiece = board.getPiece(move.getToRow(), move.getToCol());
                int oldRow = move.getFromRow();
                int oldCol = move.getFromCol();
                int newRow = move.getToRow();
                int newCol = move.getToCol();

                // Make the move
                board.setPiece(newRow, newCol, this);
                board.setPiece(oldRow, oldCol, null);
                setPosition(newRow, newCol);

                // Check if we're still in check
                if (!board.isInCheck(isWhite)) {
                    legalMoves.add(move);
                }

                // Restore the position
                board.setPiece(oldRow, oldCol, this);
                board.setPiece(newRow, newCol, capturedPiece);
                setPosition(oldRow, oldCol);
            }
            return legalMoves;
        }

        return moves;
    }

    protected abstract List<Move> calculateValidMoves(Board board);

    protected List<Move> calculatePinnedMoves(Board board) {
        // Default implementation returns no moves for pinned pieces
        // Subclasses can override to provide specific pin behavior
        return null;
    }

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

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract String getSymbol();
}

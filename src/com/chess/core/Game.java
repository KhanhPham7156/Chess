package com.chess.core;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private boolean isWhiteTurn;
    private boolean gameOver;
    private List<Move> moveHistory;

    public Game() {
        this.board = new Board();
        this.isWhiteTurn = true; // White moves first
        this.gameOver = false;
        this.moveHistory = new ArrayList<>();
    }

    public boolean makeMove(Move move) {
        Piece piece = board.getPiece(move.getFromRow(), move.getFromCol());

        // Basic validation checks
        if (piece == null)
            return false;
        if (piece.isWhite() != isWhiteTurn)
            return false;

        // Get valid moves for the selected piece
        List<Move> validMoves = piece.getValidMoves(board);

        // Check if the move is valid
        if (!validMoves.contains(move))
            return false;

        // Make the move
        Piece capturedPiece = board.getPiece(move.getToRow(), move.getToCol());
        board.movePiece(move);
        moveHistory.add(move);

        // Check if the move puts the current player in check
        if (board.isInCheck(isWhiteTurn)) {
            // Undo the move by moving the piece back and restoring the captured piece
            Move undoMove = new Move(move.getToRow(), move.getToCol(),
                    move.getFromRow(), move.getFromCol());
            board.movePiece(undoMove);
            if (capturedPiece != null) {
                board.setPiece(move.getToRow(), move.getToCol(), capturedPiece);
            }
            moveHistory.remove(moveHistory.size() - 1);
            return false;
        }

        // Move was successful, switch turns
        isWhiteTurn = !isWhiteTurn;

        // Check for checkmate or stalemate
        updateGameStatus();

        return true;
    }

    private void updateGameStatus() {
        // Check if there are any legal moves available
        boolean hasLegalMoves = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.isWhite() == isWhiteTurn) {
                    List<Move> moves = piece.getValidMoves(board);
                    if (!moves.isEmpty()) {
                        hasLegalMoves = true;
                        break;
                    }
                }
            }
            if (hasLegalMoves)
                break;
        }

        if (!hasLegalMoves) {
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public Board getBoard() {
        return board;
    }

    public boolean isInCheck() {
        return board.isInCheck(isWhiteTurn);
    }

    public boolean isCheckmate() {
        return gameOver && isInCheck();
    }

    public boolean isStalemate() {
        return gameOver && !isInCheck();
    }
}

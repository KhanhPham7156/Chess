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
        this.board.setGame(this);
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

        // Test if the move would put or leave own king in check
        Piece capturedPiece = board.getPiece(move.getToRow(), move.getToCol());
        board.setPiece(move.getToRow(), move.getToCol(), piece);
        board.setPiece(move.getFromRow(), move.getFromCol(), null);

        boolean inCheck = board.isInCheck(isWhiteTurn);

        // Undo the test move
        board.setPiece(move.getFromRow(), move.getFromCol(), piece);
        board.setPiece(move.getToRow(), move.getToCol(), capturedPiece);

        if (inCheck)
            return false; // Can't make moves that put/leave own king in check

        // Check for king capture
        Piece targetPiece = board.getPiece(move.getToRow(), move.getToCol());
        if (targetPiece instanceof King) {
            gameOver = true;
            return false; // Can't capture the king - should be checkmate before this
        }

        // Always start by setting the last move (this is the valid move we're
        // executing)
        board.setLastMove(move);

        // Handle special moves
        char specialMove = move.getSpecialMove();
        if (specialMove != '\0') {
            if (specialMove == 'E') { // En passant
                capturedPiece = board.getPiece(move.getFromRow(), move.getToCol());
                if (!(capturedPiece instanceof Pawn && ((Pawn) capturedPiece).canBeEnPassanted())) {
                    board.setLastMove(null);
                    return false;
                }

                // Save positions
                int fromRow = move.getFromRow();
                int fromCol = move.getFromCol();
                int toRow = move.getToRow();
                int toCol = move.getToCol();

                // Clear captured pawn's square and update its state
                board.setPiece(fromRow, toCol, null);
                if (capturedPiece != null) {
                    capturedPiece.setPosition(-1, -1);
                }

                // Move the attacking pawn
                board.setPiece(toRow, toCol, piece);
                board.setPiece(fromRow, fromCol, null);
                piece.setPosition(toRow, toCol);
            } else if (specialMove == 'C') { // Castling
                // Move the king first
                board.setPiece(move.getToRow(), move.getToCol(), piece);
                board.setPiece(move.getFromRow(), move.getFromCol(), null);
                piece.setPosition(move.getToRow(), move.getToCol());

                // Then move the rook
                int rookFromCol = move.getToCol() > move.getFromCol() ? 7 : 0;
                int rookToCol = move.getToCol() > move.getFromCol() ? move.getToCol() - 1 : move.getToCol() + 1;
                Piece rook = board.getPiece(move.getFromRow(), rookFromCol);
                board.setPiece(move.getFromRow(), rookFromCol, null);
                board.setPiece(move.getFromRow(), rookToCol, rook);
                rook.setPosition(move.getFromRow(), rookToCol);

                if (rook instanceof Rook) {
                    ((Rook) rook).setHasMoved();
                }
                if (piece instanceof King) {
                    ((King) piece).setHasMoved();
                }
            }
        } else {
            // Execute the main move for non-special moves
            board.setPiece(move.getToRow(), move.getToCol(), piece);
            board.setPiece(move.getFromRow(), move.getFromCol(), null);
            piece.setPosition(move.getToRow(), move.getToCol());
        }

        // Handle pawn promotion - only if the move is actually valid
        if (piece instanceof Pawn && (move.getToRow() == 0 || move.getToRow() == 7) && move.getSpecialMove() != '\0') {
            Piece promotedPiece;
            switch (move.getSpecialMove()) {
                case 'Q':
                    promotedPiece = new Queen(piece.isWhite(), move.getToRow(), move.getToCol());
                    break;
                case 'R':
                    promotedPiece = new Rook(piece.isWhite(), move.getToRow(), move.getToCol());
                    break;
                case 'B':
                    promotedPiece = new Bishop(piece.isWhite(), move.getToRow(), move.getToCol());
                    break;
                case 'N':
                    promotedPiece = new Knight(piece.isWhite(), move.getToRow(), move.getToCol());
                    break;
                default:
                    promotedPiece = new Queen(piece.isWhite(), move.getToRow(), move.getToCol()); // Default to Queen
            }
            board.setPiece(move.getToRow(), move.getToCol(), promotedPiece);
            board.setLastMove(move);
        }

        // Update piece status
        if (piece instanceof King) {
            ((King) piece).setHasMoved();
        } else if (piece instanceof Rook) {
            ((Rook) piece).setHasMoved();
        } else if (piece instanceof Pawn) {
            ((Pawn) piece).setHasMoved();
            // Set en passant vulnerability if pawn moved two squares
            if (Math.abs(move.getToRow() - move.getFromRow()) == 2) {
                ((Pawn) piece).setEnPassant(true);
            }
        }

        // Clear en passant flags for all pawns of the moving side
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p instanceof Pawn && p.isWhite() == piece.isWhite() && p != piece) {
                    ((Pawn) p).setEnPassant(false);
                }
            }
        }

        moveHistory.add(move);

        // Switch turns before checking for checkmate
        isWhiteTurn = !isWhiteTurn;

        // Check if this move results in checkmate for the opponent
        if (board.isCheckmate(isWhiteTurn)) {
            gameOver = true;
            // The player who just made the move (opposite of isWhiteTurn) wins
            return true;
        }

        // Check if the opponent is in check
        if (board.isInCheck(isWhiteTurn)) {
            // Opponent is in check but not checkmate
            return true;
        }

        // Check for stalemate
        updateGameStatus();

        return true;
    }

    public boolean isCheckmate() {
        return board.isCheckmate(isWhiteTurn);
    }

    private void updateGameStatus() {
        // Check if the current player is in checkmate
        if (board.isCheckmate(isWhiteTurn)) {
            gameOver = true;
            return;
        }

        // Check for stalemate - no legal moves but not in check
        boolean hasLegalMoves = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.isWhite() == isWhiteTurn) {
                    List<Move> moves = piece.getValidMoves(board);
                    for (Move move : moves) {
                        // Try the move
                        Piece capturedPiece = board.getPiece(move.getToRow(), move.getToCol());
                        board.setPiece(move.getToRow(), move.getToCol(), piece);
                        board.setPiece(move.getFromRow(), move.getFromCol(), null);
                        piece.setPosition(move.getToRow(), move.getToCol());

                        // Check if the move puts/leaves us in check
                        boolean inCheck = board.isInCheck(isWhiteTurn);

                        // Undo the move
                        piece.setPosition(move.getFromRow(), move.getFromCol());
                        board.setPiece(move.getFromRow(), move.getFromCol(), piece);
                        board.setPiece(move.getToRow(), move.getToCol(), capturedPiece);

                        if (!inCheck) {
                            hasLegalMoves = true;
                            break;
                        }
                    }
                }
                if (hasLegalMoves)
                    break;
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

    public boolean isStalemate() {
        return gameOver && !isInCheck();
    }

    public String getGameStatus() {
        if (isCheckmate()) {
            return (isWhiteTurn ? "Black" : "White") + " wins by checkmate!";
        }
        if (isStalemate()) {
            return "Game drawn by stalemate!";
        }
        if (isInCheck()) {
            return (isWhiteTurn ? "White" : "Black") + " is in check!";
        }
        return "";
    }
}

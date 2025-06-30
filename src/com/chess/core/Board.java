package com.chess.core;

public class Board {
    private Piece[][] board = new Piece[8][8];
    
    public Board() {
        initializeBoard();
    }

    public void initializeBoard() {
        // White pieces
        board[0][0] = new Rook(false, 0, 0);
        board[0][1] = new Knight(false, 0, 1);
        board[0][2] = new Bishop(false, 0, 2);
        board[0][3] = new Queen(false, 0, 3);
        board[0][4] = new King(false, 0, 4);
        board[0][5] = new Bishop(false, 0, 5);
        board[0][6] = new Knight(false, 0, 6);
        board[0][7] = new Rook(false, 0, 7);
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(false, 1, col);
        }
        
        // Black pieces
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(true, 6, col);
        }
        board[7][0] = new Rook(true, 7, 0);
        board[7][1] = new Knight(true, 7, 1);
        board[7][2] = new Bishop(true, 7, 2);
        board[7][3] = new Queen(true, 7, 3);
        board[7][4] = new King(true, 7, 4);
        board[7][5] = new Bishop(true, 7, 5);
        board[7][6] = new Knight(true, 7, 6);
        board[7][7] = new Rook(true, 7, 7);

        // Empty squares
        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
    }

    public Piece getPiece(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return board[row][col];
        }
        return null;
    }
    
    public boolean movePiece(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        Piece piece = getPiece(fromRow, fromCol);

        if (piece == null || !piece.getValidMoves(this).contains(move)) {
            return false;
        }

        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = null;
        piece.setPosition(toRow, toCol);
        return true;
    }
    
    // Check if the king of the specified color is in check
    public boolean isInCheck(boolean isWhite) {
        // Find the king
        int kingRow = -1, kingCol = -1;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }
        if (kingRow == -1 || kingCol == -1) {
            return false; // King not found
        }

        // Check the opponent's pieces threatening the King
        for (int row = 0; row < 8; row++) {
            for(int col = 0;col<8;col++){
                Piece piece = board[row][col];
                if (piece != null && piece.isWhite() != isWhite) {
                    for (Move move : piece.getValidMoves(this)) {
                        if (move.getToRow() == kingRow && move.getToCol() == kingCol) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Generate FEN (Forsyth-Edwards Notation) string for Stockfish
    public String toFEN() {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    String symbol = piece.getSymbol();
                    fen.append(symbol.charAt(1));
                    if (!piece.isWhite()) {
                        fen.append(Character.toLowerCase(symbol.charAt(1))); 
                    }
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row < 7) {
                fen.append("/");
            }
        }
        fen.append(" w KQkq - 0 1"); 
        return fen.toString();
    }
}

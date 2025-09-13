package com.chess.core;

public class Board {
    private Piece[][] board = new Piece[8][8];
    private Game game;

    public Board() {
        initializeBoard();
    }

    public void setGame(Game game) {
        this.game = game;
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

    public void setPiece(int row, int col, Piece piece) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            board[row][col] = piece;
            if (piece != null) {
                piece.setPosition(row, col);
            }
        }
    }

    public boolean isCheckmate(boolean isWhite) {
        // First check if the king is in check
        if (!isInCheck(isWhite)) {
            return false;
        }

        // Try all possible moves for all pieces of the current player
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece != null && piece.isWhite() == isWhite) {
                    for (Move move : piece.getValidMoves(this)) {
                        // Try the move
                        Piece capturedPiece = getPiece(move.getToRow(), move.getToCol());
                        Piece movingPiece = getPiece(move.getFromRow(), move.getFromCol());

                        // Make temporary move
                        setPiece(move.getToRow(), move.getToCol(), movingPiece);
                        setPiece(move.getFromRow(), move.getFromCol(), null);

                        // Check if king is still in check after this move
                        boolean stillInCheck = isInCheck(isWhite);

                        // Undo the move
                        setPiece(move.getFromRow(), move.getFromCol(), movingPiece);
                        setPiece(move.getToRow(), move.getToCol(), capturedPiece);

                        // If we found a move that gets us out of check, it's not checkmate
                        if (!stillInCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        // If we haven't found any legal moves to get out of check, it's checkmate
        return true;
    }

    public boolean isSquareAttacked(int targetRow, int targetCol, boolean squareOwner) {
        // Check for attacking pawns
        int pawnDirection = squareOwner ? -1 : 1; // Direction pawns would come from - opposite of pawn's movement
                                                  // direction
        int[] pawnCols = { -1, 1 }; // Diagonal captures
        for (int colOffset : pawnCols) {
            int fromCol = targetCol + colOffset;
            int fromRow = targetRow + pawnDirection;
            if (fromRow >= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8) {
                Piece piece = getPiece(fromRow, fromCol);
                if (piece instanceof Pawn && piece.isWhite() != squareOwner) {
                    return true;
                }
            }
        }

        // Check for attacking knights
        int[][] knightMoves = {
                { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 },
                { 1, -2 }, { 1, 2 }, { 2, -1 }, { 2, 1 }
        };
        for (int[] move : knightMoves) {
            int fromRow = targetRow + move[0];
            int fromCol = targetCol + move[1];
            if (fromRow >= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8) {
                Piece piece = getPiece(fromRow, fromCol);
                if (piece instanceof Knight && piece.isWhite() != squareOwner) {
                    return true;
                }
            }
        }

        // Check for attacking pieces in each direction (queen, rook, bishop)
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] dir : directions) {
            int fromRow = targetRow + dir[0];
            int fromCol = targetCol + dir[1];
            while (fromRow >= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8) {
                Piece piece = getPiece(fromRow, fromCol);
                if (piece != null) {
                    if (piece.isWhite() != squareOwner) {
                        boolean isDiagonal = dir[0] != 0 && dir[1] != 0;
                        boolean isStraight = dir[0] == 0 || dir[1] == 0;

                        if (piece instanceof Queen ||
                                (piece instanceof Rook && isStraight) ||
                                (piece instanceof Bishop && isDiagonal) ||
                                (piece instanceof King && Math.abs(fromRow - targetRow) <= 1
                                        && Math.abs(fromCol - targetCol) <= 1)) {
                            return true;
                        }
                    }
                    break; // Stop checking this direction if we hit any piece
                }
                fromRow += dir[0];
                fromCol += dir[1];
            }
        }

        return false;
    }

    // Check if the king of the specified color is in check
    public boolean isInCheck(boolean isWhite) {
        // Find the king first
        int kingRow = -1, kingCol = -1;
        outer: for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    kingRow = row;
                    kingCol = col;
                    break outer;
                }
            }
        }

        if (kingRow == -1 || kingCol == -1)
            return false;

        return isSquareAttacked(kingRow, kingCol, isWhite);
    }

    // Generate FEN (Forsyth-Edwards Notation) string for Stockfish
    public String toFEN() {
        StringBuilder fen = new StringBuilder();

        // Board position
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
                    char pieceChar = symbol.charAt(1);
                    fen.append(piece.isWhite() ? pieceChar : Character.toLowerCase(pieceChar));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row < 7) {
                fen.append("/");
            }
        }

        // Add active color based on game state
        fen.append(game != null && game.isWhiteTurn() ? " w" : " b");

        // Add castling availability (simplified)
        fen.append(" KQkq");

        // Add en passant target square (simplified)
        fen.append(" -");

        // Add halfmove clock and fullmove number (simplified)
        fen.append(" 0 1");

        return fen.toString();
    }
}

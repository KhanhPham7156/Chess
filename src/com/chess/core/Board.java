package com.chess.core;

public class Board {
    private Piece[][] board = new Piece[8][8];
    private Game game;
    private Move lastMove; // Track the last move made

    public Board() {
        initializeBoard();
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move move) {
        this.lastMove = move;
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
        piece.onMove(); // Notify piece that it has moved
        lastMove = move; // Store the last move
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

    public boolean isPinned(Piece piece) {
        if (piece == null)
            return false;

        // Find the king of the same color
        King king = null;
        int kingRow = -1, kingCol = -1;

        outer: for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = getPiece(row, col);
                if (p instanceof King && p.isWhite() == piece.isWhite()) {
                    king = (King) p;
                    kingRow = row;
                    kingCol = col;
                    break outer;
                }
            }
        }

        if (king == null)
            return false;

        // Determine if the piece is between the king and an attacking piece
        int pieceRow = piece.getRow();
        int pieceCol = piece.getCol();

        // Check if piece is on same rank, file, or diagonal as king
        int rowDiff = kingRow - pieceRow;
        int colDiff = kingCol - pieceCol;

        // Not in line with king
        if (rowDiff != 0 && colDiff != 0 && Math.abs(rowDiff) != Math.abs(colDiff)) {
            return false;
        }

        // Direction from piece to king (we want to look in the opposite direction for
        // attackers)
        int rowDir = rowDiff == 0 ? 0 : rowDiff / Math.abs(rowDiff);
        int colDir = colDiff == 0 ? 0 : colDiff / Math.abs(colDiff);

        // Look for attacking piece in the direction away from the king
        int row = pieceRow - rowDir;
        int col = pieceCol - colDir;

        while (row >= 0 && row < 8 && col >= 0 && col < 8) {
            Piece p = getPiece(row, col);
            if (p != null) {
                if (p.isWhite() != piece.isWhite()) {
                    // Found opponent's piece, check if it can pin in this direction
                    boolean isDiagonal = Math.abs(rowDir) == Math.abs(colDir);
                    boolean isStraight = rowDir == 0 || colDir == 0;

                    if (isStraight && (p instanceof Rook || p instanceof Queen)) {
                        return true;
                    }
                    if (isDiagonal && (p instanceof Bishop || p instanceof Queen)) {
                        return true;
                    }
                }
                // Found any other piece, not a pin
                break;
            }
            row -= rowDir;
            col -= colDir;
        }

        // Verify that the line between the piece and king is clear
        row = pieceRow + rowDir;
        col = pieceCol + colDir;
        while (row != kingRow || col != kingCol) {
            if (getPiece(row, col) != null) {
                // Found a piece between our piece and the king, not a pin
                return false;
            }
            row += rowDir;
            col += colDir;
        }

        return false;
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

        // Add castling availability
        StringBuilder castling = new StringBuilder();

        // White castling
        Piece whiteKing = getPiece(7, 4);
        if (whiteKing instanceof King && !((King) whiteKing).hasMoved()) {
            Piece kingSideRook = getPiece(7, 7);
            if (kingSideRook instanceof Rook && !((Rook) kingSideRook).hasMoved()) {
                castling.append("K");
            }
            Piece queenSideRook = getPiece(7, 0);
            if (queenSideRook instanceof Rook && !((Rook) queenSideRook).hasMoved()) {
                castling.append("Q");
            }
        }

        // Black castling
        Piece blackKing = getPiece(0, 4);
        if (blackKing instanceof King && !((King) blackKing).hasMoved()) {
            Piece kingSideRook = getPiece(0, 7);
            if (kingSideRook instanceof Rook && !((Rook) kingSideRook).hasMoved()) {
                castling.append("k");
            }
            Piece queenSideRook = getPiece(0, 0);
            if (queenSideRook instanceof Rook && !((Rook) queenSideRook).hasMoved()) {
                castling.append("q");
            }
        }

        if (castling.length() == 0) {
            fen.append(" -");
        } else {
            fen.append(" ").append(castling);
        }

        // Add en passant target square
        String enPassant = "-";
        if (lastMove != null) {
            Piece piece = getPiece(lastMove.getToRow(), lastMove.getToCol());
            if (piece instanceof Pawn) {
                if (Math.abs(lastMove.getFromRow() - lastMove.getToRow()) == 2) {
                    int row = (lastMove.getFromRow() + lastMove.getToRow()) / 2;
                    int col = lastMove.getFromCol();
                    enPassant = "" + (char) ('a' + col) + (8 - row);
                }
            }
        }
        fen.append(" ").append(enPassant);

        // Add halfmove clock and fullmove number (simplified)
        fen.append(" 0 1");

        return fen.toString();
    }
}

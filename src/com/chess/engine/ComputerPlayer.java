package com.chess.engine;

import java.io.IOException;
import javax.swing.JOptionPane;
import com.chess.core.*;

public class ComputerPlayer {
    private StockfishEngine engine;
    private static final int SEARCH_DEPTH = 10;

    private void showError(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Chess Engine Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public ComputerPlayer() {
        try {
            engine = new StockfishEngine();
            // Test if engine is working
            String testMove = engine.getBestMove("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 1);
            if (testMove == null) {
                throw new RuntimeException("Stockfish not responding");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Could not initialize Stockfish engine.\nPlease make sure Stockfish is installed correctly.",
                    "Engine Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public Move getMove(Game game) {
        if (engine == null) {
            showError("Stockfish engine is not initialized");
            return null;
        }

        String fen = game.getBoard().toFEN();
        String moveStr;

        try {
            moveStr = engine.getBestMove(fen, SEARCH_DEPTH);
        } catch (IOException e) {
            showError("Error getting move from Stockfish: " + e.getMessage());
            return null;
        }

        if (moveStr == null || moveStr.length() < 4) {
            showError("Invalid move returned from Stockfish");
            return null;
        }

        // Convert UCI move format (e.g., "e2e4") to our Move object
        int fromCol = moveStr.charAt(0) - 'a';
        int fromRow = '8' - moveStr.charAt(1);
        int toCol = moveStr.charAt(2) - 'a';
        int toRow = '8' - moveStr.charAt(3);

        // Check if it's a promotion
        char promotion = '\0';
        if (moveStr.length() > 4) {
            switch (moveStr.charAt(4)) {
                case 'q':
                    promotion = 'Q';
                    break;
                case 'r':
                    promotion = 'R';
                    break;
                case 'b':
                    promotion = 'B';
                    break;
                case 'n':
                    promotion = 'N';
                    break;
            }
        }

        // Check for special moves
        Piece piece = game.getBoard().getPiece(fromRow, fromCol);
        if (piece instanceof King && Math.abs(toCol - fromCol) == 2) {
            // Castling
            return new Move(fromRow, fromCol, toRow, toCol, 'C');
        } else if (piece instanceof Pawn) {
            if (promotion != '\0') {
                return new Move(fromRow, fromCol, toRow, toCol, promotion);
            } else if (fromCol != toCol && game.getBoard().getPiece(toRow, toCol) == null) {
                // En passant
                return new Move(fromRow, fromCol, toRow, toCol, 'E');
            }
        }

        return new Move(fromRow, fromCol, toRow, toCol);
    }

    public void close() {
        if (engine != null) {
            engine.close();
        }
    }
}

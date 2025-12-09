package com.chess.engine;

import javax.swing.JOptionPane;
import com.chess.core.*;

public class ComputerPlayer {
    public static final int TYPE_JAVA_BOT = 1;
    public static final int TYPE_STOCKFISH = 2;

    private int engineType;
    private int difficultyLevel;
    
    // Các engine
    private JavaChessEngine javaEngine;
    private StockfishEngine stockfishEngine;

    public ComputerPlayer(int engineType, int difficultyLevel) {
        this.engineType = engineType;
        this.difficultyLevel = difficultyLevel;

        if (engineType == TYPE_JAVA_BOT) {
            this.javaEngine = new JavaChessEngine();
        } else if (engineType == TYPE_STOCKFISH) {
            try {
                this.stockfishEngine = new StockfishEngine();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Could not start Stockfish! Switching to Java Bot.\nCheck file path in code.", 
                    "Engine Error", JOptionPane.ERROR_MESSAGE);
                this.engineType = TYPE_JAVA_BOT;
                this.javaEngine = new JavaChessEngine();
            }
        }
    }

    public Move getMove(Game game) {
        if (engineType == TYPE_JAVA_BOT) {
            // Mapping độ khó cho Java Bot (1-4)
            int depth;
            if (difficultyLevel <= 5) depth = 1;
            else if (difficultyLevel <= 10) depth = 2;
            else if (difficultyLevel <= 15) depth = 3;
            else depth = 4;
            
            return javaEngine.getBestMove(game, depth);
        } 
        else {
            // Stockfish Mode
            if (stockfishEngine == null) return null;
            
            String fen = game.getBoard().toFEN();
            try {
                // Mapping độ khó Stockfish (dùng trực tiếp level 1-20 làm depth hoặc tính toán)
                int depth = Math.max(1, difficultyLevel);
                String moveStr = stockfishEngine.getBestMove(fen, depth);
                return parseUCIMove(moveStr, game.getBoard());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // Chuyển đổi nước đi dạng String (e2e4) từ Stockfish thành object Move
    private Move parseUCIMove(String moveStr, Board board) {
        if (moveStr == null || moveStr.length() < 4) return null;

        int fromCol = moveStr.charAt(0) - 'a';
        int fromRow = '8' - moveStr.charAt(1);
        int toCol = moveStr.charAt(2) - 'a';
        int toRow = '8' - moveStr.charAt(3);

        char promotion = '\0';
        if (moveStr.length() > 4) {
            switch (moveStr.charAt(4)) {
                case 'q': promotion = 'Q'; break;
                case 'r': promotion = 'R'; break;
                case 'b': promotion = 'B'; break;
                case 'n': promotion = 'N'; break;
            }
        }

        // Kiểm tra nước đi đặc biệt
        Piece piece = board.getPiece(fromRow, fromCol);
        if (piece instanceof King && Math.abs(toCol - fromCol) == 2) {
            return new Move(fromRow, fromCol, toRow, toCol, 'C');
        } else if (piece instanceof Pawn) {
            if (promotion != '\0') {
                return new Move(fromRow, fromCol, toRow, toCol, promotion);
            } else if (fromCol != toCol && board.getPiece(toRow, toCol) == null) {
                return new Move(fromRow, fromCol, toRow, toCol, 'E');
            }
        }
        return new Move(fromRow, fromCol, toRow, toCol);
    }

    public void close() {
        if (stockfishEngine != null) {
            stockfishEngine.close();
        }
        // Java engine does not need closing
    }
}
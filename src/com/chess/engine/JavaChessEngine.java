package com.chess.engine;

import com.chess.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JavaChessEngine {

    // --- PIECE VALUES ---
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;

    // --- PIECE SQUARE TABLES ---
    private static final int[][] PAWN_TABLE = {
        {0,  0,  0,  0,  0,  0,  0,  0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {5,  5, 10, 25, 25, 10,  5,  5},
        {0,  0,  0, 20, 20,  0,  0,  0},
        {5, -5,-10,  0,  0,-10, -5,  5},
        {5, 10, 10,-20,-20, 10, 10,  5},
        {0,  0,  0,  0,  0,  0,  0,  0}
    };

    private static final int[][] KNIGHT_TABLE = {
        {-50,-40,-30,-30,-30,-30,-40,-50},
        {-40,-20,  0,  0,  0,  0,-20,-40},
        {-30,  0, 10, 15, 15, 10,  0,-30},
        {-30,  5, 15, 20, 20, 15,  5,-30},
        {-30,  0, 15, 20, 20, 15,  0,-30},
        {-30,  5, 10, 15, 15, 10,  5,-30},
        {-40,-20,  0,  5,  5,  0,-20,-40},
        {-50,-40,-30,-30,-30,-30,-40,-50}
    };

    private static final int[][] BISHOP_TABLE = {
        {-20,-10,-10,-10,-10,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5, 10, 10,  5,  0,-10},
        {-10,  5,  5, 10, 10,  5,  5,-10},
        {-10,  0, 10, 10, 10, 10,  0,-10},
        {-10, 10, 10, 10, 10, 10, 10,-10},
        {-10,  5,  0,  0,  0,  0,  5,-10},
        {-20,-10,-10,-10,-10,-10,-10,-20}
    };

    private static final int[][] ROOK_TABLE = {
        {0,  0,  0,  0,  0,  0,  0,  0},
        {5, 10, 10, 10, 10, 10, 10,  5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {0,  0,  0,  5,  5,  0,  0,  0}
    };

    private static final int[][] QUEEN_TABLE = {
        {-20,-10,-10, -5, -5,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5,  5,  5,  5,  0,-10},
        {-5,   0,  5,  5,  5,  5,  0, -5},
        {-5,   0,  5,  5,  5,  5,  0, -5},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-20,-10,-10, -5, -5,-10,-10,-20}
    };

    private static final int[][] KING_MID_TABLE = {
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-20,-30,-30,-40,-40,-30,-30,-20},
        {-10,-20,-20,-20,-20,-20,-20,-10},
        {20, 20,  0,  0,  0,  0, 20, 20},
        {20, 40, 10,  0,  0, 10, 40, 20}
    };

    private static final int[][] KING_END_TABLE = {
        {-50,-40,-30,-20,-20,-30,-40,-50},
        {-30,-20,-10,  0,  0,-10,-20,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-30,  0,  0,  0,  0,-30,-30},
        {-50,-30,-30,-30,-30,-30,-30,-50}
    };

    private boolean isWhiteEngine;

    // Helper class to store move and its score
    private class MoveScore {
        Move move;
        int score;
        MoveScore(Move move, int score) {
            this.move = move;
            this.score = score;
        }
    }

    // Helper class to store exact state for undoing moves
    // CỰC KỲ QUAN TRỌNG: Phải lưu tham chiếu đến chính object quân cờ ban đầu
    private class BoardState {
        Move move;
        Piece capturedPiece; // Quân cờ bị ăn (nếu có)
        Piece originalPiece; // Quân cờ thực hiện nước đi (trước khi di chuyển)
        boolean wasPromotion; // Đánh dấu nếu đây là nước phong cấp giả lập
    }

    public Move getBestMove(Game game, int depth) {
        // Xác định AI cầm quân nào
        this.isWhiteEngine = game.isWhiteTurn(); 
        
        Board board = game.getBoard();
        
        // Giới hạn độ sâu để tránh lag (Java thuần không mạnh bằng C++ Stockfish)
        int effectiveDepth = Math.min(depth, 4); 

        MoveScore result = minimax(board, effectiveDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        
        // Fallback: Nếu không tìm thấy nước đi (hiếm), random một nước hợp lệ
        if (result.move == null) {
             List<Move> allMoves = getAllLegalMoves(board, isWhiteEngine);
             if (!allMoves.isEmpty()) {
                 return allMoves.get(new Random().nextInt(allMoves.size()));
             }
             return null;
        }
        
        return result.move;
    }

    private MoveScore minimax(Board board, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0) {
            return new MoveScore(null, evaluateBoard(board));
        }

        boolean currentTurnIsWhite = maximizing ? isWhiteEngine : !isWhiteEngine;
        List<Move> moves = getAllLegalMoves(board, currentTurnIsWhite);

        if (moves.isEmpty()) {
            if (board.isInCheck(currentTurnIsWhite)) {
                // Checkmate: Điểm cực thấp nếu là lượt mình, cực cao nếu lượt đối thủ
                return new MoveScore(null, maximizing ? -100000 - depth : 100000 + depth);
            }
            return new MoveScore(null, 0); // Stalemate (Hòa cờ)
        }

        orderMoves(board, moves);
        Move bestMove = moves.get(0);

        if (maximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (Move move : moves) {
                // Thực hiện nước đi giả lập
                BoardState state = makeInternalMove(board, move);
                
                int score = minimax(board, depth - 1, alpha, beta, false).score;
                
                // Hoàn tác nước đi giả lập (Quan trọng!)
                undoInternalMove(board, state);

                if (score > maxScore) {
                    maxScore = score;
                    bestMove = move;
                }
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return new MoveScore(bestMove, maxScore);
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Move move : moves) {
                BoardState state = makeInternalMove(board, move);
                
                int score = minimax(board, depth - 1, alpha, beta, true).score;
                
                undoInternalMove(board, state);

                if (score < minScore) {
                    minScore = score;
                    bestMove = move;
                }
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return new MoveScore(bestMove, minScore);
        }
    }

    // --- LOGIC DI CHUYỂN AN TOÀN ---

    private BoardState makeInternalMove(Board board, Move move) {
        BoardState state = new BoardState();
        state.move = move;
        state.capturedPiece = board.getPiece(move.getToRow(), move.getToCol());
        state.originalPiece = board.getPiece(move.getFromRow(), move.getFromCol());
        state.wasPromotion = false;

        // Xử lý Phong Cấp (Pawn Promotion)
        // Nếu Tốt đi đến hàng cuối, ta giả lập nó biến thành Hậu để tính điểm
        if (state.originalPiece instanceof Pawn && (move.getToRow() == 0 || move.getToRow() == 7)) {
            state.wasPromotion = true;
            // Tạo quân Hậu mới thay thế
            Piece promoQueen = new Queen(state.originalPiece.isWhite(), move.getToRow(), move.getToCol());
            board.setPiece(move.getToRow(), move.getToCol(), promoQueen);
            // Xóa quân Tốt ở vị trí cũ
            board.setPiece(move.getFromRow(), move.getFromCol(), null);
        } else {
            // Di chuyển thông thường
            board.setPiece(move.getToRow(), move.getToCol(), state.originalPiece);
            board.setPiece(move.getFromRow(), move.getFromCol(), null);
            
            // Cập nhật tọa độ nội bộ của quân cờ
            if (state.originalPiece != null) {
                state.originalPiece.setPosition(move.getToRow(), move.getToCol());
            }
        }

        return state;
    }

    private void undoInternalMove(Board board, BoardState state) {
        Move move = state.move;
        
        // 1. Khôi phục quân cờ di chuyển về vị trí cũ (BẮT BUỘC dùng object gốc)
        board.setPiece(move.getFromRow(), move.getFromCol(), state.originalPiece);
        if (state.originalPiece != null) {
            state.originalPiece.setPosition(move.getFromRow(), move.getFromCol());
        }

        // 2. Khôi phục quân bị ăn (nếu có) về vị trí đích
        board.setPiece(move.getToRow(), move.getToCol(), state.capturedPiece);
        if (state.capturedPiece != null) {
            state.capturedPiece.setPosition(move.getToRow(), move.getToCol());
        }

        // Lưu ý: Logic này đảm bảo rằng dù có phong cấp hay không, 
        // ta luôn trả lại ĐÚNG con Tốt cũ (state.originalPiece) về ô xuất phát,
        // và trả lại ĐÚNG con cờ bị ăn (state.capturedPiece) về ô đích.
        // Điều này ngăn chặn việc Vua biến mất hoặc Hậu biến thành Tốt.
    }

    // --- EVALUATION & UTILS ---

    private int evaluateBoard(Board board) {
        int whiteScore = 0;
        int blackScore = 0;
        int pieceCount = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    pieceCount++;
                    // Mặc định tính điểm Midgame
                    int val = getPieceValue(p) + getPositionValue(p, false); 
                    if (p.isWhite()) whiteScore += val;
                    else blackScore += val;
                }
            }
        }
        
        // Nếu ít quân (Endgame), tính điểm theo bảng Endgame
        boolean isEndgame = pieceCount < 12;
        if (isEndgame) {
            whiteScore = 0;
            blackScore = 0;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece p = board.getPiece(r, c);
                    if (p != null) {
                        int val = getPieceValue(p) + getPositionValue(p, true);
                        if (p.isWhite()) whiteScore += val;
                        else blackScore += val;
                    }
                }
            }
        }

        int score = whiteScore - blackScore;
        return isWhiteEngine ? score : -score;
    }

    private int getPieceValue(Piece p) {
        if (p instanceof Pawn) return PAWN_VALUE;
        if (p instanceof Knight) return KNIGHT_VALUE;
        if (p instanceof Bishop) return BISHOP_VALUE;
        if (p instanceof Rook) return ROOK_VALUE;
        if (p instanceof Queen) return QUEEN_VALUE;
        if (p instanceof King) return KING_VALUE;
        return 0;
    }

    private int getPositionValue(Piece p, boolean isEndgame) {
        int r = p.getRow();
        int c = p.getCol();
        
        // Bảng điểm được thiết kế cho quân Trắng (hàng 0 là phía đối thủ, hàng 7 là nhà)
        // Nếu là quân Đen, ta lật ngược hàng lại (hàng 0 thành 7, 7 thành 0)
        int tableRow = p.isWhite() ? r : 7 - r;
        int tableCol = c; 

        if (p instanceof Pawn) return PAWN_TABLE[tableRow][tableCol];
        if (p instanceof Knight) return KNIGHT_TABLE[tableRow][tableCol];
        if (p instanceof Bishop) return BISHOP_TABLE[tableRow][tableCol];
        if (p instanceof Rook) return ROOK_TABLE[tableRow][tableCol];
        if (p instanceof Queen) return QUEEN_TABLE[tableRow][tableCol];
        if (p instanceof King) return isEndgame ? KING_END_TABLE[tableRow][tableCol] : KING_MID_TABLE[tableRow][tableCol];
        
        return 0;
    }

    private List<Move> getAllLegalMoves(Board board, boolean isWhite) {
        List<Move> allMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.isWhite() == isWhite) {
                    allMoves.addAll(p.getValidMoves(board));
                }
            }
        }
        return allMoves;
    }

    private void orderMoves(Board board, List<Move> moves) {
        moves.sort((m1, m2) -> {
            int score1 = 0;
            int score2 = 0;
            
            Piece cap1 = board.getPiece(m1.getToRow(), m1.getToCol());
            Piece cap2 = board.getPiece(m2.getToRow(), m2.getToCol());
            
            Piece piece1 = board.getPiece(m1.getFromRow(), m1.getFromCol());
            Piece piece2 = board.getPiece(m2.getFromRow(), m2.getFromCol());
            
            // Ưu tiên ăn quân giá trị cao bằng quân giá trị thấp (MVV-LVA)
            if (cap1 != null && piece1 != null) score1 = 10 * getPieceValue(cap1) - getPieceValue(piece1);
            if (cap2 != null && piece2 != null) score2 = 10 * getPieceValue(cap2) - getPieceValue(piece2);
            
            // Ưu tiên phong cấp
            if (m1.getSpecialMove() == 'Q') score1 += 800;
            if (m2.getSpecialMove() == 'Q') score2 += 800;
            
            return score2 - score1;
        });
    }
}
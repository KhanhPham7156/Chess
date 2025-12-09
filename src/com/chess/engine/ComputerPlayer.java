package com.chess.engine;

import com.chess.core.*;

public class ComputerPlayer {
    private JavaChessEngine engine;
    private int difficultyLevel;

    /*
     * Difficulty Level to Depth mapping:
     * Levels 1-5: Random or Depth 1
     * Levels 6-10: Depth 2
     * Levels 11-15: Depth 3
     * Levels 16-20: Depth 4 (Slow on Java without optimizations)
     */

    public ComputerPlayer(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        this.engine = new JavaChessEngine();
    }

    public Move getMove(Game game) {
        // Calculate search depth based on difficulty
        // Keep depth conservative to prevent UI freezing since this runs on a simple thread
        int depth;
        if (difficultyLevel <= 5) depth = 1;
        else if (difficultyLevel <= 10) depth = 2;
        else if (difficultyLevel <= 15) depth = 3;
        else depth = 4;

        try {
            // Introduce a small artificial delay for lower levels to feel "human"
            // and ensure the UI has repainted the previous move
            Thread.sleep(200);
            
            Move bestMove = engine.getBestMove(game, depth);
            
            if (bestMove == null) {
                System.out.println("Engine could not find a move (Stalemate or Checkmate)");
            }
            return bestMove;
            
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        // Java engine doesn't need explicit closing, but method kept for API compatibility
        engine = null;
    }
}
package com.chess.engine;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class StockfishEngine {
    private Process engineProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private static final String STOCKFISH_PATH = "resources/stockfish/stockfish-windows-x86-64-avx2.exe";

    public StockfishEngine() {
        try {
            // Start Stockfish process
            engineProcess = new ProcessBuilder(STOCKFISH_PATH).start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBestMove(String fen, int searchDepth) throws IOException {
        // Set up the position with the right side to move
        sendCommand("position fen " + fen);
        sendCommand("go depth " + searchDepth);

        String line;
        String bestMove = null;

        // Read until we find the best move or timeout after 5 seconds
        long startTime = System.currentTimeMillis();
        while ((line = processReader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                bestMove = line.split(" ")[1];
                break;
            }
            // Timeout after 5 seconds
            if (System.currentTimeMillis() - startTime > 5000) {
                // Send stop command to engine
                sendCommand("stop");
                throw new IOException("Stockfish timed out after 5 seconds");
            }
        }

        // Clear any remaining output
        while (processReader.ready()) {
            processReader.readLine();
        }

        if (bestMove == null) {
            throw new IOException("Stockfish did not return a move");
        }

        return bestMove;
    }

    private void sendCommand(String command) throws IOException {
        processWriter.write(command + "\n");
        processWriter.flush();
    }

    public void close() {
        try {
            sendCommand("quit");
            processWriter.close();
            processReader.close();
            engineProcess.destroy();
            engineProcess.waitFor(2, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

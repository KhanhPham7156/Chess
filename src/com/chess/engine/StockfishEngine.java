package com.chess.engine;

import java.io.*;
import java.util.concurrent.*;

public class StockfishEngine {
    private Process engineProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private static final String STOCKFISH_PATH = "resources/stockfish/stockfish-windows-x86-64-avx2.exe";

    private Thread readerThread;
    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
    private volatile boolean isRunning = false;

    public StockfishEngine() throws IOException {
        startEngine();
    }

    private void startEngine() throws IOException {
        File engineFile = new File(STOCKFISH_PATH);
        if (!engineFile.exists()) {
            throw new FileNotFoundException("Stockfish engine not found at: " + engineFile.getAbsolutePath());
        }

        ProcessBuilder pb = new ProcessBuilder(STOCKFISH_PATH);
        pb.redirectErrorStream(true);
        engineProcess = pb.start();

        processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
        processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));

        isRunning = true;
        readerThread = new Thread(this::readOutputLoop);
        readerThread.setDaemon(true);
        readerThread.start();

        sendCommand("uci");
        if (waitForResponse("uciok", 5000) == null) {
            throw new IOException("Stockfish failed to initialize (no uciok)");
        }

        sendCommand("ucinewgame");
        sendCommand("isready");
        if (waitForResponse("readyok", 5000) == null) {
            throw new IOException("Stockfish failed to be ready (no readyok)");
        }
    }

    private void readOutputLoop() {
        try {
            String line;
            while (isRunning && (line = processReader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("bestmove") || trimmed.equals("readyok") || trimmed.equals("uciok")) {
                    outputQueue.offer(trimmed);
                }
            }
        } catch (IOException e) {
        }
    }

    private String waitForResponse(String expectedStart, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                String line = outputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (line != null && line.startsWith(expectedStart)) {
                    return line;
                }
                if (engineProcess != null && !engineProcess.isAlive()) {
                    return null;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    public String getBestMove(String fen, int searchDepth) throws IOException {
        if (engineProcess == null || !engineProcess.isAlive()) {
            restart();
        }
        outputQueue.clear();

        // Thời gian suy nghĩ tăng theo độ khó
        int moveTimeMs = Math.min(3000, 50 + (searchDepth * 100));

        try {
            sendCommand("position fen " + fen);
            sendCommand("go movetime " + moveTimeMs);
        } catch (IOException e) {
            restart();
            throw e;
        }

        String response = waitForResponse("bestmove", moveTimeMs + 3000);
        if (response == null) {
            try {
                sendCommand("stop");
                response = waitForResponse("bestmove", 1000);
            } catch (IOException e) {
            }
            if (response == null) {
                restart();
                throw new IOException("Stockfish did not return a move");
            }
        }

        String[] parts = response.split(" ");
        return parts.length > 1 ? parts[1] : null;
    }

    private void sendCommand(String command) throws IOException {
        if (processWriter == null)
            throw new IOException("Engine not connected");
        processWriter.write(command + "\n");
        processWriter.flush();
    }

    private void restart() throws IOException {
        close();
        startEngine();
    }

    public void close() {
        isRunning = false;
        if (processWriter != null) {
            try {
                sendCommand("quit");
                processWriter.close();
            } catch (IOException e) {
            }
        }
        if (processReader != null) {
            try {
                processReader.close();
            } catch (IOException e) {
            }
        }
        if (engineProcess != null) {
            engineProcess.destroyForcibly();
        }
    }
}
package com.example.nqueenssolver.solver;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NQueensThread extends Thread {
    private final int threadNumber;
    private final int n;
    private final Random random;
    private final ChessboardPanel chessboardPanel; // Added ChessboardPanel instance
    private final NQueensSolver solver;
    private volatile boolean solutionFound = false;
    private final Object lock = new Object(); // Add a lock object for synchronization

    public NQueensThread(int threadNumber, int n, Random random, NQueensThread.ChessboardPanel chessboardPanel) {
        this.threadNumber = threadNumber;
        this.n = n;
        this.random = new Random(); // Create a new Random instance for each thread
        this.chessboardPanel = chessboardPanel;
        this.solver = new NQueensSolver(n, random);
    }

    @Override
    public void run() {
        System.out.println("Thread " + threadNumber + " started.");

        // Solve N-Queens problem for this thread
        solveWithVisualization();

        System.out.println("Thread " + threadNumber + " finished.");

        synchronized (lock) {
            if (isSolutionFound()) {
                // Display a message indicating the thread has found a solution
                showMessage("Thread " + threadNumber + " found a solution!");

                // Resume the thread after displaying the message
                lock.notify();
            } else {
                // Display a message indicating the thread has finished without finding a solution
                showMessage("Thread " + threadNumber + " has finished its task.");
            }
        }
    }

    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Thread Finished", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void solveWithVisualization() {
        try {
            solveNQueensWithVisualization(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }

    private void solveNQueensWithVisualization(int row) throws InterruptedException {
        if (row == n) {
            // All queens are placed successfully
            synchronized (lock) {
                solutionFound = true; // Set the flag to true when a solution is found
                showMessage("Thread " + threadNumber + " found a solution!");
                // Pause the thread here to keep the solution displayed
                lock.wait();
            }
            return;
        }
    
        int[] shuffledColumns = getShuffledColumns();
    
        for (int i = 0; i < n; i++) {
            int col = shuffledColumns[i];
            if (isSafe(row, col)) {
                // Place the queen in this cell
                solver.placeQueen(row, col);
    
                // Publish intermediate state to update UI
                updateChessboardDisplay(solver.getQueens().clone());
                Thread.sleep(500); // Adjust sleep duration for visualization speed
    
                // Recur to place queens in the remaining rows
                solveNQueensWithVisualization(row + 1);
    
                // If placing queen in the current cell doesn't lead to a solution, backtrack
                solver.removeQueen(row);
            }
        }
    }    

    private void updateChessboardDisplay(int[] queens) {
        SwingUtilities.invokeLater(() -> {
            chessboardPanel.updateQueens(queens);
            chessboardPanel.repaint();
        });
    }

    private int[] getShuffledColumns() {
        int[] columns = new int[n];
        for (int i = 0; i < n; i++) {
            columns[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            // Swap columns[i] and columns[j]
            int temp = columns[i];
            columns[i] = columns[j];
            columns[j] = temp;
        }

        return columns;
    }

    private boolean isSafe(int row, int col) {
        // Check if no queens are in the same column or diagonals
        for (int i = 0; i < row; i++) {
            if (solver.getQueens()[i] == col || Math.abs(solver.getQueens()[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    public static class ChessboardPanel extends JPanel {
        private int[] queens;

        public ChessboardPanel(int[] queens) {
            this.queens = queens;
        }

        public void updateQueens(int[] queens) {
            this.queens = queens;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int size = queens.length;
            int cellSize = getWidth() / size;

            // Draw chessboard
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if ((i + j) % 2 == 0) {
                        g.setColor(Color.LIGHT_GRAY);
                    } else {
                        g.setColor(Color.DARK_GRAY);
                    }
                    g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }

            // Draw queens
            g.setColor(Color.RED);
            for (int i = 0; i < size; i++) {
                int col = queens[i];
                g.fillOval(col * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }
}
package com.example.nqueenssolver.solver;

import java.util.Random;

public class NQueensSolver {

    private int[] queens; // queens[i] represents the column number of the queen in the i-th row
    private int n; // size of the chessboard
    private Random random; // Random instance for each solver

    public NQueensSolver(int n, Random random) {
        this.n = n;
        this.queens = new int[n];
        this.random = random;

        // Initialize the queens array to -1, indicating that no queens are placed initially
        for (int i = 0; i < n; i++) {
            queens[i] = -1;
        }
    }

    public boolean solve() {
        return solveNQueens(0);
    }

    private boolean solveNQueens(int row) {
        if (row == n) {
            // All queens are placed successfully
            return true;
        }

        // Shuffle the column indices to randomize the order
        int[] shuffledColumns = getShuffledColumns();

        for (int i = 0; i < n; i++) {
            int col = shuffledColumns[i];
            if (isSafe(row, col)) {
                // Place the queen in this cell
                queens[row] = col;

                // Recur to place queens in the remaining rows
                if (solveNQueens(row + 1)) {
                    return true;
                }

                // If placing queen in the current cell doesn't lead to a solution, backtrack
                queens[row] = -1;
            }
        }

        // No valid placement in this row
        return false;
    }

    private boolean isSafe(int row, int col) {
        // Check if no queens are in the same column or diagonals
        for (int i = 0; i < row; i++) {
            if (queens[i] == col || Math.abs(queens[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
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

    public int[] getQueens() {
        return queens.clone();
    }

    public int[][] getChessboard() {
        int[][] chessboard = new int[n][n];
        // Fill the chessboard based on queen placements
        for (int i = 0; i < n; i++) {
            int queenCol = queens[i];
            if (queenCol != -1) {
                chessboard[i][queenCol] = 1; // 1 represents a queen
            }
        }
        return chessboard;
    }

    // New method to place a queen at a specific row and column
    public void placeQueen(int row, int col) {
        queens[row] = col;
    }

    // New method to remove a queen from a specific row
    public void removeQueen(int row) {
        queens[row] = -1;
    }
}
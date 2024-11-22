package com.example.nqueenssolver.gui;

import com.example.nqueenssolver.solver.NQueensThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class NQueensSolverGUI extends JFrame {
    private static int offsetX = 0;  // Static variable to track horizontal offset

    public NQueensSolverGUI() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("N-Queens Solver");

        JLabel label = new JLabel("Enter the number of queens (n):");
        JTextField textField = new JTextField(10);
        JButton solveButton = new JButton("Solve");

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int n = Integer.parseInt(textField.getText());

                    // Ensure n is a valid value
                    if (n > 0) {
                        solveNQueens(n);
                    } else {
                        JOptionPane.showMessageDialog(NQueensSolverGUI.this,
                                "Please enter a positive integer for the number of queens.",
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(NQueensSolverGUI.this,
                            "Please enter a valid integer for the number of queens.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(label);
        panel.add(textField);
        panel.add(solveButton);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void solveNQueens(int n) {
        Random random = new Random();  // Create a Random instance

        for (int i = 0; i < n; i++) {
            // Create a new thread for each solver with the Random instance and ChessboardPanel instance
            NQueensThread.ChessboardPanel chessboardPanel = new NQueensThread.ChessboardPanel(new int[n]);
            NQueensThread thread = new NQueensThread(i, n, random, chessboardPanel);
            thread.start(); // Use execute() instead of start() for SwingWorker

            // Add the ChessboardPanel to the GUI with horizontal offset and thread number in the title
            addChessboardPanelToGUI(chessboardPanel, i);
        }
    }

    private void addChessboardPanelToGUI(NQueensThread.ChessboardPanel chessboardPanel, int threadNumber) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thread " + threadNumber + " Solution");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(400, 400);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            int screenWidth = ge.getDefaultScreenDevice().getDisplayMode().getWidth();

            // Set location relative to the previous frame with increased spacing
            if (offsetX + 420 > screenWidth) {
                // Start a new row
                offsetX = 20;  // Reset horizontal offset
            }

            int offsetY = (offsetX == 20) ? 0 : 420;  // Determine vertical offset based on a new row or not
            frame.setLocation(offsetX, offsetY);
            offsetX += 420;  // Increase the horizontal spacing

            frame.add(chessboardPanel);

            frame.setVisible(true);
        });
    }
}
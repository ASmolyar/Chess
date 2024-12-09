package org.cis1200;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class RunChess implements Runnable {
    @Override
    public void run() {
        // Top-level frame
        final JFrame frame = new JFrame("Chess");
        frame.setLocation(300, 300);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("White's Turn");
        status_panel.add(status);

        // Game board
        final ChessBoard board = new ChessBoard(status);
        frame.add(board, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> board.reset());
        control_panel.add(reset);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        showInstructions();
    }

    private void showInstructions() {
        String instructions = """
                Chess Game Instructions:

                1. The game starts with White's turn
                2. Click on a piece to select it
                3. Click on a valid square to move the piece
                4. Captured pieces will be shown in the side panels
                5. The game ends when:
                   - A player is checkmated
                   - A stalemate occurs
                   - Players agree to a draw
                   - 50-move rule is reached
                   - Threefold repetition occurs

                Click OK to start the game!
                """;

        JOptionPane.showMessageDialog(
                null, instructions, "How to Play",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
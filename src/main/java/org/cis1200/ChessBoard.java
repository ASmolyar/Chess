package org.cis1200;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cis1200.util.Piece;

public class ChessBoard extends JPanel {
    private Board gameBoard; // model for the game
    private JPanel[][] squares; // squares on the board
    private JLabel status; // current status text
    private Piece selectedPiece; // currently selected piece

    // Game constants
    public static final int BOARD_SIZE = 8;
    public static final int SQUARE_SIZE = 80;
    
    // Colors
    private static final Color LIGHT_SQUARE = new Color(0xf0, 0xf1, 0xf0);
    private static final Color DARK_SQUARE = new Color(0x84, 0x77, 0xba);
    private static final Color HIGHLIGHT_SQUARE = new Color(196, 196, 136);
    private static final Color LEGAL_MOVE_SQUARE = new Color(154, 196, 136);

    /**
     * Initializes the game board.
     */
    public ChessBoard(JLabel statusInit) {
        // Creates border around the board
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Layout manager for the board
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        // Initialize model and status
        this.gameBoard = Board.starterBoard();
        this.status = statusInit;

        // Create squares array and initialize board
        squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();

        // Update display
        updateStatus();
    }

    private void initializeBoard() {
        // Create the checkerboard pattern
        boolean isLight = true;
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        
        // Initialize squares array from bottom to top
        squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
        
        // Create squares from bottom to top, left to right
        for (int rank = BOARD_SIZE - 1; rank >= 0; rank--) {  // Start from top rank (7) to bottom rank (0)
            for (int file = 0; file < BOARD_SIZE; file++) {  // Left to right (0 = file a, 7 = file h)
                JPanel square = new JPanel(new BorderLayout());
                square.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                square.setBackground(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                square.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                // Add mouse listener to handle moves
                final int r = rank;
                final int f = file;
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(r, f);
                    }
                });

                squares[rank][file] = square;
                add(square);  //  add to GridLayout
                isLight = !isLight;
            }
            isLight = !isLight;
        }
        updateBoard();
    }

    private void handleSquareClick(int rank, int file) {
        Piece clickedPiece = gameBoard.getPiece(new int[] { file, rank });

        // If no piece is selected and clicked on a piece of current player's color
        if (selectedPiece == null && clickedPiece != null &&
                clickedPiece.getColor() == gameBoard.getToMove()) {
            selectedPiece = clickedPiece;
            highlightSquare(rank, file);
            highlightLegalMoves(clickedPiece);
        }
        // If a piece is selected
        else if (selectedPiece != null) {
            // Try to make the move
            try {
                gameBoard.movePiece(selectedPiece, new int[] { file, rank });
                clearHighlights();
                updateBoard();
            } catch (IllegalArgumentException ex) {
                // Invalid move, deselect piece
                System.out.println("Invalid move: " + ex.getMessage());
                clearHighlights();
                updateStatus();
            }

            // Clear selection
            selectedPiece = null;
        } else {
            // Clicked on empty square or opponent's piece with no selection
            clearHighlights();
            updateStatus();
        }
    }

    private void highlightSquare(int rank, int file) {
        squares[rank][file].setBackground(HIGHLIGHT_SQUARE);
    }

    private void highlightLegalMoves(Piece piece) {
        for (int[] move : piece.getLegalMoves()) {
            squares[move[1]][move[0]].setBackground(LEGAL_MOVE_SQUARE);
        }
    }

    private void clearHighlights() {
        // Start with light square at bottom right (a1)
        boolean isLight = true;
        for (int rank = BOARD_SIZE - 1; rank >= 0; rank--) {
            for (int file = 0; file < BOARD_SIZE; file++) {
                squares[rank][file].setBackground(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                isLight = !isLight;
            }
            isLight = !isLight;
        }
    }

    private void updateBoard() {
        // Update all squares
        for (int rank = BOARD_SIZE - 1; rank >= 0; rank--) {
            for (int file = 0; file < BOARD_SIZE; file++) {
                // Clear the square
                squares[rank][file].removeAll();
                
                // Add piece label if there is a piece
                Piece piece = gameBoard.getPiece(new int[] { file, rank });
                if (piece != null) {
                    JLabel pieceLabel = new JLabel(getPieceSymbol(piece), JLabel.CENTER);
                    pieceLabel.setForeground(piece.getColor() == Piece.Color.WHITE ? Color.WHITE : Color.BLACK);
                    pieceLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 48));
                    pieceLabel.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                    squares[rank][file].add(pieceLabel);
                }

                // Revalidate and repaint the square
                squares[rank][file].revalidate();
                squares[rank][file].repaint();
            }
        }
        
        // Update game status
        updateStatus();
    }

    private void updateStatus() {
        if (gameBoard.isGameOver()) {
            if (gameBoard.isCheckmate(gameBoard.getToMove())) {
                status.setText(
                        (gameBoard.getToMove() == Piece.Color.WHITE ? "Black" : "White") +
                                " wins by checkmate!"
                );
            } else {
                status.setText("Game Over - Draw!");
            }
        } else {
            status.setText(
                    (gameBoard.getToMove() == Piece.Color.WHITE ? "White" : "Black") +
                            "'s turn"
            );
        }
    }

    public void reset() {
        gameBoard = Board.starterBoard();
        selectedPiece = null;
        clearHighlights();
        updateBoard();
        updateStatus();
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_SIZE * SQUARE_SIZE, BOARD_SIZE * SQUARE_SIZE);
    }

    private String getPieceSymbol(Piece piece) {
        String symbol = switch (piece.getType()) {
            case KING -> "♔";
            case QUEEN -> "♕";
            case ROOK -> "♖";
            case BISHOP -> "♗";
            case KNIGHT -> "♘";
            case PAWN -> "♙";
        };

        // Use black symbols for black pieces
        if (piece.getColor() == Piece.Color.BLACK) {
            symbol = symbol.replace('♔', '♚')
                    .replace('♕', '♛')
                    .replace('♖', '♜')
                    .replace('♗', '♝')
                    .replace('♘', '♞')
                    .replace('♙', '♟');
        }

        return symbol;
    }
}
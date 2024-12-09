package org.cis1200;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
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

    // Add image fields
    private final Image blackBishop;
    private final Image blackKing;
    private final Image blackKnight;
    private final Image blackPawn;
    private final Image blackQueen;
    private final Image blackRook;
    private final Image whiteBishop;
    private final Image whiteKing;
    private final Image whiteKnight;
    private final Image whitePawn;
    private final Image whiteQueen;
    private final Image whiteRook;

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

        // Load images
        try {
            blackBishop = ImageIO.read(getClass().getResource("/black_bishop.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            blackKing = ImageIO.read(getClass().getResource("/black_king.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            blackKnight = ImageIO.read(getClass().getResource("/black_knight.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            blackPawn = ImageIO.read(getClass().getResource("/black_pawn.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            blackQueen = ImageIO.read(getClass().getResource("/black_queen.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            blackRook = ImageIO.read(getClass().getResource("/black_rook.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            whiteBishop = ImageIO.read(getClass().getResource("/white_bishop.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            whiteKing = ImageIO.read(getClass().getResource("/white_king.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            whiteKnight = ImageIO.read(getClass().getResource("/white_knight.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            whitePawn = ImageIO.read(getClass().getResource("/white_pawn.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            whiteQueen = ImageIO.read(getClass().getResource("/white_queen.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
            whiteRook = ImageIO.read(getClass().getResource("/white_rook.png"))
                    .getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException("Could not load piece images", e);
        }

        // Create squares array and initialize board
        squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();

        // Update display
        updateStatus();
    }

    private void initializeBoard() {
        // Create the checkerboard pattern
        boolean isLight = true;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JPanel square = new JPanel();
                square.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                square.setBackground(isLight ? Color.WHITE : Color.GRAY);
                square.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                // Add mouse listener to handle moves
                final int r = row;
                final int c = col;
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(r, c);
                    }
                });

                squares[row][col] = square;
                add(square);
                isLight = !isLight;
            }
            isLight = !isLight;
        }
        updateBoard();
    }

    private void handleSquareClick(int row, int col) {
        Piece clickedPiece = gameBoard.getPiece(new int[] { row, col });

        // If no piece is selected and clicked on a piece of current player's color
        if (selectedPiece == null && clickedPiece != null &&
                clickedPiece.getColor() == gameBoard.getToMove()) {
            selectedPiece = clickedPiece;
            highlightSquare(row, col);
            highlightLegalMoves(clickedPiece);
        }
        // If a piece is selected
        else if (selectedPiece != null) {
            // Try to make the move
            try {
                gameBoard.movePiece(selectedPiece, new int[] { row, col });
                updateBoard();
                updateStatus();
            } catch (IllegalArgumentException ex) {
                // Invalid move, deselect piece
                System.out.println("Invalid move: " + ex.getMessage());
            }

            // Clear selection and highlights
            selectedPiece = null;
            clearHighlights();
        }
    }

    private void highlightSquare(int row, int col) {
        squares[row][col].setBackground(Color.YELLOW);
    }

    private void highlightLegalMoves(Piece piece) {
        for (int[] move : piece.getLegalMoves()) {
            squares[move[0]][move[1]].setBackground(Color.GREEN);
        }
    }

    private void clearHighlights() {
        boolean isLight = true;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col].setBackground(isLight ? Color.WHITE : Color.GRAY);
                isLight = !isLight;
            }
            isLight = !isLight;
        }
    }

    private void updateBoard() {
        // Clear all squares
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col].removeAll();

                // Add piece label if there is a piece
                Piece piece = gameBoard.getPiece(new int[] { row, col });
                if (piece != null) {
                    JLabel pieceLabel = new JLabel(getPieceSymbol(piece));
                    pieceLabel.setHorizontalAlignment(JLabel.CENTER);
                    pieceLabel.setFont(new Font("Serif", Font.BOLD, 40));
                    squares[row][col].add(pieceLabel);
                }
            }
        }

        // Refresh the display
        revalidate();
        repaint();
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(0xf0, 0xf1, 0xf0)); // White squares #f0f1f0
                } else {
                    g.setColor(new Color(0x84, 0x77, 0xba)); // Black squares #8477ba
                }
                g.fillRect(j * SQUARE_SIZE, (BOARD_SIZE - 1 - i) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
        
        // Draw pieces
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Piece piece = gameBoard.getPiece(new int[]{j, i});
                if (piece != null) {
                    Image img = getPieceImage(piece);
                    g.drawImage(img, j * SQUARE_SIZE, (BOARD_SIZE - 1 - i) * SQUARE_SIZE, this);
                }
            }
        }
        
        // ... rest of painting code (selected piece highlights, etc)
    }
    
    private Image getPieceImage(Piece piece) {
        return switch (piece.getType()) {
            case PAWN -> piece.getColor() == Piece.Color.WHITE ? whitePawn : blackPawn;
            case KNIGHT -> piece.getColor() == Piece.Color.WHITE ? whiteKnight : blackKnight;
            case BISHOP -> piece.getColor() == Piece.Color.WHITE ? whiteBishop : blackBishop;
            case ROOK -> piece.getColor() == Piece.Color.WHITE ? whiteRook : blackRook;
            case QUEEN -> piece.getColor() == Piece.Color.WHITE ? whiteQueen : blackQueen;
            case KING -> piece.getColor() == Piece.Color.WHITE ? whiteKing : blackKing;
        };
    }
}
package org.cis1200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cis1200.pieces.Bishop;
import org.cis1200.pieces.King;
import org.cis1200.pieces.Knight;
import org.cis1200.pieces.Pawn;
import org.cis1200.pieces.Queen;
import org.cis1200.pieces.Rook;
import org.cis1200.util.Piece;

public class Board {
    private final Piece[][] board;

    private static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Piece.Color toMove;

    private int halfMoveClock;

    private int fullMoveNumber;

    // Castling rights
    private boolean blackKingsideCastle;
    private boolean blackQueensideCastle;
    private boolean whiteKingsideCastle;
    private boolean whiteQueensideCastle;

    // Lists of pieces
    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;

    // En passant target coord
    private int[] enPassantTarget;

    // Lists of moves for display purposes
    private final List<String> moveHistory;

    // Lists of positions for tracking repetition
    private final List<String> positionHistory;

    /**
     * Creates a new empty board.
     */
    public Board() {
        this.board = new Piece[8][8];
        this.toMove = Piece.Color.WHITE;
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
        this.whiteKingsideCastle = true;
        this.whiteQueensideCastle = true;
        this.blackKingsideCastle = true;
        this.blackQueensideCastle = true;
        this.enPassantTarget = null;
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.moveHistory = new ArrayList<>();
        this.positionHistory = new ArrayList<>();
    }

    /**
     * Creates a new board with a custom board state.
     */
    public Board(
            Piece.Color toMove, int halfMoveClock, int fullMoveNumber, boolean blackKingsideCastle,
            boolean blackQueensideCastle, boolean whiteKingsideCastle, boolean whiteQueensideCastle,
            int[] enPassantTarget
    ) {
        this.board = new Piece[8][8];
        this.toMove = toMove;
        this.halfMoveClock = halfMoveClock;
        this.fullMoveNumber = fullMoveNumber;
        this.blackKingsideCastle = blackKingsideCastle;
        this.blackQueensideCastle = blackQueensideCastle;
        this.whiteKingsideCastle = whiteKingsideCastle;
        this.whiteQueensideCastle = whiteQueensideCastle;
        this.enPassantTarget = enPassantTarget;

        // Initialize piece arrays
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.moveHistory = new ArrayList<>();
        this.positionHistory = new ArrayList<>();
    }

    /**
     * Adds a piece to the board at a given position.
     * 
     * @param piece    The piece to add.
     * @param position The position to add the piece to.
     */
    public void addPiece(Piece piece, int[] position) {
        this.board[position[0]][position[1]] = piece;
        if (piece.getColor() == Piece.Color.WHITE) {
            this.whitePieces.add(piece);
        } else {
            this.blackPieces.add(piece);
        }
    }

    /**
     * Removes a piece from the board at a given position.
     * 
     * @param position The position to remove the piece from.
     */
    public void removePiece(int[] position) {
        this.board[position[0]][position[1]] = null;
    }

    /**
     * Gets the piece at a given position.
     * 
     * @param position The position to get the piece from.
     * @return The piece at the given position.
     */
    public Piece getPiece(int[] position) {
        return this.board[position[0]][position[1]];
    }

    /**
     * Moves a piece to a new position without validation.
     * Used internally for move validation and checking for check.
     * 
     * @return a copy of the board with the move made
     */
    public Board tryMove(int[] oldPos, int[] newPos) {
        Piece piece = this.board[oldPos[0]][oldPos[1]];
        board[oldPos[0]][oldPos[1]] = null;
        board[newPos[0]][newPos[1]] = piece;
        piece.setPosition(newPos);
        return this;
    }

    /**
     * Given a piece and a list of moves, returns a list of moves that don't leave
     * the king in check.
     * 
     * @param piece the piece to check
     * @param moves the list of moves to check
     * @return a list of valid moves
     */
    public static List<int[]> filterChecklessMoves(Piece piece, List<int[]> moves) {
        List<int[]> validMoves = new ArrayList<>();
        for (int[] move : moves) {
            Board copyBoard = piece.getBoard().copy();
            copyBoard.tryMove(piece.getPosition(), move);
            if (!copyBoard.isInCheck(piece.getColor())) {
                validMoves.add(Arrays.copyOf(move, 2));
            }
        }
        return validMoves;
    }

    /**
     * Moves a piece to a new position with validation.
     * 
     * @param piece  the piece to move
     * @param newPos the position to move to
     * @throws IllegalArgumentException if the move is not valid
     */
    public void movePiece(Piece piece, int[] newPos) {
        if (!isValidMove(piece, newPos)) {
            throw new IllegalArgumentException("Invalid move");
        }

        int[] oldPos = piece.getPosition();

        // Handle castling
        if (piece.getType() == Piece.Type.KING && Math.abs(newPos[1] - oldPos[1]) == 2) {
            // Kingside castle
            if (newPos[1] > oldPos[1]) {
                int rank = oldPos[0];
                Piece rook = getPiece(new int[] { rank, 7 });
                board[rank][7] = null;
                board[rank][5] = rook;
                rook.setPosition(new int[] { rank, 5 });
            }
            // Queenside castle
            else {
                int rank = oldPos[0];
                Piece rook = getPiece(new int[] { rank, 0 });
                board[rank][0] = null;
                board[rank][3] = rook;
                rook.setPosition(new int[] { rank, 3 });
            }
        }

        // Handle en passant capture
        if (piece.getType() == Piece.Type.PAWN &&
                newPos[1] != oldPos[1] && // diagonal move
                getPiece(newPos) == null) { // no piece at target square
            // Remove captured pawn
            int capturedPawnRank = oldPos[0];
            Piece capturedPawn = getPiece(new int[] { capturedPawnRank, newPos[1] });
            if (capturedPawn != null) {
                board[capturedPawnRank][newPos[1]] = null;
                capturedPawn.setActive(false);
                if (capturedPawn.getColor() == Piece.Color.WHITE) {
                    whitePieces.remove(capturedPawn);
                } else {
                    blackPieces.remove(capturedPawn);
                }
            }
        }

        // Handle regular capture
        Piece capturedPiece = getPiece(newPos);
        if (capturedPiece != null) {
            capturedPiece.setActive(false);
            if (capturedPiece.getColor() == Piece.Color.WHITE) {
                whitePieces.remove(capturedPiece);
            } else {
                blackPieces.remove(capturedPiece);
            }
        }

        // Move piece
        board[oldPos[0]][oldPos[1]] = null;
        board[newPos[0]][newPos[1]] = piece;
        piece.setPosition(newPos);

        // Handle pawn promotion
        if (piece.getType() == Piece.Type.PAWN) {
            // White pawn reaches 8th rank or black pawn reaches 1st rank
            if ((piece.getColor() == Piece.Color.WHITE && newPos[1] == 7) ||
                (piece.getColor() == Piece.Color.BLACK && newPos[1] == 0)) {
                
                // Create new queen
                Queen queen = new Queen(piece.getColor(), newPos, this);
                
                // Deactivate pawn first
                piece.setActive(false);
                
                // Remove pawn from piece list and add queen
                if (piece.getColor() == Piece.Color.WHITE) {
                    whitePieces.remove(piece);
                    whitePieces.add(queen);
                } else {
                    blackPieces.remove(piece);
                    blackPieces.add(queen);
                }
                
                // Replace pawn with queen on board
                board[newPos[0]][newPos[1]] = queen;
            }
        }

        // Update game state (castling rights, en passant target, move counters, etc.)
        updateGameState(piece, oldPos, newPos);
    }

    /**
     * Checks if a move is valid for a piece.
     * 
     * @param piece  the piece to check
     * @param newPos the position to check
     * @return true if the move is valid
     */
    public boolean isValidMove(Piece piece, int[] newPos) {
        List<int[]> validMoves = piece.getLegalMoves();
        for (int[] move : validMoves) {
            if (move[0] == newPos[0] && move[1] == newPos[1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the castling rights.
     * 
     * @return an array of booleans representing the castling rights in the order
     *         white kingside, white queenside, black kingside, black queenside
     */
    public boolean[] getCastlingRights() {
        return new boolean[] { whiteKingsideCastle, whiteQueensideCastle, blackKingsideCastle,
            blackQueensideCastle };
    }

    public void setWhiteKingsideCastle(boolean whiteKingsideCastle) {
        this.whiteKingsideCastle = whiteKingsideCastle;
    }

    public void setWhiteQueensideCastle(boolean whiteQueensideCastle) {
        this.whiteQueensideCastle = whiteQueensideCastle;
    }

    public void setBlackKingsideCastle(boolean blackKingsideCastle) {
        this.blackKingsideCastle = blackKingsideCastle;
    }

    public void setBlackQueensideCastle(boolean blackQueensideCastle) {
        this.blackQueensideCastle = blackQueensideCastle;
    }

    /*----------------------------------
    --- FEN related stuff ---------------  
    ----------------------------------*/

    /**
     * Checks if a given FEN string is valid.
     * 
     * @param fen The FEN string to check.
     * @return True if the FEN string is valid, false otherwise.
     */
    public static boolean isValidFEN(String fen) {
        // check that the FEN has 7 "/" characters and 5 " " characters (this is to
        // check that the FEN is formatted correctly)
        if (!fen.replaceAll("[^/ ]", "").equals("///////     ")) {
            System.out.println("FEN has incorrect number of \"/\" and \" \" characters");
            return false;
        }

        // Split the FEN string into its components
        String[] parts = fen.split("[ ]");
        if (parts.length != 6) {
            System.out.println(
                    "FEN has incorrect number of components/is not complete (" + parts.length
                            + " instead of 6)"
            );
            return false;
        }

        // SECTION 1: Check that each row has 8 pieces, and that the pieces are valid

        // check that the kings exist
        if (!parts[0].matches("^[^k]*k[^k]*$") || !parts[0].matches("^[^K]*K[^K]*$")) {
            System.out.println("FEN is has too many/few kings (should be 1 of each color)");
            return false;
        }

        // check that each row has 8 pieces and that the pieces are valid
        String[] rows = parts[0].split("/");
        for (int i = 0; i < 8; i++) {
            String currentRow = rows[i];
            int count = 0;
            for (char c : currentRow.toCharArray()) {
                if (Character.isDigit(c)) {
                    count += Character.getNumericValue(c);
                } else {
                    if (!"pnbrqk".contains(String.valueOf(c).toLowerCase())) {
                        System.out.println("FEN has invalid piece");
                        return false;
                    }
                    count++;
                }
            }
            if (count != 8) {
                System.out.println(
                        "FEN has incorrect number of pieces in a row (row " + i + " has " + count
                                + " instead of 8)"
                );
                return false;
            }
        }

        // SECTION 2: Check that the color to move is valid
        String colorStatement = parts[1];

        if (!colorStatement.equals("w") && !colorStatement.equals("b")) {
            System.out.println("FEN has invalid color to move (" + colorStatement + ")");
            return false;
        }

        // SECTION 3: Check that the castling availability is valid
        String castleStatement = parts[2];

        // general character check
        if (!castleStatement.equals("-") &&
                !castleStatement.equals("K") &&
                !castleStatement.equals("Q") &&
                !castleStatement.equals("k") &&
                !castleStatement.equals("q") &&
                !castleStatement.equals("KQ") &&
                !castleStatement.equals("Kk") &&
                !castleStatement.equals("Qk") &&
                !castleStatement.equals("Kq") &&
                !castleStatement.equals("kq") &&
                !castleStatement.equals("Qq") &&
                !castleStatement.equals("KQk") &&
                !castleStatement.equals("KQq") &&
                !castleStatement.equals("Kkq") &&
                !castleStatement.equals("Qkq") &&
                !castleStatement.equals("KQkq")) {
            System.out.println("FEN has invalid castling availability (" + castleStatement + ")");
            return false;
        }

        // individual character checks

        // check that BLACK's QUEEN side castle is valid
        if (castleStatement.contains("q")) {
            String homeBaseString = rows[0];

            // check that the king and rook exist
            if (!homeBaseString.contains("k") || !homeBaseString.contains("r")) {
                System.out.println(
                        "FEN has a castling contradiction for black queen side castle (missing king or rook on back rank)"
                );
                return false;
            }

            // isolate relevant characters
            char[] homeBase = homeBaseString.split("k")[0].toCharArray();

            // check that the rook is in the correct position
            if (homeBase[0] != 'r') {
                System.out.println(
                        "FEN has a castling contradiction for black queen side castle (rook is not in the correct position)"
                );
                return false;
            }
            int homeBaseSize = 0;
            for (char c : homeBase) {
                if (Character.isDigit(c)) {
                    homeBaseSize += Character.getNumericValue(c);
                } else {
                    homeBaseSize++;
                }
            }
            // check that the king is in the correct position
            if (homeBaseSize != 4) {
                System.out.println(
                        "FEN has a castling contradiction for black queen side castle (king is not in the correct position)"
                );
                return false;
            }
        }

        // check that BLACK's KING side castle is valid
        if (castleStatement.contains("k")) {
            String homeBaseString = rows[0];

            // check that the king and rook exist
            if (!homeBaseString.contains("k") || !homeBaseString.contains("r")) {
                System.out.println(
                        "FEN has a castling contradiction for black king side castle (missing king or rook on back rank)"
                );
                return false;
            }

            // isolate relevant characters (reversed since the black king side rook is on
            // the right)
            char[] homeBase = new StringBuilder(homeBaseString).reverse().toString().split("k")[0]
                    .toCharArray();

            // check that the rook is in the correct position
            if (homeBase[0] != 'r') {
                System.out.println(
                        "FEN has a castling contradiction for black king side castle (rook is not in the correct position)"
                );
                return false;
            }
            int homeBaseSize = 0;
            for (char c : homeBase) {
                if (Character.isDigit(c)) {
                    homeBaseSize += Character.getNumericValue(c);
                } else {
                    homeBaseSize++;
                }
            }
            // check that the king is in the correct position
            if (homeBaseSize != 3) {
                System.out.println(
                        "FEN has a castling contradiction for black king side castle (king is not in the correct position)"
                );
                return false;
            }
        }

        // check that WHITE's QUEEN side castle is valid
        if (castleStatement.contains("Q")) {
            String homeBaseString = rows[7];

            // check that the king and rook exist
            if (!homeBaseString.contains("K") || !homeBaseString.contains("R")) {
                System.out.println(
                        "FEN has a castling contradiction for white queen side castle (missing king or rook on back rank)"
                );
                return false;
            }

            // isolate relevant characters
            char[] homeBase = homeBaseString.split("K")[0].toCharArray();

            // check that the rook is in the correct position
            if (homeBase[0] != 'R') {
                System.out.println(
                        "FEN has a castling contradiction for white queen side castle (rook is not in the correct position)"
                );
                return false;
            }
            int homeBaseSize = 0;
            for (char c : homeBase) {
                if (Character.isDigit(c)) {
                    homeBaseSize += Character.getNumericValue(c);
                } else {
                    homeBaseSize++;
                }
            }
            // check that the king is in the correct position
            if (homeBaseSize != 4) {
                System.out.println(
                        "FEN has a castling contradiction for white queen side castle (king is not in the correct position)"
                );
                return false;
            }
        }

        // check that WHITE's KING side castle is valid
        if (castleStatement.contains("K")) {
            String homeBaseString = rows[7];

            // check that the king and rook exist
            if (!homeBaseString.contains("K") || !homeBaseString.contains("R")) {
                System.out.println(
                        "FEN has a castling contradiction for white king side castle (missing king or rook on back rank)"
                );
                return false;
            }

            // isolate relevant characters (reversed since the white king side rook is on
            // the right)
            char[] homeBase = new StringBuilder(homeBaseString).reverse().toString().split("K")[0]
                    .toCharArray();

            // check that the rook is in the correct position
            if (homeBase[0] != 'R') {
                System.out.println(
                        "FEN has a castling contradiction for white king side castle (rook is not in the correct position)"
                );
                return false;
            }
            int homeBaseSize = 0;
            for (char c : homeBase) {
                if (Character.isDigit(c)) {
                    homeBaseSize += Character.getNumericValue(c);
                } else {
                    homeBaseSize++;
                }
            }
            // check that the king is in the correct position
            if (homeBaseSize != 3) {
                System.out.println(
                        "FEN has a castling contradiction for white king side castle (king is not in the correct position)"
                );
                return false;
            }
        }

        // SECTION 4: Check that the en passant target square is valid
        String enPassantTarget = parts[3];

        if (!enPassantTarget.equals("-")) {
            // First validate the format: must be a file (a-h) followed by rank (3 or 6)
            if (!enPassantTarget.matches("^[a-h][36]$")) {
                System.out.println(
                        "FEN has invalid en passant target square format (must be a-h followed by 3 or 6, recieved "
                                + enPassantTarget + ")"
                );
                return false;
            }

            // Convert algebraic notation to array indices
            int file = enPassantTarget.charAt(0) - 'a';
            int rank = Character.getNumericValue(enPassantTarget.charAt(1));

            // Check if there's a pawn of the correct color in the right position
            int pawnRank = rank + (rank == 5 ? -1 : 1); // For f3 (rank=5), check rank 4; for f6
                                                        // (rank=2), check rank 3

            String pawnRow = rows[pawnRank];

            // Debug print to see what's happening
            System.out.println("Checking for pawn at rank " + pawnRank + " file " + file);
            System.out.println("Row contents: " + pawnRow);

            // Find the piece at the target file position
            int currentFile = 0;
            char pieceAtFile = '-';
            for (char c : pawnRow.toCharArray()) {
                if (currentFile == file) {
                    if (Character.isDigit(c)) {
                        pieceAtFile = '-';
                    } else {
                        pieceAtFile = c;
                    }
                    break;
                }
                if (Character.isDigit(c)) {
                    currentFile += Character.getNumericValue(c);
                } else {
                    currentFile++;
                }
                if (currentFile > file) {
                    pieceAtFile = '-';
                    break;
                }
            }

            // Check if there's a pawn of the correct color
            char expectedPawn = rank == 2 ? 'p' : 'P'; // Black pawn for 3rd rank, white pawn for
                                                       // 6th
            if (pieceAtFile != expectedPawn) {
                System.out.println(
                        "FEN has invalid en passant target square (no pawn in correct position)"
                );
                System.out.println("Expected " + expectedPawn + " but found " + pieceAtFile);
                return false;
            }
        }

        // SECTION 5: Check that the halfmove clock is valid
        String halfMoveClockStatement = parts[4];

        if (!halfMoveClockStatement.matches("^[0-9]+$")) {
            System.out.println("FEN has invalid halfmove clock (" + halfMoveClockStatement + ")");
            return false;
        }

        int halfMoveClock = Integer.parseInt(halfMoveClockStatement);

        // If there's an en passant target, halfmove clock must be 0
        if (!parts[3].equals("-") && halfMoveClock != 0) {
            System.out.println(
                    "FEN has invalid halfmove clock (must be 0 when en passant is available)"
            );
            return false;
        }

        // SECTION 6: Check that the fullmove number is valid
        String fullMoveNumberStatement = parts[5];

        if (!fullMoveNumberStatement.matches("^[0-9]+$")) {
            System.out.println("FEN has invalid fullmove number (" + fullMoveNumberStatement + ")");
            return false;
        }

        int fullMoveNumber = Integer.parseInt(fullMoveNumberStatement);

        // Fullmove number must be at least 1 (games start at move 1)
        if (fullMoveNumber < 1) {
            System.out.println(
                    "FEN has invalid fullmove number (must be at least 1, recieved "
                            + fullMoveNumber + ")"
            );
            return false;
        }

        // Check upper bound of halfmove clock based on fullmove number
        int maxHalfMoveClock;
        if (parts[1].equals("w")) {
            // Before White's move: max = 2*(F-1)
            maxHalfMoveClock = 2 * (fullMoveNumber - 1);
        } else {
            // After White's move: max = 2*(F-1) + 1
            maxHalfMoveClock = 2 * (fullMoveNumber - 1) + 1;
        }

        if (halfMoveClock > maxHalfMoveClock) {
            System.out.println(
                    "FEN has invalid halfmove clock (exceeds maximum possible value for given fullmove number)"
            );
            return false;
        }

        return true;
    }

    /**
     * Identifies the type and color of a piece given a character.
     * 
     * @param fen The FEN string to convert to a board.
     * @return A board with the pieces in the FEN string.
     * @throws IllegalArgumentException if the FEN string is invalid.
     */
    public static Board FENtoBoard(String fen) {

        if (!isValidFEN(fen)) {
            throw new IllegalArgumentException("Invalid FEN string");
        }

        String[] parts = fen.split(" ");
        String boardString = parts[0];
        String colorToMoveString = parts[1];
        String castlingRightsString = parts[2];
        String enPassantTargetString = parts[3];
        String halfMoveClockString = parts[4];
        String fullMoveNumberString = parts[5];

        Piece.Color colorToMove = colorToMoveString.equals("w") ? Piece.Color.WHITE
                : Piece.Color.BLACK;
        boolean blackKingsideCastle = castlingRightsString.contains("k");
        boolean blackQueensideCastle = castlingRightsString.contains("q");
        boolean whiteKingsideCastle = castlingRightsString.contains("K");
        boolean whiteQueensideCastle = castlingRightsString.contains("Q");
        int[] enPassantTarget = enPassantTargetString.equals("-") ? null
                : new int[] { enPassantTargetString.charAt(0) - 'a',
                    8 - Character.getNumericValue(enPassantTargetString.charAt(1)) };
        int halfMoveClock = Integer.parseInt(halfMoveClockString);
        int fullMoveNumber = Integer.parseInt(fullMoveNumberString);

        Board board = new Board(
                colorToMove, halfMoveClock, fullMoveNumber, blackKingsideCastle,
                blackQueensideCastle,
                whiteKingsideCastle, whiteQueensideCastle, enPassantTarget
        );

        // Parse the board string
        String[] rows = boardString.split("/");
        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            int file = 0;
            for (char c : row.toCharArray()) {
                if (Character.isDigit(c)) {
                    file += Character.getNumericValue(c);
                } else {
                    Piece.Color color = Character.isUpperCase(c) ? Piece.Color.WHITE
                            : Piece.Color.BLACK;
                    String cLower = String.valueOf(c).toLowerCase();
                    switch (cLower) {
                        case "p" -> board.addPiece(
                                new Pawn(color, new int[] { file, 7 - i }, board),
                                new int[] { file, 7 - i }
                        );
                        case "n" -> board.addPiece(
                                new Knight(color, new int[] { file, 7 - i }, board),
                                new int[] { file, 7 - i }
                        );
                        case "b" -> board.addPiece(
                                new Bishop(color, new int[] { file, 7 - i }, board),
                                new int[] { file, 7 - i }
                        );
                        case "r" -> board.addPiece(
                                new Rook(color, new int[] { file, 7 - i }, board),
                                new int[] { file, 7 - i }
                        );
                        case "q" -> board.addPiece(
                                new Queen(color, new int[] { file, 7 - i }, board),
                                new int[] { file, 7 - i }
                        );
                        case "k" -> board.addPiece(
                                new King(color, new int[] { file, 7 - i }, board),
                                new int[] { file, 7 - i }
                        );
                    }
                    file++;
                }
            }
        }

        return board;

    }

    /**
     * Checks if the king of the given color is in check.
     * 
     * @param color the color of the king to check
     * @return true if the king is in check
     */
    public boolean isInCheck(Piece.Color color) {
        // Find the king's position
        int[] kingPos = null;
        for (Piece piece : color == Piece.Color.WHITE ? whitePieces : blackPieces) {
            if (piece.getType() == Piece.Type.KING) {
                kingPos = piece.getPosition();
                break;
            }
        }

        // Check if any opponent piece can capture the king
        if (color == Piece.Color.WHITE) {
            for (Piece piece : blackPieces) {
                if (piece != null && piece.isActive() && piece.getColor() != color) {
                    List<int[]> moves = piece.getSimpleMoves();
                    for (int[] move : moves) {
                        if (move[0] == kingPos[0] && move[1] == kingPos[1]) {
                            return true;
                        }
                    }
                }
            }
        } else {
            for (Piece piece : whitePieces) {
                if (piece != null && piece.isActive() && piece.getColor() != color) {
                    List<int[]> moves = piece.getSimpleMoves();
                    for (int[] move : moves) {
                        if (move[0] == kingPos[0] && move[1] == kingPos[1]) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets the current en passant target square
     * 
     * @return the target square coordinates, or null if none exists
     */
    public int[] getEnPassantTarget() {
        return enPassantTarget != null ? Arrays.copyOf(enPassantTarget, 2) : null;
    }

    /**
     * Creates a deep copy of the board, including all pieces and game state.
     * 
     * @return A new Board instance with the same state
     */
    public Board copy() {
        Board newBoard = new Board(
                this.toMove, this.halfMoveClock, this.fullMoveNumber, this.blackKingsideCastle,
                this.blackQueensideCastle, this.whiteKingsideCastle, this.whiteQueensideCastle,
                this.enPassantTarget
        );

        // Copy pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.board[i][j];
                if (piece != null) {
                    // Create new piece of same type and color
                    Piece newPiece = switch (piece.getType()) {
                        case PAWN -> new Pawn(piece.getColor(), new int[] { i, j }, newBoard);
                        case KNIGHT -> new Knight(piece.getColor(), new int[] { i, j }, newBoard);
                        case BISHOP -> new Bishop(piece.getColor(), new int[] { i, j }, newBoard);
                        case ROOK -> new Rook(piece.getColor(), new int[] { i, j }, newBoard);
                        case QUEEN -> new Queen(piece.getColor(), new int[] { i, j }, newBoard);
                        case KING -> new King(piece.getColor(), new int[] { i, j }, newBoard);
                    };
                    newBoard.addPiece(newPiece, new int[] { i, j });
                }
            }
        }
        return newBoard;
    }

    /**
     * Gets the possible moves for all pieces of the given color.
     * 
     * @param color the color of the pieces to get moves for
     * @return a map of pieces to their possible moves
     */
    public Map<Piece, List<int[]>> getLegalMoves(Piece.Color color) {
        Map<Piece, List<int[]>> moves = new HashMap<>();
        List<Piece> pieces = color == Piece.Color.WHITE ? whitePieces : blackPieces;
        for (Piece piece : pieces) {
            if (piece.isActive()) {
                List<int[]> currentMoves = piece.getLegalMoves();
                if (!currentMoves.isEmpty()) {
                    moves.put(piece, currentMoves);
                }
            }
        }
        return moves;
    }

    /**
     * Checks if the given color is in checkmate.
     * 
     * @param color the color to check
     * @return true if the color is in checkmate
     */
    public boolean isCheckmate(Piece.Color color) {
        return isInCheck(color) && getLegalMoves(color).isEmpty();
    }

    /**
     * Checks if the given color is in stalemate.
     * 
     * @param color the color to check
     * @return true if the color is in stalemate
     */
    public boolean isStalemate(Piece.Color color) {
        return !isInCheck(color) && getLegalMoves(color).isEmpty();
    }

    public boolean is50MoveRule() {
        return halfMoveClock >= 100;
    }

    /**
     * Gets the color of the player to move
     */
    public Piece.Color getToMove() {
        return toMove;
    }

    /**
     * Gets the half move clock
     */
    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    /**
     * Gets the full move number
     */
    public int getFullMoveNumber() {
        return fullMoveNumber;
    }

    /**
     * Updates game state after a move
     * 
     * @param piece  The piece that was moved
     * @param oldPos The position the piece moved from
     * @param newPos The position the piece moved to
     */
    public void updateGameState(Piece piece, int[] oldPos, int[] newPos) {
        // Determine if move is a capture
        boolean isCapture = getPiece(newPos) != null ||
                (piece.getType() == Piece.Type.PAWN && oldPos[1] != newPos[1]);

        // Make the move
        Piece capturedPiece = getPiece(newPos);
        if (capturedPiece != null) {
            capturedPiece.setActive(false);
        }

        // Store old state
        Piece.Color oppositeColor = (piece.getColor() == Piece.Color.WHITE) ? Piece.Color.BLACK
                : Piece.Color.WHITE;

        // Make temporary move to check for check/checkmate
        Board tempBoard = this.copy();
        tempBoard.tryMove(oldPos, newPos);
        boolean causesCheck = tempBoard.isInCheck(oppositeColor);
        boolean causesCheckmate = tempBoard.isCheckmate(oppositeColor);

        // Record the move
        recordMove(piece, oldPos, newPos, isCapture, causesCheck, causesCheckmate);

        // Update move counters
        if (piece.getType() == Piece.Type.PAWN || isCapture) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        if (toMove == Piece.Color.BLACK) {
            fullMoveNumber++;
        }

        // Update castling rights
        if (piece.getType() == Piece.Type.KING) {
            if (piece.getColor() == Piece.Color.WHITE) {
                whiteKingsideCastle = false;
                whiteQueensideCastle = false;
            } else {
                blackKingsideCastle = false;
                blackQueensideCastle = false;
            }
        } else if (piece.getType() == Piece.Type.ROOK) {
            if (oldPos[1] == 0) { // White's back rank
                if (oldPos[0] == 0)
                    whiteQueensideCastle = false;
                if (oldPos[0] == 7)
                    whiteKingsideCastle = false;
            } else if (oldPos[1] == 7) { // Black's back rank
                if (oldPos[0] == 0)
                    blackQueensideCastle = false;
                if (oldPos[0] == 7)
                    blackKingsideCastle = false;
            }
        }

        // Update en passant target
        if (piece.getType() == Piece.Type.PAWN && Math.abs(newPos[1] - oldPos[1]) == 2) {
            enPassantTarget = new int[] { (oldPos[1] + newPos[1]) / 2, oldPos[0] };
        } else {
            enPassantTarget = null;
        }

        // Switch turns
        toMove = (toMove == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;

        // Record the position after the move
        positionHistory.add(getCurrentPosition());
    }

    /**
     * Checks if the game is drawn by insufficient material
     */
    public boolean isInsufficientMaterial() {
        // Count active pieces
        int whitePieceCount = 0;
        for (Piece p : whitePieces) {
            if (p.isActive()) {
                whitePieceCount++;
            }
        }

        int blackPieceCount = 0;
        for (Piece p : blackPieces) {
            if (p.isActive()) {
                blackPieceCount++;
            }
        }

        // King vs King
        if (whitePieceCount == 1 && blackPieceCount == 1) {
            return true;
        }

        // King and Bishop/Knight vs King
        if ((whitePieceCount == 2 && blackPieceCount == 1) ||
                (whitePieceCount == 1 && blackPieceCount == 2)) {
            for (Piece p : whitePieces) {
                if (p.isActive()
                        && (p.getType() == Piece.Type.BISHOP || p.getType() == Piece.Type.KNIGHT)) {
                    return true;
                }
            }
            for (Piece p : blackPieces) {
                if (p.isActive()
                        && (p.getType() == Piece.Type.BISHOP || p.getType() == Piece.Type.KNIGHT)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the game is drawn by threefold repetition
     * Note: You'll need to track position history to implement this
     */
    public boolean isThreefoldRepetition() {
        if (positionHistory.size() < 5) { // Need at least 5 moves for 3 repetitions
            return false;
        }

        String currentPos = positionHistory.get(positionHistory.size() - 1);
        int repetitions = 0;

        // Count occurrences of current position in history
        for (String pos : positionHistory) {
            if (pos.equals(currentPos)) {
                repetitions++;
                if (repetitions >= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the game is over
     */
    public boolean isGameOver() {
        return isCheckmate(toMove) || isStalemate(toMove) ||
                is50MoveRule() || isInsufficientMaterial() ||
                isThreefoldRepetition();
    }

    /**
     * Converts a position array to algebraic notation (e.g. [0,0] -> "a1")
     */
    private String positionToAlgebraic(int[] pos) {
        char file = (char) ('a' + pos[0]); // First index is file (a-h)
        int rank = pos[1] + 1; // Second index is rank (1-8)
        return "" + file + rank;
    }

    /**
     * Adds a move to the move history in algebraic notation
     * 
     * @param piece       The piece that moved
     * @param oldPos      Starting position
     * @param newPos      Ending position
     * @param isCapture   Whether the move was a capture
     * @param isCheck     Whether the move puts opponent in check
     * @param isCheckmate Whether the move is checkmate
     */
    private void recordMove(
            Piece piece, int[] oldPos, int[] newPos, boolean isCapture,
            boolean isCheck, boolean isCheckmate
    ) {
        StringBuilder moveNotation = new StringBuilder();

        // Add piece letter (except for pawns)
        if (piece.getType() != Piece.Type.PAWN) {
            moveNotation.append(switch (piece.getType()) {
                case KING -> "K";
                case QUEEN -> "Q";
                case ROOK -> "R";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                default -> "";
            });
        }

        // Handle castling
        if (piece.getType() == Piece.Type.KING && Math.abs(newPos[1] - oldPos[1]) == 2) {
            moveNotation = new StringBuilder(newPos[1] > oldPos[1] ? "O-O" : "O-O-O");
        } else {
            // Add capture notation
            if (isCapture) {
                if (piece.getType() == Piece.Type.PAWN) {
                    moveNotation.append(positionToAlgebraic(oldPos).charAt(0));
                }
                moveNotation.append("x");
            }

            // Add destination square
            moveNotation.append(positionToAlgebraic(newPos));
        }

        // Add check/checkmate notation
        if (isCheckmate) {
            moveNotation.append("#");
        } else if (isCheck) {
            moveNotation.append("+");
        }

        moveHistory.add(moveNotation.toString());
    }

    /**
     * Gets the move history in algebraic notation
     * 
     * @return List of moves in algebraic notation
     */
    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Converts the current board state to FEN notation
     */
    public String boardToFEN() {
        StringBuilder fen = new StringBuilder();

        // Add piece positions
        for (int i = 0; i < 8; i++) { // Start from rank 8 (index 0) to rank 1 (index 7)
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    char pieceChar = switch (piece.getType()) {
                        case PAWN -> 'p';
                        case KNIGHT -> 'n';
                        case BISHOP -> 'b';
                        case ROOK -> 'r';
                        case QUEEN -> 'q';
                        case KING -> 'k';
                    };
                    fen.append(
                            piece.getColor() == Piece.Color.WHITE ? Character.toUpperCase(pieceChar)
                                    : pieceChar
                    );
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i < 7)
                fen.append('/');
        }

        // Add color to move
        fen.append(' ').append(toMove == Piece.Color.WHITE ? 'w' : 'b');

        // Add castling rights
        fen.append(' ');
        if (whiteKingsideCastle) {
            fen.append('K');
        }
        if (whiteQueensideCastle) {
            fen.append('Q');
        }
        if (blackKingsideCastle) {
            fen.append('k');
        }
        if (blackQueensideCastle) {
            fen.append('q');
        }
        if (!whiteKingsideCastle && !whiteQueensideCastle &&
                !blackKingsideCastle && !blackQueensideCastle) {
            fen.append('-');
        }

        // Add en passant target
        fen.append(' ');
        if (enPassantTarget != null) {
            fen.append(positionToAlgebraic(enPassantTarget));
        } else {
            fen.append('-');
        }

        // Add move counters
        fen.append(' ').append(halfMoveClock);
        fen.append(' ').append(fullMoveNumber);

        return fen.toString();
    }

    /**
     * Gets the current position in a format suitable for repetition checking
     * (excludes move counters which don't affect position identity)
     */
    private String getCurrentPosition() {
        String fen = boardToFEN();
        // Return everything before the move counters (last two fields)
        return fen.substring(0, fen.lastIndexOf(' ', fen.lastIndexOf(' ') - 1));
    }

    public static Board starterBoard() {
        Board board = FENtoBoard(STARTING_POSITION);
        return board;
    }
}

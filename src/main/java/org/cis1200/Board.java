package org.cis1200;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.cis1200.util.Piece;
import org.cis1200.util.Position;
import org.cis1200.pieces.*;
import java.util.ArrayList;
public class Board {
    private Piece[][] board;

    private static final String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Piece.Color toMove;

    private int halfMoveClock;

    private int fullMoveNumber;

    // Castling rights
    private boolean blackKingsideCastle;
    private boolean blackQueensideCastle;
    private boolean whiteKingsideCastle;
    private boolean whiteQueensideCastle;

    private Piece[] whitePieces;
    private Piece[] blackPieces;

    // En passant target coord
    private int[] enPassantTarget;

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
        this.whitePieces = new Piece[16];
        this.blackPieces = new Piece[16];
    }


    /**
     * Creates a new board with a custom board state.
     */
    public Board(Piece[][] board, Piece.Color toMove, int halfMoveClock, int fullMoveNumber, boolean blackKingsideCastle, boolean blackQueensideCastle, boolean whiteKingsideCastle, boolean whiteQueensideCastle, int[] enPassantTarget) {
        this.board = board;
        this.toMove = toMove;
        this.halfMoveClock = halfMoveClock;
        this.fullMoveNumber = fullMoveNumber;
        this.blackKingsideCastle = blackKingsideCastle;
        this.blackQueensideCastle = blackQueensideCastle;
        this.whiteKingsideCastle = whiteKingsideCastle;
        this.whiteQueensideCastle = whiteQueensideCastle;
        this.enPassantTarget = enPassantTarget;
        
        // Initialize piece arrays
        this.whitePieces = new Piece[16];
        this.blackPieces = new Piece[16];
        
        // Populate piece arrays
        int whiteIndex = 0;
        int blackIndex = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    if (piece.getColor() == Piece.Color.WHITE) {
                        whitePieces[whiteIndex++] = piece;
                    } else {
                        blackPieces[blackIndex++] = piece;
                    }
                }
            }
        }
    }

    /**
     * Adds a piece to the board at a given position.
     * @param piece The piece to add.
     * @param position The position to add the piece to.
     */
    public void addPiece(Piece piece, int[] position) {
        this.board[position[0]][position[1]] = piece;
    }

    /**
     * Adds a piece to the board at a given position.
     * @param piece The piece to add.
     * @param x The x coordinate to add the piece to.
     * @param y The y coordinate to add the piece to.
     */
    public void addPiece(Piece piece, int x, int y) {
        this.board[x][y] = piece;
    }

    /**
     * Removes a piece from the board at a given position.
     * @param position The position to remove the piece from.
     */
    public void removePiece(int[] position) {
        this.board[position[0]][position[1]] = null;
    }

    /**
     * Removes a piece from the board at a given position.
     * @param x The x coordinate to remove the piece from.
     * @param y The y coordinate to remove the piece from.
     */
    public void removePiece(int x, int y) {
        this.board[x][y] = null;
    }

    /**
     * Gets the piece at a given position.
     * @param position The position to get the piece from.
     * @return The piece at the given position.
     */
    public Piece getPiece(int[] position) {
        return this.board[position[0]][position[1]];
    }

    /**
     * Moves a piece to a new position without validation.
     * Used internally for move validation and checking for check.
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
     * Given a piece and a list of moves, returns a list of moves that don't leave the king in check.
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
     * @param piece the piece to move
     * @param newPos the position to move to
     * @throws IllegalArgumentException if the move is not valid
     */
    public void movePiece(Piece piece, int[] newPos) {
        if (!isValidMove(piece, newPos)) {
            throw new IllegalArgumentException("Invalid move");
        }

        // Handle capture
        Piece atNewPos = getPiece(newPos);
        if (atNewPos != null) {
            atNewPos.setActive(false);
        }

        // Update board
        int[] oldPos = piece.getPosition();
        board[oldPos[0]][oldPos[1]] = null;
        board[newPos[0]][newPos[1]] = piece;
        piece.setPosition(newPos);
    }

    /**
     * Checks if a move is valid for a piece.
     * @param piece the piece to check
     * @param newPos the position to check
     * @return true if the move is valid
     */
    public boolean isValidMove(Piece piece, int[] newPos) {
        List<int[]> validMoves = piece.getPossibleMoves();
        for (int[] move : validMoves) {
            if (move[0] == newPos[0] && move[1] == newPos[1]) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the castling rights.
     * @return an array of booleans representing the castling rights in the order white kingside, white queenside, black kingside, black queenside
     */
    public boolean[] getCastlingRights() {
        return new boolean[] { whiteKingsideCastle, whiteQueensideCastle, blackKingsideCastle, blackQueensideCastle };
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
     * @param fen The FEN string to check.
     * @return True if the FEN string is valid, false otherwise.
     */
    public static boolean isValidFEN(String fen) {
        //check that the FEN has 7 "/" characters and 5 " " characters (this is to check that the FEN is formatted correctly)
        if (!fen.replaceAll("[^/ ]", "").equals("///////     ")) {
            System.out.println("FEN has incorrect number of \"/\" and \" \" characters");
            return false;
        }

        // Split the FEN string into its components
        String[] parts = fen.split("[ ]");
        if (parts.length != 6) {
            System.out.println(
                    "FEN has incorrect number of components/is not complete (" + parts.length + " instead of 6)");
            return false;
        }

        //SECTION 1: Check that each row has 8 pieces, and that the pieces are valid

        //check that the kings exist
        if (!parts[0].matches("^[^k]*k[^k]*$") || !parts[0].matches("^[^K]*K[^K]*$")) {
            System.out.println("FEN is has too many/few kings (should be 1 of each color)");
            return false;
        }

        //check that each row has 8 pieces and that the pieces are valid
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
                if (count != 8) {
                    System.out.println("FEN has incorrect number of pieces in a row (" + count + " instead of 8)");
                    return false;
                }
            }
        }

        //SECTION 2: Check that the color to move is valid
        String colorStatement = parts[1];

        if (!colorStatement.equals("w") && !colorStatement.equals("b")) {
            System.out.println("FEN has invalid color to move (" + colorStatement + ")");
            return false;
        }

        //SECTION 3: Check that the castling availability is valid
        String castleStatement = parts[2];

        //general character check
        if (!castleStatement.equals("-") && !castleStatement.matches("^[KQkq]{1,4}$")) {
            System.out.println("FEN has invalid castling availability (" + castleStatement + ")");
            return false;
        }

        // individual character checks

        //check that BLACK's QUEEN side castle is valid
        if (castleStatement.contains("q")) {
            String homeBaseString = rows[0];

            //check that the king and rook exist
            if (!homeBaseString.contains("k") || !homeBaseString.contains("r")) {
                System.out.println(
                        "FEN has a castling contradiction for black queen side castle (missing king or rook on back rank)");
                return false;
            }

            //isolate relevant characters
            char[] homeBase = homeBaseString.split("k")[0].toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'r') {
                System.out.println(
                        "FEN has a castling contradiction for black queen side castle (rook is not in the correct position)");
                return false;
            }

            //check that the king is in the correct position
            int kingPosition = 0;

            for (int i = 0; i < homeBase.length; i++) {
                char current = homeBase[i];
                if (Character.isDigit(current)) {
                    kingPosition += Character.getNumericValue(current);
                } else {
                    kingPosition++;
                }
            }

            if (kingPosition != 5) {
                System.out.println(
                        "FEN has a castling contradiction for black queen side castle (king is not in the correct position)");
                return false;
            }
        }

        //check that BLACK's KING side castle is valid
        if (castleStatement.contains("k")) {
            String homeBaseString = rows[0];

            //check that the king and rook exist
            if (!homeBaseString.contains("k") || !homeBaseString.contains("r")) {
                System.out.println(
                        "FEN has a castling contradiction for black king side castle (missing king or rook on back rank)");
                return false;
            }

            //isolate relevant characters (reversed since the black king side rook is on the right)
            char[] homeBase = new StringBuilder(homeBaseString.split("k")[0]).reverse().toString().toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'r') {
                System.out.println(
                        "FEN has a castling contradiction for black king side castle (rook is not in the correct position)");
                return false;
            }

            //check that the king is in the correct position
            int kingPosition = 0;

            for (int i = 0; i < homeBase.length; i++) {
                char current = homeBase[i];
                if (Character.isDigit(current)) {
                    kingPosition += Character.getNumericValue(current);
                } else {
                    kingPosition++;
                }
            }

            if (kingPosition != 4) {
                System.out.println(
                        "FEN has a castling contradiction for black king side castle (king is not in the correct position)");
                return false;
            }
        }

        //check that WHITE's QUEEN side castle is valid
        if (castleStatement.contains("Q")) {
            String homeBaseString = rows[7];

            //check that the king and rook exist
            if (!homeBaseString.contains("K") || !homeBaseString.contains("R")) {
                System.out.println(
                        "FEN has a castling contradiction for white queen side castle (missing king or rook on back rank)");
                return false;
            }

            //isolate relevant characters
            char[] homeBase = homeBaseString.split("K")[0].toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'R') {
                System.out.println(
                        "FEN has a castling contradiction for white queen side castle (rook is not in the correct position)");
                return false;
            }

            //check that the king is in the correct position
            int kingPosition = 0;

            for (int i = 0; i < homeBase.length; i++) {
                char current = homeBase[i];
                if (Character.isDigit(current)) {
                    kingPosition += Character.getNumericValue(current);
                } else {
                    kingPosition++;
                }
            }

            if (kingPosition != 5) {
                System.out.println(
                        "FEN has a castling contradiction for white queen side castle (king is not in the correct position)");
                return false;
            }
        }

        //check that WHITE's KING side castle is valid
        if (castleStatement.contains("K")) {
            String homeBaseString = rows[7];

            //check that the king and rook exist
            if (!homeBaseString.contains("K") || !homeBaseString.contains("R")) {
                System.out.println(
                        "FEN has a castling contradiction for white king side castle (missing king or rook on back rank)");
                return false;
            }

            //isolate relevant characters (reversed since the white king side rook is on the right)
            char[] homeBase = new StringBuilder(homeBaseString.split("K")[0]).reverse().toString().toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'R') {
                System.out.println(
                        "FEN has a castling contradiction for white king side castle (rook is not in the correct position)");
                return false;
            }

            //check that the king is in the correct position
            int kingPosition = 0;

            for (int i = 0; i < homeBase.length; i++) {
                char current = homeBase[i];
                if (Character.isDigit(current)) {
                    kingPosition += Character.getNumericValue(current);
                } else {
                    kingPosition++;
                }
            }

            if (kingPosition != 4) {
                System.out.println(
                        "FEN has a castling contradiction for white king side castle (king is not in the correct position)");
                return false;
            }
        }

        //SECTION 4: Check that the en passant target square is valid
        String enPassantTarget = parts[3];

        if (!enPassantTarget.equals("-")) {
            // First validate the format: must be a file (a-h) followed by rank (3 or 6)
            if (!enPassantTarget.matches("^[a-h][36]$")) {
                System.out.println(
                        "FEN has invalid en passant target square format (must be a-h followed by 3 or 6, recieved "
                                + enPassantTarget + ")");
                return false;
            }

            // Convert algebraic notation to array indices
            int file = enPassantTarget.charAt(0) - 'a';
            int rank = 8 - Character.getNumericValue(enPassantTarget.charAt(1));

            // Check if it's on the correct rank (3rd rank for white pawn, 6th rank for black pawn)
            if (rank != 2 && rank != 5) {
                System.out.println("FEN has invalid en passant target square (must be on 3rd or 6th rank)");
                return false;
            }

            // Check if it's the correct player's turn
            String colorToMove = parts[1];
            if ((rank == 2 && colorToMove.equals("w")) || (rank == 5 && colorToMove.equals("b"))) {
                System.out.println("FEN has invalid en passant target (wrong player to move)");
                return false;
            }

            // Check if there's a pawn of the correct color one step closer to the center
            int pawnRank = rank + (rank == 2 ? -1 : 1); // One rank closer to center
            String pawnRow = rows[pawnRank];

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
            char expectedPawn = rank == 2 ? 'P' : 'p'; // White pawn for 3rd rank, black pawn for 6th
            if (pieceAtFile != expectedPawn) {
                System.out.println("FEN has invalid en passant target square (no pawn in correct position)");
                return false;
            }

        }

        //SECTION 5: Check that the halfmove clock is valid
        String halfMoveClockStatement = parts[4];

        if (!halfMoveClockStatement.matches("^[0-9]+$")) {
            System.out.println("FEN has invalid halfmove clock (" + halfMoveClockStatement + ")");
            return false;
        }

        int halfMoveClock = Integer.parseInt(halfMoveClockStatement);

        // If there's an en passant target, halfmove clock must be 0
        if (!parts[3].equals("-") && halfMoveClock != 0) {
            System.out.println("FEN has invalid halfmove clock (must be 0 when en passant is available)");
            return false;
        }

        //SECTION 6: Check that the fullmove number is valid
        String fullMoveNumberStatement = parts[5];

        if (!fullMoveNumberStatement.matches("^[0-9]+$")) {
            System.out.println("FEN has invalid fullmove number (" + fullMoveNumberStatement + ")");
            return false;
        }

        int fullMoveNumber = Integer.parseInt(fullMoveNumberStatement);

        // Fullmove number must be at least 1 (games start at move 1)
        if (fullMoveNumber < 1) {
            System.out.println("FEN has invalid fullmove number (must be at least 1, recieved " + fullMoveNumber + ")");
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
            System.out.println("FEN has invalid halfmove clock (exceeds maximum possible value for given fullmove number)");
            return false;
        }

        return true;
    }

    /**
     * Identifies the type and color of a piece given a character.
     * @param fen The FEN string to convert to a board.
     * @return A board with the pieces in the FEN string.
     * @throws IllegalArgumentException if the FEN string is invalid.
     */
    public static Board FENtoBoard(String fen) {
        
        if (!isValidFEN(fen)) {
            throw new IllegalArgumentException("Invalid FEN string");
        }
        
        Map<Character, Piece.Type> pieceMap = Map.of(
            'p', Piece.Type.PAWN,
            'n', Piece.Type.KNIGHT,
            'b', Piece.Type.BISHOP,
            'r', Piece.Type.ROOK,
            'q', Piece.Type.QUEEN,
            'k', Piece.Type.KING);
       
        Piece.Type type = pieceMap.get(c);

        Piece.Color color = Character.isUpperCase(c) ? Piece.Color.WHITE : Piece.Color.BLACK;

    }

    /**
     * Checks if the king of the given color is in check.
     * @param color the color of the king to check
     * @return true if the king is in check
     */
    public boolean isInCheck(Piece.Color color) {
        // Find the king's position
        int[] kingPos = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && 
                    piece.getType() == Piece.Type.KING && 
                    piece.getColor() == color) {
                    kingPos = new int[] {i, j};
                    break;
                }
            }
            if (kingPos != null) break;
        }

        // Check if any opponent piece can capture the king
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getColor() != color) {
                    List<int[]> moves = piece.getPossibleMoves();
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
     * @return the target square coordinates, or null if none exists
     */
    public int[] getEnPassantTarget() {
        return enPassantTarget != null ? Arrays.copyOf(enPassantTarget, 2) : null;
    }
    /**
     * Creates a deep copy of the board, including all pieces and game state.
     * @return A new Board instance with the same state
     */
    public Board copy() {
        Piece[][] newBoard = new Piece[8][8];
        // Copy pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = this.board[i][j];
                if (piece != null) {
                    // Create new piece of same type and color
                    Piece newPiece = null;
                    switch (piece.getType()) {
                        case PAWN:
                            newPiece = new Pawn(piece.getColor(), i, j, newBoard);
                            break;
                        case KNIGHT:
                            newPiece = new Knight(piece.getColor(), i, j, newBoard);
                            break;
                        case BISHOP:
                            newPiece = new Bishop(piece.getColor(), i, j, newBoard);
                            break;
                        case ROOK:
                            newPiece = new Rook(piece.getColor(), i, j, newBoard);
                            break;
                        case QUEEN:
                            newPiece = new Queen(piece.getColor(), i, j, newBoard);
                            break;
                        case KING:
                            newPiece = new King(piece.getColor(), i, j, newBoard);
                            break;
                    }
                    newBoard[i][j] = newPiece;
                }
            }
        }
        Board copyBoard = new Board(
            newBoard,
            this.toMove,
            this.halfMoveClock,
            this.fullMoveNumber,
            this.blackKingsideCastle,
            this.blackQueensideCastle,
            this.whiteKingsideCastle,
            this.whiteQueensideCastle,
            this.enPassantTarget != null ? Arrays.copyOf(this.enPassantTarget, 2) : null
        );
        
        return copyBoard;
    }
}

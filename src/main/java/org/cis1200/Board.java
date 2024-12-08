import util.Piece;
import util.Position;

public class Board {
    private Piece[][] board;

    private static final String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Piece.Color toMove;

    private int halfMoveClock;

    private int fullMoveNumber;

    /**
     * Creates a new board.
     */
    public Board() {
        this.board = new Piece[8][8];
    }

    /**
     * Adds a piece to the board at a given position.
     * @param piece The piece to add.
     * @param position The position to add the piece to.
     */
    public void addPiece(Piece piece, Position position) {
        this.board[position.getX()][position.getY()] = piece;
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
    public void removePiece(Position position) {
        this.board[position.getX()][position.getY()] = null;
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
    public Piece getPiece(Position position) {
        return this.board[position.getX()][position.getY()];
    }

    /**
     * Checks if a given FEN string is valid.
     * @param fen The FEN string to check.
     * @return True if the FEN string is valid, false otherwise.
     */
    public boolean isValidFEN(String fen) {
        //check that the FEN has 7 "/" characters and 5 " " characters (this is to check that the FEN is formatted correctly)
        if (!fen.replaceAll("[^/ ]", "").equals("///////     ")) {
            System.out.println("FEN has incorrect number of \"/\" and \" \" characters");
            return false;
        }

        // Split the FEN string into its components
        String[] parts = fen.split("[ ]");
        if (parts.length != 6) {
            System.out.println("FEN has incorrect number of components/is not complete (" + parts.length + " instead of 6)");
            return false;
        }

        //SECTION 1: Check that each row has 8 pieces, and that the pieces are valid
        
        //check that the kings exist
        if (!parts[0].matches("^[^k]*k[^k]*$") || !parts[0].matches("^[^K]*K[^K]*$")) {
            System.out.println("FEN is missing kings");
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
                System.out.println("FEN has a castling contradiction for black queen side castle (missing king or rook on back rank)");
                return false;
            }
 
            //isolate relevant characters
            char[] homeBase = homeBaseString.split("k")[0].toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'r') {
                System.out.println("FEN has a castling contradiction for black queen side castle (rook is not in the correct position)");
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
                System.out.println("FEN has a castling contradiction for black queen side castle (king is not in the correct position)");
                return false;
            }
        }

        //check that BLACK's KING side castle is valid
        if (castleStatement.contains("k")) {
            String homeBaseString = rows[0];

            //check that the king and rook exist
            if (!homeBaseString.contains("k") || !homeBaseString.contains("r")) {
                System.out.println("FEN has a castling contradiction for black king side castle (missing king or rook on back rank)");
                return false;
            }

            //isolate relevant characters (reversed since the black king side rook is on the right)
            char[] homeBase = new StringBuilder(homeBaseString.split("k")[0]).reverse().toString().toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'r') {
                System.out.println("FEN has a castling contradiction for black king side castle (rook is not in the correct position)");
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
                System.out.println("FEN has a castling contradiction for black king side castle (king is not in the correct position)");
                return false;
            }
        }

        //check that WHITE's QUEEN side castle is valid
        if (castleStatement.contains("Q")) {
            String homeBaseString = rows[7];

            //check that the king and rook exist
            if (!homeBaseString.contains("K") || !homeBaseString.contains("R")) {
                System.out.println("FEN has a castling contradiction for white queen side castle (missing king or rook on back rank)");
                return false;
            }
 
            //isolate relevant characters
            char[] homeBase = homeBaseString.split("K")[0].toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'R') {
                System.out.println("FEN has a castling contradiction for white queen side castle (rook is not in the correct position)");
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
                System.out.println("FEN has a castling contradiction for white queen side castle (king is not in the correct position)");
                return false;
            }
        }

        //check that WHITE's KING side castle is valid
        if (castleStatement.contains("K")) {
            String homeBaseString = rows[7];

            //check that the king and rook exist
            if (!homeBaseString.contains("K") || !homeBaseString.contains("R")) {
                System.out.println("FEN has a castling contradiction for white king side castle (missing king or rook on back rank)");
                return false;
            }

            //isolate relevant characters (reversed since the white king side rook is on the right)
            char[] homeBase = new StringBuilder(homeBaseString.split("K")[0]).reverse().toString().toCharArray();

            //check that the rook is in the correct position
            if (homeBase[0] != 'R') {
                System.out.println("FEN has a castling contradiction for white king side castle (rook is not in the correct position)");
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
                System.out.println("FEN has a castling contradiction for white king side castle (king is not in the correct position)");
                return false;
            }
        }

        //SECTION 4: Check that the en passant target square is valid
        String enPassantTarget = parts[3];
        
        if (!enPassantTarget.equals("-")) {
            // First validate the format: must be a file (a-h) followed by rank (3 or 6)
            if (!enPassantTarget.matches("^[a-h][36]$")) {
                System.out.println("FEN has invalid en passant target square format (must be a-h followed by 3 or 6, recieved " + enPassantTarget + ")");
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
}

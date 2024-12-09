package org.cis1200.chess;

import java.util.List;
import java.util.Map;

import org.cis1200.Board;
import org.cis1200.pieces.Queen;
import org.cis1200.util.Piece;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BoardTest {
    private static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final String SCHOLAR_MATE_SETUP = "r1bqkbnr/pppppppp/2n5/4P3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";
    private static final String FOOL_MATE_POSITION = "rnb1kbnr/pppppppp/8/8/3q4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 3";
    private static final String KING_PAWN_ENDGAME = "8/8/8/8/8/8/5K2/4k3 w - - 0 1";
    private static final String QUEENS_GAMBIT_DECLINED = "rnbqkbnr/ppp2ppp/8/3pp3/2P5/8/PP1PPPPP/RNBQKBNR w KQkq - 0 4";
    private static final String CASTLING_TEST = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
    private static final String PROMOTION_POSITION = "8/4P3/8/8/8/8/8/4k2K w - - 0 1";
    private static final String MID_GAME_BATTLE = "r1bqkb1r/pppppppp/2n5/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 3 4";
    private static final String EN_PASSANT_EXAMPLE = "4k3/8/8/4P3/5Pp1/8/8/4K3 b - f3 0 10";
    private static final String CHECKMATE_EXAMPLE = "rnb1kbnr/pppp1ppp/8/4p3/4P3/3P4/PPP2PPP/RNBQKBNR b KQkq - 0 5";

    @Nested
    class FENTests {

        @Test
        void testInvalidBoardFormat() {
            // Not enough rows
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            // Too many rows
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            );
        }

        @Test
        void testBadComponents() {
            // Missing castling rights
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w"));
            // Missing en passant
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq"));
            // Missing move counts
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -"));
            // Too many components
            assertFalse(Board.isValidFEN(STARTING_POSITION + " - "));
        }

        @Test
        void testKingCount() {
            // No white king
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQQBNR w KQkq - 0 1")
            );
            // No black king
            assertFalse(
                    Board.isValidFEN("rnbqqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            );
            // Multiple white kings
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKKBNR w KQkq - 0 1")
            );
            // Multiple black kings
            assertFalse(
                    Board.isValidFEN("rnbkkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            );
        }

        @Test
        void testInvalidPieces() {
            // Invalid piece character
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBxR w KQkq - 0 1")
            );
        }

        @Test
        void testInvalidRowLength() {
            // Too many pieces in a row
            assertFalse(
                    Board.isValidFEN("rnbqkbnrr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            );
            // Too few pieces in a row
            assertFalse(
                    Board.isValidFEN("rnbqkbn/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            );
            // Invalid number in row
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/9/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
            );
        }

        @Test
        void testInvalidColorToMove() {
            // Invalid color character
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1")
            );
        }

        @Test
        void testInvalidCastling() {
            // Invalid characters
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w XQkq - 0 1")
            );
            // Duplicate rights
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KKkq - 0 1")
            );
        }

        @Test
        void testCastlingPositionValidity() {
            // White kingside castle rights but no rook
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBN1 w K - - 0 1")
            );
            // White queenside castle rights but no rook
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR w Q - - 0 1")
            );
            // Black kingside castle rights but no rook
            assertFalse(
                    Board.isValidFEN("rnbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w k - - 0 1")
            );
            // Black queenside castle rights but no rook
            assertFalse(
                    Board.isValidFEN("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w q - - 0 1")
            );
            // White kingside castle rights but no king
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPKPPPP/RNBQ1BNR w K - - 0 1")
            );
            // White queenside castle rights but no king
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPKPPPP/RNBQ1BNR w Q - - 0 1")
            );
            // Black kingside castle rights but no king
            assertFalse(
                    Board.isValidFEN("rnbq1bnr/pppkpppp/8/8/8/8/PPPPPPPP/RNBQKBNR w k - - 0 1")
            );
            // Black queenside castle rights but no king
            assertFalse(
                    Board.isValidFEN("rnbq1bnr/pppkpppp/8/8/8/8/PPPPPPPP/RNBQKBNR w q - - 0 1")
            );
            // White kingside castle rights but king moved
            assertFalse(
                    Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w K - - 0 1")
            );
            // White queenside castle rights but king moved
            assertFalse(
                    Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w Q - - 0 1")
            );
            // Black kingside castle rights but king moved
            assertFalse(
                    Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w k - - 0 1")
            );
            // Black queenside castle rights but king moved
            assertFalse(
                    Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w q - - 0 1")
            );
        }

        @Test
        void testInvalidEnPassant() {
            // Invalid format
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq x6 0 1")
            );
            // Invalid square
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e4 0 1")
            );
            // No pawn in correct position
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 1")
            );
            // Wrong player to move
            assertFalse(
                    Board.isValidFEN(
                            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 1"
                    )
            );
        }

        @Test
        void testInvalidHalfmoveClock() {
            // Not a number
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1")
            );
            // Negative number
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1")
            );
            // Non-zero halfmove clock with en passant
            assertFalse(
                    Board.isValidFEN(
                            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 1 1"
                    )
            );
        }

        @Test
        void testInvalidFullmoveNumber() {
            // Not a number
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x")
            );
            // Zero or negative
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0")
            );
            // Too small relative to halfmove clock
            assertFalse(
                    Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 40 1")
            );
        }

        @Test
        void testValidExamplePositions() {
            // Test all the example positions defined at the top of the class
            assertTrue(Board.isValidFEN(STARTING_POSITION));
            assertTrue(Board.isValidFEN(SCHOLAR_MATE_SETUP));
            assertTrue(Board.isValidFEN(FOOL_MATE_POSITION));
            assertTrue(Board.isValidFEN(KING_PAWN_ENDGAME));
            assertTrue(Board.isValidFEN(QUEENS_GAMBIT_DECLINED));
            assertTrue(Board.isValidFEN(CASTLING_TEST));
            assertTrue(Board.isValidFEN(PROMOTION_POSITION));
            assertTrue(Board.isValidFEN(MID_GAME_BATTLE));
            assertTrue(Board.isValidFEN(EN_PASSANT_EXAMPLE));
            assertTrue(Board.isValidFEN(CHECKMATE_EXAMPLE));
        }
    }

    @Nested
    class PieceManipulationTests {
        @Test
        void testAddRemoveGetPiece() {
            Board board = new Board();
            int[] position = new int[] { 3, 3 }; // d4
            Queen queen = new Queen(Piece.Color.WHITE, position, board);

            // Test adding piece
            board.addPiece(queen, position);
            Piece retrievedPiece = board.getPiece(position);
            assertEquals(queen, retrievedPiece, "Piece should be found at added position");

            // Test removing piece
            board.removePiece(position);
            assertNull(board.getPiece(position), "Position should be empty after removal");
        }
    }

    @Nested
    class LegalMovesTests {
        @Test
        void testNormalPosition() {
            Board board = Board.FENtoBoard(
                    "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 0 1"
            );

            // Test bishop moves (should be 5 legal moves)
            Piece bishop = board.getPiece(new int[] { 2, 3 }); // c4 - file c (index 2), rank 4
                                                               // (index 3)
            assertEquals(6, bishop.getLegalMoves().size(), "Bishop should have 6 legal moves");
        }

        @Test
        void testPinnedPiece() {
            // Position with pinned knight
            Board board = Board
                    .FENtoBoard("rnb1kb1r/pppp1ppp/8/4n3/8/3B4/PPPPQPPP/RNB1K2R b KQkq - 0 1");

            // Knight is pinned to king by bishop
            Piece knight = board.getPiece(new int[] { 4, 4 }); // e5
            assertTrue(
                    knight.getLegalMoves().isEmpty(), "Pinned knight should have no legal moves"
            );
        }

        @Test
        void testMustBlockCheck() {
            // Position with king in check where only specific pieces can block
            Board board = Board
                    .FENtoBoard("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 0 1");

            // Only f3 pawn can move to g4 to block check
            Map<Piece, List<int[]>> allMoves = board.getLegalMoves(Piece.Color.WHITE);
            int totalMoves = allMoves.values().stream().mapToInt(List::size).sum();
            assertEquals(0, totalMoves, "Zero legal moves should be available");
            assertEquals(
                    true, board.isCheckmate(Piece.Color.WHITE), "White should be in checkmate"
            );
        }
    }
}

package org.cis1200.chess;

import org.cis1200.Board;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BoardTest {
    private String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private String scholarMateSetup = "r1bqkbnr/pppppppp/2n5/4P3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";
    private String foolMatePosition = "rnb1kbnr/pppppppp/8/8/3q4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 3";
    private String kingPawnEndgame = "8/8/8/8/8/8/5K2/4k3 w - - 0 1";
    private String queensGambitDeclined = "rnbqkbnr/ppp2ppp/8/3pp3/2P5/8/PP1PPPPP/RNBQKBNR w KQkq - 0 4";
    private String castlingTest = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
    private String promotionPosition = "8/4P3/8/8/8/8/8/4k2K w - - 0 1";
    private String midGameBattle = "r1bqkb1r/pppppppp/2n5/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 3 4";
    private String enPassantExample = "8/8/8/3pP3/8/8/8/8 w - d6 0 2";
    private String checkmateExample = "rnb1kbnr/pppp1ppp/8/4p3/4P3/3P4/PPP2PPP/RNBQKBNR b KQkq - 0 5";

    @Nested
    class FENTests {

        @Test
        void testInvalidBoardFormat() {
            // Not enough rows
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            // Too many rows
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
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
            assertFalse(Board.isValidFEN(startingPosition + " - "));
        }

        @Test
        void testKingCount() {
            // No white king
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQQBNR w KQkq - 0 1"));
            // No black king
            assertFalse(Board.isValidFEN("rnbqqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            // Multiple white kings
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKKBNR w KQkq - 0 1"));
            // Multiple black kings
            assertFalse(Board.isValidFEN("rnbkkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        }

        @Test
        void testInvalidPieces() {
            // Invalid piece character
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBxR w KQkq - 0 1"));
        }

        @Test
        void testInvalidRowLength() {
            // Too many pieces in a row
            assertFalse(Board.isValidFEN("rnbqkbnrr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            // Too few pieces in a row
            assertFalse(Board.isValidFEN("rnbqkbn/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
            // Invalid number in row
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/9/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        }

        @Test
        void testInvalidColorToMove() {
            // Invalid color character
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1"));
        }

        @Test
        void testInvalidCastling() {
            // Invalid characters
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w XQkq - 0 1"));
            // Duplicate rights
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KKkq - 0 1"));
        }

        @Test
        void testCastlingPositionValidity() {
            // White kingside castle rights but no rook
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBN1 w K - - 0 1"));
            // White queenside castle rights but no rook
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR w Q - - 0 1"));
            // Black kingside castle rights but no rook
            assertFalse(Board.isValidFEN("rnbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w k - - 0 1"));
            // Black queenside castle rights but no rook
            assertFalse(Board.isValidFEN("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w q - - 0 1"));
            // White kingside castle rights but no king
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPKPPPP/RNBQ1BNR w K - - 0 1"));
            // White queenside castle rights but no king
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPKPPPP/RNBQ1BNR w Q - - 0 1"));
            // Black kingside castle rights but no king
            assertFalse(Board.isValidFEN("rnbq1bnr/pppkpppp/8/8/8/8/PPPPPPPP/RNBQKBNR w k - - 0 1"));
            // Black queenside castle rights but no king
            assertFalse(Board.isValidFEN("rnbq1bnr/pppkpppp/8/8/8/8/PPPPPPPP/RNBQKBNR w q - - 0 1"));
            // White kingside castle rights but king moved
            assertFalse(Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w K - - 0 1"));
            // White queenside castle rights but king moved
            assertFalse(Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w Q - - 0 1"));
            // Black kingside castle rights but king moved
            assertFalse(Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w k - - 0 1"));
            // Black queenside castle rights but king moved
            assertFalse(Board.isValidFEN("rnbqbknr/pppppppp/8/8/8/8/PPPKPPPP/RNBQBKNR w q - - 0 1"));
        }

        @Test
        void testInvalidEnPassant() {
            // Invalid format
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq x6 0 1"));
            // Invalid square
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e4 0 1"));
            // No pawn in correct position
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 1"));
            // Wrong player to move
            assertFalse(Board.isValidFEN("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 1"));
        }

        @Test
        void testInvalidHalfmoveClock() {
            // Not a number
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1"));
            // Negative number
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1"));
            // Non-zero halfmove clock with en passant
            assertFalse(Board.isValidFEN("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 1 1"));
        }

        @Test
        void testInvalidFullmoveNumber() {
            // Not a number
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x"));
            // Zero or negative
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"));
            // Too small relative to halfmove clock
            assertFalse(Board.isValidFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 40 1"));
        }

        @Test
        void testValidExamplePositions() {
            // Test all the example positions defined at the top of the class
            assertTrue(Board.isValidFEN(startingPosition));
            assertTrue(Board.isValidFEN(scholarMateSetup));
            assertTrue(Board.isValidFEN(foolMatePosition));
            assertTrue(Board.isValidFEN(kingPawnEndgame));
            assertTrue(Board.isValidFEN(queensGambitDeclined));
            assertTrue(Board.isValidFEN(castlingTest));
            assertTrue(Board.isValidFEN(promotionPosition));
            assertTrue(Board.isValidFEN(midGameBattle));
            assertTrue(Board.isValidFEN(enPassantExample));
            assertTrue(Board.isValidFEN(checkmateExample));
        }
    }
}

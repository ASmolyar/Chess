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
        
    }
}

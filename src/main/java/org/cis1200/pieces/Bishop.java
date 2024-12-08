package pieces;

import java.util.ArrayList;
import java.util.List;
import util.Piece;
import util.Position;

public class Bishop extends Piece {
    public Bishop(Color color, int x, int y) {
        super(Type.BISHOP, color, x, y);
    }

    public Bishop(Color color, Position position) {
        super(Type.BISHOP, color, position);
    }

    @Override
    public List<Position> getPossibleMoves() {
        List<Position> moves = new ArrayList<>();
        // Bishop moves diagonally in all four directions
        // Implementation will be added later
        return moves;
    }
}

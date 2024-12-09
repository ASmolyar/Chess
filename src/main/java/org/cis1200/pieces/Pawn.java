package org.cis1200.pieces;

import java.util.ArrayList;
import java.util.List;

import org.cis1200.util.Piece;
import org.cis1200.util.Piece.Color;
import org.cis1200.util.Position;

public class Pawn extends Piece {
    public Pawn(Color color, int x, int y) {
        super(Type.PAWN, color, x, y);
    }

    public Pawn(Color color, Position position) {
        super(Type.PAWN, color, position);
    }

    @Override
    public List<Position> getPossibleMoves() {
        int[][] moveDirections;
        
        if (this.getColor() == Color.WHITE) {
            moveDirections = {{1, 0}};
        } else {
            moveDirections = {{-1, 0}};
        }
        
        List<Position> moves = new ArrayList<>();

        return moves;
    }
}

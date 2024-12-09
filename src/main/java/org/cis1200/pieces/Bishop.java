package org.cis1200.pieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cis1200.Board;
import org.cis1200.util.Piece;

public class Bishop extends Piece {

    private static final int[][] moveDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    

    public Bishop(Color color, int[] position, Board board) {
        super(Type.BISHOP, color, position, board);
    }

    @Override
    public List<int[]> getSimpleMoves() {
        List<int[]> range = new ArrayList<>();
        
        //iterate through all possible move directions
        for (int[] direction : moveDirections) {
            //store current position
            int[] currentPos = Arrays.copyOf(this.getPosition(), 2);

            //iterate through all possible moves in the current direction
            while (true) {
                int[] newPos = new int[] {
                    currentPos[0] + direction[0],
                    currentPos[1] + direction[1]
                };

                //check if the new position is out of bounds
                if (newPos[0] < 0 || newPos[0] > 7 || newPos[1] < 0 || newPos[1] > 7) {
                    break;
                }

                //check if there is a piece at the new position
                Piece atNewPos = this.getBoard().getPiece(newPos);
                if (atNewPos != null) {
                    if (atNewPos.getColor() == this.getColor()) {
                        break;
                    } else {
                        range.add(Arrays.copyOf(newPos, 2));
                        break;
                    }
                }
                range.add(Arrays.copyOf(newPos, 2));
                currentPos = newPos;
            }
        }
        
        return range;
    }

    @Override
    public List<int[]> getLegalMoves() {
        List<int[]> moves = getSimpleMoves();
        return Board.filterChecklessMoves(this, moves);
    }
}

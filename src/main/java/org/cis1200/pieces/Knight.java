package org.cis1200.pieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cis1200.Board;
import org.cis1200.util.Piece;

public class Knight extends Piece {
     private static final int[][] moveDirections = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};

    public Knight(Color color, int[] position, Board board) {
        super(Type.KNIGHT, color, position, board);
    }
    
    @Override
    public List<int[]> getPossibleMoves() {
        
        List<int[]> range = new ArrayList<>();

        //iterate through all possible move directions
        for (int[] direction : moveDirections) {
            
            //store current position
            int[] currentPos = Arrays.copyOf(this.getPosition(), 2);

            //calculate new position
            int[] newPos = new int[] {
                currentPos[0] + direction[0],
                currentPos[1] + direction[1]
            };

            //check if the new position is out of bounds
            if (newPos[0] < 0 || newPos[0] > 7 || newPos[1] < 0 || newPos[1] > 7) {
                continue;
            }

            //check if there is a piece at the new position
            Piece atNewPos = this.getBoard().getPiece(newPos);
            if (atNewPos != null) {
                if (atNewPos.getColor() == this.getColor()) {
                    continue;
                } else {
                    range.add(Arrays.copyOf(newPos, 2));
                    continue;
                }
            }
            range.add(Arrays.copyOf(newPos, 2));
        }

        //check which moves don't leave king in check
        range = Board.filterChecklessMoves(this, range);
        return range;
    }
}

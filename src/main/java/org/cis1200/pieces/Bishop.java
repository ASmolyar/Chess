package org.cis1200.pieces;

import org.cis1200.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.cis1200.util.Piece;

public class Bishop extends Piece {
    public Bishop(Color color, int x, int y, Board board) {
        super(Type.BISHOP, color, x, y, board);
    }

    public Bishop(Color color, int[] position, Board board) {
        super(Type.BISHOP, color, position, board);
    }

    private static final int[][] moveDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    

    /**
     * Returns a list of all possible moves for the bishop.
     * @return A list of all possible positions for the bishop to move to.
     */
    @Override
    public List<int[]> getPossibleMoves() {

        //initialize return list
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
                currentPos = newPos;

                //check if the new position is out of bounds
                if (currentPos[0] < 0 || currentPos[0] > 7 || currentPos[1] < 0 || currentPos[1] > 7) {
                    break;
                }

                //check if there is a piece at the current position
                Piece atCurrentPos = this.getBoard().getPiece(currentPos);
                if (atCurrentPos != null) {
                    //check if the piece is the same color
                    if (atCurrentPos.getColor() == this.getColor()) {
                        break;
                    } else {
                        // Capture move - check if it leaves own king in check
                        int[] originalPos = this.getPosition();
                        Piece capturedPiece = this.getBoard().getPiece(currentPos);
                        
                        // Try move
                        this.getBoard().movePiece(this, currentPos);
                        
                        if (!this.getBoard().isInCheck(this.getColor())) {
                            range.add(Arrays.copyOf(currentPos, 2));
                        }
                        
                        // Undo move
                        this.getBoard().movePiece(this, originalPos);
                        this.getBoard().setPiece(capturedPiece, currentPos);
                        break;
                    }
                }
                
                // Regular move - check if it leaves own king in check
                int[] originalPos = this.getPosition();
                
                // Try move
                this.getBoard().movePiece(this, currentPos);
                
                if (!this.getBoard().isInCheck(this.getColor())) {
                    range.add(Arrays.copyOf(currentPos, 2));
                }
                
                // Undo move
                this.getBoard().movePiece(this, originalPos);
            }
        }

        return range;
    }
}

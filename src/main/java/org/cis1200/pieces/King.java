package org.cis1200.pieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cis1200.Board;
import org.cis1200.util.Piece;

public class King extends Piece {

    private static final int[][] moveDirections = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 },
        { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    public King(Color color, int[] position, Board board) {
        super(Type.KING, color, position, board);
    }

    @Override
    public List<int[]> getLegalMoves() {

        List<int[]> range = new ArrayList<>();

        // iterate through all possible move directions
        for (int[] direction : moveDirections) {

            // store current position
            int[] currentPos = Arrays.copyOf(this.getPosition(), 2);

            // calculate new position
            int[] newPos = new int[] {
                currentPos[0] + direction[0],
                currentPos[1] + direction[1]
            };

            // check if the new position is out of bounds
            if (newPos[0] < 0 || newPos[0] > 7 || newPos[1] < 0 || newPos[1] > 7) {
                continue;
            }

            // check if there is a piece at the new position
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

        // check which moves don't leave king in check
        range = Board.filterChecklessMoves(this, range);

        // check if castling is possible
        if (this.getColor() == Color.WHITE) {

            // white kingside
            if (this.getBoard().getCastlingRights()[0] &&
                    this.getBoard().getPiece(new int[] { 5, 0 }) == null &&
                    this.getBoard().getPiece(new int[] { 6, 0 }) == null) {
                int[][] whiteKingsideMoves = {
                    { 5, 0 },
                    { 6, 0 }
                };
                boolean canCastle = true;
                for (int[] move : whiteKingsideMoves) {
                    Board copyBoard = this.getBoard().copy();
                    copyBoard.tryMove(this.getPosition(), move);
                    if (copyBoard.isInCheck(this.getColor())) {
                        canCastle = false;
                    }
                }
                if (canCastle) {
                    range.add(new int[] { 6, 0 });
                }
            }

            // white queenside
            if (this.getBoard().getCastlingRights()[1] &&
                    this.getBoard().getPiece(new int[] { 3, 0 }) == null &&
                    this.getBoard().getPiece(new int[] { 2, 0 }) == null &&
                    this.getBoard().getPiece(new int[] { 1, 0 }) == null) {
                int[][] whiteQueensideMoves = {
                    { 3, 0 },
                    { 2, 0 },
                };
                for (int[] move : whiteQueensideMoves) {
                    Board copyBoard = this.getBoard().copy();
                    copyBoard.tryMove(this.getPosition(), move);
                    if (!copyBoard.isInCheck(this.getColor())) {
                        range.add(new int[] { 2, 0 });
                    }
                }
            }
        } else {

            // black kingside
            if (this.getBoard().getCastlingRights()[2] &&
                    this.getBoard().getPiece(new int[] { 5, 7 }) == null &&
                    this.getBoard().getPiece(new int[] { 6, 7 }) == null) {
                int[][] blackKingsideMoves = {
                    { 5, 7 },
                    { 6, 7 }
                };
                for (int[] move : blackKingsideMoves) {
                    Board copyBoard = this.getBoard().copy();
                    copyBoard.tryMove(this.getPosition(), move);
                    if (!copyBoard.isInCheck(this.getColor())) {
                        range.add(new int[] { 6, 7 });
                    }
                }
            }

            // black queenside
            if (this.getBoard().getCastlingRights()[3] &&
                    this.getBoard().getPiece(new int[] { 3, 7 }) == null &&
                    this.getBoard().getPiece(new int[] { 2, 7 }) == null &&
                    this.getBoard().getPiece(new int[] { 1, 7 }) == null) {
                int[][] blackQueensideMoves = {
                    { 3, 7 },
                    { 2, 7 }
                };
                for (int[] move : blackQueensideMoves) {
                    Board copyBoard = this.getBoard().copy();
                    copyBoard.tryMove(this.getPosition(), move);
                    if (!copyBoard.isInCheck(this.getColor())) {
                        range.add(new int[] { 2, 7 });
                    }
                }
            }
        }

        return range;
    }

    @Override
    public List<int[]> getSimpleMoves() {
        List<int[]> range = new ArrayList<>();

        // iterate through all possible move directions
        for (int[] direction : moveDirections) {
            // store current position
            int[] currentPos = Arrays.copyOf(this.getPosition(), 2);

            // calculate new position
            int[] newPos = new int[] {
                currentPos[0] + direction[0],
                currentPos[1] + direction[1]
            };

            // check if the new position is out of bounds
            if (newPos[0] < 0 || newPos[0] > 7 || newPos[1] < 0 || newPos[1] > 7) {
                continue;
            }

            // check if there is a piece at the new position
            Piece atNewPos = this.getBoard().getPiece(newPos);
            if (atNewPos != null) {
                if (atNewPos.getColor() == this.getColor()) {
                    continue;
                }
            }
            range.add(Arrays.copyOf(newPos, 2));
        }

        return range;
    }
}
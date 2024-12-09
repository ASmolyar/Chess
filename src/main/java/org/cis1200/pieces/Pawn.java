package org.cis1200.pieces;

import java.util.ArrayList;
import java.util.List;

import org.cis1200.Board;
import org.cis1200.util.Piece;

public class Pawn extends Piece {
    public Pawn(Color color, int[] position, Board board) {
        super(Type.PAWN, color, position, board);
    }

    @Override
    public List<int[]> getSimpleMoves() {
        List<int[]> range = new ArrayList<>();
        int[] enPassantTarget = this.getBoard().getEnPassantTarget();

        if (this.getColor() == Color.WHITE) {
            // one space forward
            if (this.getY() != 7) {
                if (this.getBoard().getPiece(new int[] { this.getX(), this.getY() + 1 }) == null) {
                    range.add(new int[] { this.getX(), this.getY() + 1 });
                }
            }

            // two spaces forward if on starting row
            if (this.getY() == 1 && this.getBoard()
                    .getPiece(new int[] { this.getX(), this.getY() + 2 }) == null) {
                range.add(new int[] { this.getX(), this.getY() + 2 });
            }

            // diagonal captures
            int[] northEast = new int[] { this.getX() + 1, this.getY() + 1 };
            if (this.getX() != 7 && this.getY() != 7) {
                Piece northEastPiece = this.getBoard().getPiece(northEast);
                if ((northEastPiece != null && northEastPiece.getColor() != this.getColor()) ||
                        (enPassantTarget != null && enPassantTarget[0] == northEast[0]
                                && enPassantTarget[1] == northEast[1])) {
                    range.add(northEast);
                }
            }
            int[] northWest = new int[] { this.getX() - 1, this.getY() + 1 };
            if (this.getX() != 0 && this.getY() != 7) {
                Piece northWestPiece = this.getBoard().getPiece(northWest);
                if ((northWestPiece != null && northWestPiece.getColor() != this.getColor()) ||
                        (enPassantTarget != null && enPassantTarget[0] == northWest[0]
                                && enPassantTarget[1] == northWest[1])) {
                    range.add(northWest);
                }
            }
        } else {
            // one space forward
            if (this.getY() != 0) {
                if (this.getBoard().getPiece(new int[] { this.getX(), this.getY() - 1 }) == null) {
                    range.add(new int[] { this.getX(), this.getY() - 1 });
                }
            }

            // two spaces forward if on starting row
            if (this.getY() == 6 && this.getBoard()
                    .getPiece(new int[] { this.getX(), this.getY() - 2 }) == null) {
                range.add(new int[] { this.getX(), this.getY() - 2 });
            }

            // diagonal captures
            int[] southEast = new int[] { this.getX() + 1, this.getY() - 1 };
            if (this.getX() != 7 && this.getY() != 0) {
                Piece southEastPiece = this.getBoard().getPiece(southEast);
                if ((southEastPiece != null && southEastPiece.getColor() != this.getColor()) ||
                        (enPassantTarget != null && enPassantTarget[0] == southEast[0]
                                && enPassantTarget[1] == southEast[1])) {
                    range.add(southEast);
                }
            }
            int[] southWest = new int[] { this.getX() - 1, this.getY() - 1 };
            if (this.getX() != 0 && this.getY() != 0) {
                Piece southWestPiece = this.getBoard().getPiece(southWest);
                if ((southWestPiece != null && southWestPiece.getColor() != this.getColor()) ||
                        (enPassantTarget != null && enPassantTarget[0] == southWest[0]
                                && enPassantTarget[1] == southWest[1])) {
                    range.add(southWest);
                }
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

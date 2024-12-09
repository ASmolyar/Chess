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
    public List<int[]> getPossibleMoves() {
        List<int[]> range = new ArrayList<>();
        int[] enPassantTarget = this.getBoard().getEnPassantTarget();

        if (this.getColor() == Color.WHITE) {
            //one space forward
            if (this.getBoard().getPiece(new int[] {this.getX() + 1, this.getY()}) == null) {
                range.add(new int[] {this.getX() + 1, this.getY()});
            }

            //two spaces forward if on starting row
            if (this.getY() == 1 && this.getBoard().getPiece(new int[] { this.getX() + 2, this.getY() }) == null) {
                range.add(new int[] { this.getX() + 2, this.getY() });
            }

            //diagonal captures
            int[] northEast = new int[] { this.getX() + 1, this.getY() + 1 };
            Piece northEastPiece = this.getBoard().getPiece(northEast);
            if ((northEastPiece != null && northEastPiece.getColor() != this.getColor()) ||
                (enPassantTarget != null && enPassantTarget[0] == northEast[0] && enPassantTarget[1] == northEast[1])) {
                range.add(northEast);
            }
            int[] northWest = new int[] { this.getX() + 1, this.getY() - 1 };
            Piece northWestPiece = this.getBoard().getPiece(northWest);
            if ((northWestPiece != null && northWestPiece.getColor() != this.getColor()) ||
                (enPassantTarget != null && enPassantTarget[0] == northWest[0] && enPassantTarget[1] == northWest[1])) {
                range.add(northWest);
            }
        } else {
            //one space forward
            if (this.getBoard().getPiece(new int[] {this.getX() - 1, this.getY()}) == null) {
                range.add(new int[] {this.getX() - 1, this.getY()});
            }

            //two spaces forward if on starting row
            if (this.getY() == 6 && this.getBoard().getPiece(new int[] { this.getX() - 2, this.getY() }) == null) {
                range.add(new int[] { this.getX() - 2, this.getY() });
            }

            //diagonal captures
            int[] southEast = new int[] { this.getX() - 1, this.getY() + 1 };
            Piece southEastPiece = this.getBoard().getPiece(southEast);
            if ((southEastPiece != null && southEastPiece.getColor() != this.getColor()) ||
                (enPassantTarget != null && enPassantTarget[0] == southEast[0] && enPassantTarget[1] == southEast[1])) {
                range.add(southEast);
            }
            int[] southWest = new int[] { this.getX() - 1, this.getY() - 1 };
            Piece southWestPiece = this.getBoard().getPiece(southWest);
            if ((southWestPiece != null && southWestPiece.getColor() != this.getColor()) ||
                (enPassantTarget != null && enPassantTarget[0] == southWest[0] && enPassantTarget[1] == southWest[1])) {
                range.add(southWest);
            }
        }

        //check which moves don't leave king in check
        range = Board.filterChecklessMoves(this, range);
        return range;
    }
}

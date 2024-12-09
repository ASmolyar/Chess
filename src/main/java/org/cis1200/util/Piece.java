package org.cis1200.util;
import java.util.List;
import java.util.Map;

import org.cis1200.Board;
public abstract class Piece {
    public static enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    }

    public static Map<Type, Integer> pieceValues = Map.of(
        Type.PAWN, 1,
        Type.KNIGHT, 3,
        Type.BISHOP, 3,
        Type.ROOK, 5,
        Type.QUEEN, 9,
        Type.KING, 0);

    public static enum Color {
        WHITE, BLACK
    }
    
    private final Type type;  
    private final Color color;
    private boolean active;
    private int x;
    private int y;
    private final Board board;

    public Piece(Type type, Color color, int[] position, Board board) {
        this.type = type;
        this.color = color;
        this.active = true;
        this.x = position[0];
        this.y = position[1];
        this.board = board;
    }

    //getters

    /**
     * @return the type of the piece
     */
    public Type getType() {
        return this.type;
    }

    /**
     * @return the color of the piece
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return whether the piece is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @return the x coordinate of the piece
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return the y coordinate of the piece
     */
    public int getY() {
        return this.y;
    }

    /**
     * @return the position of the piece
     */
    public int[] getPosition() {
        return new int[] {this.x, this.y};
    }
      
    /**
     * @return the possible moves of the piece
     */
    public abstract List<int[]> getPossibleMoves();

    public Board getBoard() {
        return this.board;
    }
    
    //setters

    /**
     * sets the active status of the piece
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * sets the position of the piece
     */
    public void setPosition(int[] position) {
        this.x = position[0];
        this.y = position[1];
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    //checkers

    /**
     * @return whether the move to the new position is valid
     */
    public boolean isValidMove(int newX, int newY) {
        return this.getPossibleMoves().contains(new int[] {newX, newY});
    }
        
}

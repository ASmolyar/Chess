package util;
import java.util.List;
import java.util.Map;

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
    
    private Type type;  
    private Color color;
    private boolean active;
    private int x;
    private int y;
    private int[][] moveDirections;

    public Piece(Type type, Color color, int x, int y) {
        this.type = type;
        this.color = color;
        this.active = true;
        this.x = x;
        this.y = y;
    }

    public Piece(Type type, Color color, Position position) {
        this(type, color, position.getX(), position.getY());
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
    public Position getPosition() {
        return new Position(this.x, this.y);
    }
      
    /**
     * @return the possible moves of the piece
     */
    public abstract List<Position> getPossibleMoves();
    
    //setters

    /**
     * moves the piece to the given position
     */
    public void move(int x, int y) {
        if (this.isValidMove(x, y)) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * sets the active status of the piece
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    //checkers

    /**
     * @return whether the move to the new position is valid
     */
    public boolean isValidMove(int newX, int newY) {
        return this.getPossibleMoves().contains(new Position(newX, newY));
    }
        
}

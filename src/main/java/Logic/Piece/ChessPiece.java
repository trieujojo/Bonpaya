package Logic.Piece;

import java.util.*;

public class ChessPiece {
    private String name;
    private int id;
    private int[] position; // row then col
    private MovePattern movePattern;
    private List<int[]> possibleMoves;
    private List<int[]> blocked;
    private boolean hasMoved;
    private boolean enPassant;

    public ChessPiece(String name, int id) {
        this.name = name;
        this.id = id;
        blocked = new LinkedList<>();
        possibleMoves=new LinkedList<>();
    }

    public ChessPiece(String name, int id, int[] position) {
        this.name = name;
        this.id = id;
        this.position=position;
        blocked = new LinkedList<>();
        possibleMoves=new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int[] getPosition() {
        return position;
    }

    private void setPosition(int[] position) {
        this.position = position;
    }

    public MovePattern getMovePattern() {
        return movePattern;
    }

    public void setMovePattern(MovePattern movePattern) {
        this.movePattern = movePattern;
    }

    public List<int[]> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(List<int[]> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }

    public ChessPiece clone(){
        ChessPiece clone = new ChessPiece(name, id);
        clone.setPosition(new int[]{position[0],position[1]});
        clone.setEnPassant(enPassant);
        clone.setMovePattern(movePattern);
        clone.setHasMoved(hasMoved);
        clone.setPossibleMoves(List.copyOf(possibleMoves));
        return clone;
    }
}



package Logic.Piece;

import Logic.Board.Board;
import Logic.Board.Position;

import java.util.*;

public class ChessPiece {
    private String name;
    private int id;
    private Position position; // row then col
    private MovePattern movePattern;
    private List<Position> possibleMoves;
    private List<Position> blocked;
    private boolean hasMoved;
    private boolean enPassant;
    private String color;

    public ChessPiece(String name, String color, int id) {
        this.name = name;
        this.id = id;
        blocked = new LinkedList<>();
        possibleMoves=new LinkedList<>();
        this.color=color;
    }

    public ChessPiece(String name,String color, int id, Position position) {
        this.name = name;
        this.id = id;
        this.position=position;
        blocked = new LinkedList<>();
        possibleMoves=new LinkedList<>();
        this.color=color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean movePiece(Position position){
        if(!hasMoved){
            if(Math.abs(position.row()-this.position.row())==2 && name.equals("pawn")) enPassant=true;
            else enPassant=false;
        }else enPassant=false;
        hasMoved=true;
        this.position=position;
        return true;
    }

    public MovePattern getMovePattern() {
        return movePattern;
    }

    public void setMovePattern(MovePattern movePattern) {
        this.movePattern = movePattern;
    }

    public List<Position> getPossibleMoves() {
        return possibleMoves;
    }

    public boolean addPossibleMove(Position position, Board board){
        possibleMoves.add(position);
        if(board.getArray().get(position)==null) board.addOnArriv(position,this);
        else board.addOnLeav(position,this);
        return true;
    }

    public void setPossibleMoves(List<Position> possibleMoves) {
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

    public boolean isWhite(){
        return color.equals("white");
    }

    public ChessPiece clone(){
        ChessPiece clone = new ChessPiece(name, color,id);
        clone.setPosition(position.copy());
        clone.setEnPassant(enPassant);
        clone.setMovePattern(movePattern);
        clone.setHasMoved(hasMoved);
        clone.color=color;
        clone.setPossibleMoves(List.copyOf(possibleMoves));
        return clone;
    }

    @Override
    public String toString(){
        StringBuilder sb= new StringBuilder();
        sb.append(name);
        sb.append(" ");
        sb.append(id);
        if(possibleMoves.size()>0) sb.append("\n");
        for(Position p : possibleMoves) sb.append(p);
        return sb.toString();
    }

    @Override
    public boolean equals(Object other){
        if(this==other) return true;
        if(other==null){
            if(this==null) return true;
            return false;
        }
        if(other.getClass() != this.getClass()) return false;
        ChessPiece o = (ChessPiece) other;
        return o.getId() == id;
    }
}



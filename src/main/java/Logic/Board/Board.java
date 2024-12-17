package Logic.Board;

import Logic.Move;
import Logic.Piece.ChessPiece;

import java.util.*;

public class Board {
    private int size;
//    private int[][] board; // contains ID of pieces
    private Map<Position, ChessPiece> board;
    private HashMap<String,List<ChessPiece>> chessPieces;
    private boolean whiteTurn=true;//true first color
    private Map<Position, Set<ChessPiece>> blocked; // blocked pieces
    private Map<Position, Set<ChessPiece>> blocking; // block if landing on this
    private LinkedList<Move> moveLog;
    private LinkedList<ChessPiece> eatenPieces;
    private boolean eatenThisTurn = false;


    public Board(int size) {
        this.size = size;
        board = new HashMap<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board.put(new Position(i,j),null);
            }
        }
        blocked= new HashMap<>();
        blocking = new HashMap<>();
        moveLog = new LinkedList<>();
        eatenPieces=new LinkedList<>();
    }

    public Map<Position, ChessPiece> getArray() {
        return board;
    }

    public HashMap<String, List<ChessPiece>> getChessPieces() {
        return chessPieces;
    }

    public void setChessPieces(HashMap<String, List<ChessPiece>> chessPieces) {
        this.chessPieces = chessPieces;
    }

    public void changeTurn(){
        whiteTurn = !whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public Board clone(){
        Board clone = new Board(size);
        clone.setWhiteTurn(whiteTurn);
//        clone.setChessPieces();
        return clone;
    }

    public int getSize() {
        return size;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    //coordinates where arrival/removal of piece will change possible move of cp
    public boolean addOnLeav(Position pos, ChessPiece cp){
        if(blocked.containsKey(pos)) blocked.get(pos).add(cp);
        else blocked.put(pos,new HashSet<>(Collections.singletonList(cp)));
//        System.out.println(pos+" "+cp);
        return true;
    }

    public boolean removeOnLeav(Position pos){
        if(blocked.containsKey(pos)){
            blocked.remove(pos);
            return true;
        }
        return false;
    }

    public Map<Position, Set<ChessPiece>> getOnLeave() {
        return blocked;
    }

    //coordinates where arrival/removal of piece will change possible move of cp
    public boolean addOnArriv(Position pos, ChessPiece cp){
        if(blocking.containsKey(pos)) return blocking.get(pos).add(cp);
        else blocking.put(pos,new HashSet<>(Collections.singletonList(cp)));
        return true;
    }

    public boolean removeOnArriv(Position pos){
        if(blocking.containsKey(pos)){
            blocking.remove(pos);
            return true;
        }
        return false;
    }

    public Map<Position, Set<ChessPiece>> getOnArriv() {
        return blocking;
    }

    public LinkedList<Move> getMoveLog() {
        return moveLog;
    }

    public void addEaten(ChessPiece cp){
        eatenPieces.add(cp);
        eatenThisTurn=true;
    }

    public ChessPiece getEatenLast(){
//        eatenThisTurn=false;
        return eatenPieces.getLast();
    }

    public boolean isEatenThisTurn() {
        return eatenThisTurn;
    }

    public void setEatenThisTurn(boolean eatenThisTurn) {
        this.eatenThisTurn = eatenThisTurn;
    }
}

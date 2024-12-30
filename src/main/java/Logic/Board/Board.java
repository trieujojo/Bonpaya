package Logic.Board;

import Logic.GameFlow.GameLogic;
import Logic.GameFlow.Move;
import Logic.Piece.ChessPiece;
import Logic.Piece.PieceFactory;

import java.util.*;

public class Board {
    private int size;
    private GameLogic logic;
    private Map<Position, ChessPiece> board;
    private HashMap<String,LinkedList<ChessPiece>> chessPieces;
    private boolean whiteTurn=true;//true first color
    private Map<Position, Set<ChessPiece>> blocked; // blocked pieces
    private Map<Position, Set<ChessPiece>> blocking; // block if landing on this
    private LinkedList<Move> moveLog;
    private LinkedList<ChessPiece> eatenPieces;
    private boolean eatenThisTurn = false;
    private boolean endGame= false;



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

    public void setLogic(GameLogic logic) {
        this.logic = logic;
    }

    public Map<Position, ChessPiece> getArray() {
        return board;
    }

    public HashMap<String, LinkedList<ChessPiece>> getChessPieces() {
        return chessPieces;
    }

    public void setChessPieces(HashMap<String, LinkedList<ChessPiece>> chessPieces) {
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
        clone.setLogic(logic);
        clone.setChessPieces(new HashMap<>());
        for(String key: chessPieces.keySet()) {
            clone.chessPieces.put(key, PieceFactory.cloneSet(chessPieces.get(key)));
            for(ChessPiece chessPiece: clone.chessPieces.get(key)) clone.board.put(chessPiece.getPosition(),chessPiece);
        }
//
        clone.blocked = Move.cloneBlocked(blocked);
        clone.blocking = Move.cloneBlocked(blocking);
        clone.eatenThisTurn = eatenThisTurn;
        clone.eatenPieces = PieceFactory.cloneSet(eatenPieces);
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

    public boolean movePiece(ChessPiece cp, Position pos){return logic.movePiece(cp,pos,true);}

    public boolean uneaten() {
        if(eatenPieces.size()>0) {
            ChessPiece p = eatenPieces.removeLast();
            getChessPieces().get(p.getId()>0?"white":"black").add(p);
        }
        return true;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }
}

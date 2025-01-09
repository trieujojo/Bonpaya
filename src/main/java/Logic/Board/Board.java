package Logic.Board;

import Logic.GameFlow.GameLogic;
import Logic.GameFlow.Move;
import Logic.GameFlow.PossibleMoveChecker;
import Logic.Piece.ChessPiece;
import Logic.Piece.PieceFactory;
import Logic.Player.HumanPlayer;
import Logic.Player.Player;

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
    private boolean fakeBoard;



    public Board(int size, boolean fake) {
        this.size = size;
        board = new HashMap<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board.put(new Position(i,j),null);
            }
        }
        this.fakeBoard = fake;
        blocked= new HashMap<>();
        chessPieces = new HashMap<>();
        if(!fake) {
            chessPieces.put("white", PieceFactory.getPiecesSet(this, true));
            chessPieces.put("black", PieceFactory.getPiecesSet(this, false));
            for (String key : chessPieces.keySet())
                for (ChessPiece p : chessPieces.get(key))
                    board.put(p.getPosition(), p);
        }
        blocking = new HashMap<>();
        moveLog = new LinkedList<>();
        eatenPieces=new LinkedList<>();
        logic=new GameLogic(this);
    }

    public void setLogic(GameLogic logic) {
        this.logic = logic;
    }

    public GameLogic getLogic() {
        return logic;
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
        Board clone = new Board(size, true);
        clone.setWhiteTurn(whiteTurn);

        for(Position p: board.keySet())
            if(board.get(p)!=null) clone.board.put(p,board.get(p).clone());
        clone.chessPieces=new HashMap<>();
        clone.chessPieces.put("white",new LinkedList<>());
        clone.chessPieces.put("black",new LinkedList<>());
        for(ChessPiece cp:clone.board.values()) if(cp!=null){
            if(cp.isWhite()) clone.chessPieces.get("white").add(cp);
            else clone.chessPieces.get("black").add(cp);
        }

//        for(Position p: board.keySet())
//            if(board.get(p)!=null) if(!board.get(p).getPosition().equals(p)) System.out.println("updated position wrong:"+board.get(p));

//        for(String key: chessPieces.keySet())
//            for(ChessPiece chessPiece: clone.chessPieces.get(key)) PossibleMoveChecker.checkPossibleMove(clone,chessPiece);
        clone.logic=new GameLogic(clone);
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

    public boolean isFakeBoard() {
        return fakeBoard;
    }

    public void setFakeBoard(boolean fakeBoard) {
        this.fakeBoard = fakeBoard;
    }

    public void setPlayers(Player p1,Player p2){
        logic.setPlayers(p1,p2);
    }

    public Player getWhitePlayer(){
        return logic.getWhitePlayer();
    }
    public Player getBlackPlayer(){
        return logic.getBlackPlayer();
    }

    public Position getPromotionPos(){
        return logic.getPromotionPos();
    }

    public ChessPiece getPromotedPawn(){
        return logic.getPromotedPawn();
    }

    public ChessPiece promote(String choice, String color){
        return logic.promote(choice,color);
    }

    public boolean isPlayerTurn(){
        if(isWhiteTurn()) return getWhitePlayer() instanceof HumanPlayer;
        else return getBlackPlayer() instanceof HumanPlayer;
    }

    public Board newGame(){
        return logic.newGame();
    }

    public void print(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(board.get(new Position(i,j))== null) sb.append("_");
                else if (board.get(new Position(i,j)).getName().equals("pawn")) sb.append("p");
                else if (board.get(new Position(i,j)).getName().equals("rook")) sb.append("R");
                else if (board.get(new Position(i,j)).getName().equals("bishop")) sb.append("B");
                else if (board.get(new Position(i,j)).getName().equals("knight")) sb.append("k");
                else if (board.get(new Position(i,j)).getName().equals("queen")) sb.append("Q");
                else if (board.get(new Position(i,j)).getName().equals("king")) sb.append("K");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }
}

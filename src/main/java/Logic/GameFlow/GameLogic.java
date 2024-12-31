package Logic.GameFlow;

import Logic.Board.Board;
import Logic.Board.Position;
import Logic.Parameters;
import Logic.Piece.ChessPiece;
import Logic.Piece.PieceFactory;
import Logic.Player.Player;

import java.util.*;

import static Logic.GameFlow.PossibleMoveChecker.checkPossibleMove;

public class GameLogic {
    private Board board;
    private Player player1;
    private Player player2;
    private Position promotionPos;
    private ChessPiece promotedPawn;
    private boolean endGame=false;

    public GameLogic(){
        init();
    }

    private void init(){
        Board board = new Board(Parameters.boardSize());
        HashMap<String, LinkedList<ChessPiece>> piecesSet = new HashMap<>();
        piecesSet.put("white",PieceFactory.getPiecesSet(board,true));
        piecesSet.put("black",PieceFactory.getPiecesSet(board,false));
        PossibleMoveChecker.init(board);
        board.setChessPieces(piecesSet);
        this.board= board;
        for(String key : piecesSet.keySet())
            for(ChessPiece cp:piecesSet.get(key))
                checkPossibleMove(cp);
        board.setLogic(this);
    }

    public void setPlayers(Player p1, Player p2){
        player1=p1;
        player2=p2;
    }

    public Player getWhitePlayer(){
        if(player1.isWhite()) return player1;
        else return player2;
    }
    public Player getBlackPlayer(){
        if(player1.isWhite()) return player2;
        else return player1;
    }

    public boolean movePiece(ChessPiece cp, Position pos){
        System.out.println("fake?"+ board.isFakeBoard());
        board.setEatenThisTurn(false);
        board.getArray().put(cp.getPosition(),null);
        List<ChessPiece> toCheckBack = new LinkedList<>();
        if(board.getArray().get(pos)!=null) {
            board.addEaten(board.getArray().get(pos));
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,board.getArray().get(pos),false,toCheckBack,cp.hasMoved()));
        }else if(cp.getName().equals("king") && Math.abs(pos.col()-cp.getPosition().col())>1){
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,null,false,toCheckBack,cp.hasMoved()));
            ChessPiece rook=board.getArray().get(pos.left());
            if(rook!=null){
                movePiece(rook,pos.right());
            }
            cp.setEnPassant(true);
            board.changeTurn();
        }else if(cp.getName().equals("pawn") && enPassantEat(cp,pos)){
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,board.getEatenLast(),false,toCheckBack,cp.hasMoved()));
        }else{
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,null,false,toCheckBack,cp.hasMoved()));
        }
        if(cp.getName().equals("pawn") && (pos.row()==0 || pos.row()==board.getSize()-1)){
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,board.getEatenLast(),true,toCheckBack,cp.hasMoved()));
            promotePawn(cp,pos);
            board.getChessPieces().get((cp.getColor())).remove(cp);
        }

        board.getArray().put(pos,cp);
        Position oldPos = cp.getPosition();
        cp.movePiece(pos);

        if(board.getOnLeave().containsKey(oldPos)){
            for(ChessPiece piece: board.getOnLeave().get(oldPos)){
                checkPossibleMove(piece);
                toCheckBack.add(piece);
            }
            board.removeOnLeav(oldPos);
        }
        if(board.getOnArriv().containsKey(pos)){
            for(ChessPiece piece: board.getOnArriv().get(pos)){
                checkPossibleMove(piece);
                toCheckBack.add(piece);
            }

            board.removeOnArriv(pos);
        }
        if(board.isEatenThisTurn() && board.getOnLeave().containsKey(pos)) {
            for(ChessPiece piece:board.getOnLeave().get(pos)) {
                checkPossibleMove(piece);
                toCheckBack.add(piece);
            }
            board.removeOnLeav(pos);
        }

        if(board.getMoveLog().getLast().eatenPiece()!=null) {
            board.getChessPieces().get((isWhiteTurn()) ? "black" : "white").remove(board.getMoveLog().getLast().eatenPiece());
            if(board.getMoveLog().getLast().eatenPiece().getName().equals("king")) {
                endGame=true;
                board.setEndGame(true);
            }
        }
        board.changeTurn();
        return true;
    }

    private void promotePawn(ChessPiece cp,Position pos) {
        promotedPawn=cp;
        promotionPos = pos;
        if(board.isFakeBoard()){
            if(isWhiteTurn())
                promote("queen","white");
            else
                promote("queen","black");
        }else{
        if(isWhiteTurn())
            getWhitePlayer().promotePiece();
        else
            getBlackPlayer().promotePiece();
        }
    }

    public ChessPiece promote(String choice, String color){
        ChessPiece newPiece = PieceFactory.getSingle(choice,promotedPawn.getId()*100,promotedPawn.getPosition());
        board.getChessPieces().get(color).add(newPiece);
        board.getArray().put(promotedPawn.getPosition(),newPiece);
        PossibleMoveChecker.checkPossibleMove(newPiece);
        return newPiece;
    }

    public ChessPiece getPromotedPawn(){
        return promotedPawn;
    }
    public boolean movePiece(ChessPiece cp, Position pos,boolean verify){
        if(verify(cp,pos)) {
            return movePiece(cp, pos);
        }
        return false;
    }
    private boolean verify(ChessPiece cp, Position newPos){
        if(cp.getPossibleMoves().contains(newPos)) return true;
        return false;
    }

    private boolean enPassantEat(ChessPiece cp,Position position){
        if(position.col()!=cp.getPosition().col() && getBoard().getArray().get(position)==null){
            ChessPiece expectedPawn = board.getArray().get(new Position(cp.getPosition().row(),position.col()));
            if(expectedPawn != null && expectedPawn.getName().equals("pawn") && expectedPawn.isEnPassant()){
                board.addEaten(expectedPawn);
                return true;
            }
        }return false;
    }

    private void checkNextTo(Position pos, Position next,ChessPiece cp){
        if(next != null && board.getArray().get(next)!= null && board.getArray().get(next).getName().equals("pawn") &&
                board.getArray().get(next).isEnPassant()) {
            board.getArray().get(next)
                    .getPossibleMoves().add(new Position((cp.getPosition().row()+pos.row())/2,pos.col()));
        }
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void changeTurn(){
        board.changeTurn();
    }
    public boolean isWhiteTurn(){
        return board.isWhiteTurn();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Position getPromotionPos() {
        return promotionPos;
    }

    public boolean isEndGame() {
        return endGame;
    }

}

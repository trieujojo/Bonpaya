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
    private boolean promotion = false;
    private boolean echec=false;

    public GameLogic(Board b){
        this.board= b;
        for(Position pos: b.getArray().keySet())
            if(b.getArray().get(pos)!=null)
                PossibleMoveChecker.checkPossibleMove(b,b.getArray().get(pos));
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

    public boolean movePiece(ChessPiece cp, Position dest){
        if(!board.isEndGame()) {
            board.setEatenThisTurn(false);
            promotion = false;
            board.getArray().put(cp.getPosition(), null);
            List<ChessPiece> toCheckBack = new LinkedList<>();
            addMoveToLog(board,cp,dest,toCheckBack);

            Position oldPos = cp.getPosition();//position of piece before the move
            if (promotion) {
                promotePawn(cp, dest);
                cp.setPromoted(true);
                board.getChessPieces().get((cp.getColor())).remove(cp);
            }else{
                board.getArray().put(dest, cp);
                cp.movePiece(dest);
            }
            if (board.getOnLeave().containsKey(oldPos)) {
                for (ChessPiece piece : board.getOnLeave().get(oldPos)) {
                    checkPossibleMove(board, piece);
                    toCheckBack.add(piece);
                }
                board.removeOnLeav(oldPos);
            }
            if (board.getOnArriv().containsKey(dest)) {
                for (ChessPiece piece : board.getOnArriv().get(dest)) {
                    checkPossibleMove(board, piece);
                    toCheckBack.add(piece);
                }
                board.removeOnArriv(dest);
            }

            if (board.isEatenThisTurn() && board.getEatenLast().getPosition()!=dest &&
                    board.getOnLeave().containsKey(board.getEatenLast().getPosition())) {
                for (ChessPiece piece : board.getOnLeave().get(board.getEatenLast().getPosition())) {
                    checkPossibleMove(board, piece);
                    toCheckBack.add(piece);
                }
                board.removeOnLeav(board.getEatenLast().getPosition());
            }

            if (board.getMoveLog().getLast().eatenPiece() != null) {/// comment this
//                board.getChessPieces().get((isWhiteTurn()) ? "black" : "white").remove(board.getMoveLog().getLast().eatenPiece());
                if (board.getMoveLog().getLast().eatenPiece().getName().equals("king")) {
                    board.setEndGame(true);
                }
            }
            if(board.isEatenThisTurn()) board.getEatenLast().setAlive(false);
            PossibleMoveChecker.checkPossibleMove(board,cp);
            board.changeTurn();
            if(!board.isSuperficial()&& !board.isEndGame()){
                if(!verifyCheck()){
                    board.setEndGame(verifyDraw());
                }
            }
            return true;
        }return false;
    }

    private boolean verifyDraw() {
        Board clone = board.cloneBrief();
        List<ChessPiece> pieces =clone.getChessPieces().get(clone.isWhiteTurn()?"white":"black");
        for (int i = 0; i < pieces.size(); i++) {
            ChessPiece currentPiece = pieces.get(i);
            List<Position> moves = Move.clonePositions(currentPiece.getPossibleMoves());
            for (Position p : moves) {
                if (clone.movePiece(currentPiece, p)) {
                    if(!clone.isCheck()) return false;
                }
            }
        }
        return true;
    }

    public boolean verifyCheck() {
        echec=false;
        Position kingP = new Position(-1,-1);
        for(ChessPiece cp: board.getChessPieces().get(isWhiteTurn()?"white":"black"))
            if(cp.getName().equals("king")) kingP=cp.getPosition();
        if(kingP.col()==-1){
            System.out.println("couldn't find the King");
            return false;
        }
        for(ChessPiece cp: board.getChessPieces().get(isWhiteTurn()?"black":"white"))
            if(cp.getPossibleMoves().contains(kingP)) {
                echec=true;
                return true;
            }
        return echec;
    }

    private void addMoveToLog(Board board, ChessPiece cp, Position pos, List<ChessPiece> toCheckBack){
        if (board.getArray().get(pos) != null) {
            board.addEaten(board.getArray().get(pos));
            if (cp.getName().equals("pawn") && (pos.row() == 0 || pos.row() == board.getSize() - 1)) {
                board.getMoveLog().add(new Move(cp.getPosition(), pos, cp, board.getArray().get(pos), true, toCheckBack, cp.hasMoved()));
                promotion = true;
            } else
                board.getMoveLog().add(new Move(cp.getPosition(), pos, cp, board.getArray().get(pos), false, toCheckBack, cp.hasMoved()));
        } else if (cp.getName().equals("king") && Math.abs(pos.col() - cp.getPosition().col()) > 1) {
            board.getMoveLog().add(new Move(cp.getPosition(), pos, cp, null, false, toCheckBack, cp.hasMoved()));
            ChessPiece rook = board.getArray().get(pos.left());//todo
            if (rook != null) {
//                movePiece(rook, pos.right());
                board.getArray().put(pos.left(),null);
                rook.setPosition(pos.right());
                board.getArray().put(rook.getPosition(),rook);
                PossibleMoveChecker.checkPossibleMove(board,rook);
                toCheckBack.add(rook);
            }
            cp.setEnPassant(true);
//            board.changeTurn();
        } else if (cp.getName().equals("pawn") && enPassant(cp, pos)) {
            board.getMoveLog().add(new Move(cp.getPosition(), pos, cp, board.getEatenLast(), false, toCheckBack, cp.hasMoved()));
            board.getArray().put(board.getEatenLast().getPosition(), null);
        } else {
            if (cp.getName().equals("pawn") && (pos.row() == 0 || pos.row() == board.getSize() - 1)) {
                board.getMoveLog().add(new Move(cp.getPosition(), pos, cp, null, true, toCheckBack, cp.hasMoved()));
                promotion = true;
            } else
                board.getMoveLog().add(new Move(cp.getPosition(), pos, cp, null, false, toCheckBack, cp.hasMoved()));
        }
    }

    private void promotePawn(ChessPiece cp,Position pos) {
        promotedPawn=cp;
        promotionPos=pos;
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
        ChessPiece newPiece = PieceFactory.getSingle(choice,
                promotedPawn.getId()+(20*(promotedPawn.getId()>0?1:-1)),promotionPos);
        board.getChessPieces().get(color).add(newPiece);
        board.getArray().put(promotionPos,newPiece);
        PossibleMoveChecker.checkPossibleMove(board,newPiece);
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
        PossibleMoveChecker.checkPossibleMove(board,cp);
        return cp.getPossibleMoves().contains(newPos);
    }

    private boolean enPassant(ChessPiece cp, Position position){
        if(position.col()!=cp.getPosition().col() && getBoard().getArray().get(position)==null){
            ChessPiece expectedPawn = board.getArray().get(new Position(cp.getPosition().row(),position.col()));
            if(expectedPawn != null && expectedPawn.getName().equals("pawn") && expectedPawn.isEnPassant()){
                board.addEaten(expectedPawn);
                return true;
            }
        }return false;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isWhiteTurn(){
        return board.isWhiteTurn();
    }

    public Position getPromotionPos() {
        return promotionPos;
    }

    public Board newGame(){
        Board board = new Board(Parameters.boardSize(),false);
        HashMap<String, LinkedList<ChessPiece>> piecesSet = new HashMap<>();
        piecesSet.put("white",PieceFactory.getPiecesSet(board,true));
        piecesSet.put("black",PieceFactory.getPiecesSet(board,false));
        board.setChessPieces(piecesSet);
        this.board= board;
        for(String key : piecesSet.keySet())
            for(ChessPiece cp:piecesSet.get(key))
                checkPossibleMove(board,cp);
        return board;
    }

}

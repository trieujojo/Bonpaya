package Logic;

import Logic.Board.Board;
import Logic.Board.Position;
import Logic.Piece.ChessPiece;
import Logic.Piece.PieceFactory;

import java.util.*;

public class GameLogic {
    private Board board;

    public GameLogic(){
        init();
    }

    private void init(){
        Board board = new Board(Parameters.boardSize());
        HashMap<String, List<ChessPiece>> piecesSet = new HashMap<>();
        piecesSet.put("white",PieceFactory.getPiecesSet(board,true));
        piecesSet.put("black",PieceFactory.getPiecesSet(board,false));
        board.setChessPieces(piecesSet);
        this.board= board;
        for(String key : piecesSet.keySet()){
            for(ChessPiece cp:piecesSet.get(key)){
                checkPossibleMove(cp);
//                System.out.println(cp + " " + cp.getId());
//                System.out.println(Arrays.toString(cp.getPossibleMoves().toArray()));
            }
        }
        PossibleMoveChecker.init(board);
    }
    //TODO next
    public boolean movePiece(ChessPiece cp, Position pos){
        board.setEatenThisTurn(false);
        board.getArray().put(cp.getPosition(),null);
        List<ChessPiece> toCheckBack = new LinkedList<>();
        if(cp.getName().equals("pawn")&& enPassantEat(cp,pos)){
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,board.getEatenLast(),toCheckBack,cp.hasMoved()));
        }else if(board.getArray().get(pos)!=null) {
            board.addEaten(board.getArray().get(pos));
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,board.getArray().get(pos),toCheckBack,cp.hasMoved()));
        }else if(cp.getName().equals("king") && Math.abs(pos.col()-cp.getPosition().col())>1){
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,null,toCheckBack,cp.hasMoved()));
            ChessPiece rook=board.getArray().get(pos.left());
            if(rook!=null){
                movePiece(rook,pos.right());
            }
            cp.setEnPassant(true);
            System.out.println(cp);
            System.out.println(cp.isEnPassant());
            board.changeTurn();
        }else{
            board.getMoveLog().add(new Move(cp.getPosition(),pos,cp,null,toCheckBack,cp.hasMoved()));
        }



        board.getArray().put(pos,cp);
        Position oldPos = cp.getPosition();
        cp.movePiece(pos);

//        if(cp.getName().equals("pawn")) enPassantLogic(pos,cp); // add the move enPassant when arrive next to it

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
        board.changeTurn();
        return true;
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
            if(expectedPawn != null && expectedPawn.getName().equals("pawn")){
                board.addEaten(expectedPawn);
                return true;
            }
        }return false;
    }
    private void enPassantCheckMove(Position pos, ChessPiece cp) {//todo move this
        Position next= pos.left();
        if(pos.row()< board.getSize() - 3 && pos.row()> 2){
            checkNextTo(pos,next,cp);
            next= pos.right();
            checkNextTo(pos,next,cp);
        }else cp.setEnPassant(false);
//        if()
    }

    private void checkNextTo(Position pos, Position next,ChessPiece cp){
        if(next != null && board.getArray().get(next)!= null && board.getArray().get(next).getName().equals("pawn") &&
                board.getArray().get(next).isEnPassant()) {
            board.getArray().get(next)
                    .getPossibleMoves().add(new Position((cp.getPosition().row()+pos.row())/2,pos.col()));
        }
    }


    public void checkPossibleMove(ChessPiece cp){
        Position pos = cp.getPosition();
        cp.setPossibleMoves(new LinkedList<>());
        switch(cp.getMovePattern()){
            case UP1:
                verifyEmpty(pos.up(),cp);
                if(!cp.hasMoved())verifyEmpty(pos.up().up(),cp);
                verifyPawnAttack(pos.upLeft(),cp);
                verifyPawnAttack(pos.upRight(),cp);
                enPassantCheckMove(pos,cp);
                break;
            case DOWN1:
                verifyEmpty(pos.down(),cp);
                if(!cp.hasMoved())verifyEmpty(pos.down().down(),cp);
                verifyPawnAttack(pos.downRight(),cp);
                verifyPawnAttack(pos.downLeft(),cp);
                enPassantCheckMove(pos,cp);
                break;
            case L:
                Position dest = pos.up();
                if(dest!=null) {
                    verifyEmptyAttack(dest.upRight(),cp);
                    verifyEmptyAttack(dest.upLeft(),cp);
                }
                dest = pos.right();
                if(dest!=null) {
                    verifyEmptyAttack(dest.upRight(),cp);
                    verifyEmptyAttack(dest.downRight(),cp);
                }
                dest = pos.down();
                if(dest!=null) {
                    verifyEmptyAttack(dest.downLeft(),cp);
                    verifyEmptyAttack(dest.downRight(),cp);
                }
                dest = pos.left();
                if(dest!=null) {
                    verifyEmptyAttack(dest.upLeft(),cp);
                    verifyEmptyAttack(dest.downLeft(),cp);
                }
                break;
            case ONE:
                verifyEmptyAttack(pos.right(),cp);
                verifyEmptyAttack(pos.left(),cp);
                verifyEmptyAttack(pos.up(),cp);
                verifyEmptyAttack(pos.down(),cp);
                verifyEmptyAttack(pos.upLeft(),cp);
                verifyEmptyAttack(pos.upRight(),cp);
                verifyEmptyAttack(pos.downLeft(),cp);
                verifyEmptyAttack(pos.downRight(),cp);
                verifyRockMove(cp);
                break;
            case DIAGONAL:
                checkDiagonal(cp,pos);
                break;
            case CROSS:
                checkCross(cp,pos);
                break;
            case QUEEN:
                checkDiagonal(cp,pos);
                checkCross(cp,pos);
                break;
            default:
                System.out.println("default case activated");
        }
    }

    private void verifyRockMove(ChessPiece cp) {
        if(!cp.hasMoved()){
            if(board.getArray().get(cp.getPosition().left().left().left())!=null&&
                    board.getArray().get(cp.getPosition().left().left().left()).getName().equals("rook")){
                if(!board.getArray().get(cp.getPosition().left().left().left()).hasMoved() &&
                        board.getArray().get(cp.getPosition().left())==null &&
                        board.getArray().get(cp.getPosition().left().left())==null){
                    cp.getPossibleMoves().add(cp.getPosition().left().left());
                }else if(board.getArray().get(cp.getPosition().left().left())!=null)
                    board.addOnLeav(cp.getPosition().left().left(),cp);
            }
        }
    }

    private void checkDiagonal(ChessPiece cp, Position pos){
        Position dest = pos.upLeft();
        while(verifyEmpty(dest,cp)) dest = dest.upLeft();
        verifyAttack(dest,cp);
        dest= pos.upRight();
        while(verifyEmpty(dest,cp)) dest = dest.upRight();
        verifyAttack(dest,cp);
        dest= pos.downLeft();
        while(verifyEmpty(dest,cp)) dest = dest.downLeft();
        verifyAttack(dest,cp);
        dest= pos.downRight();
        while(verifyEmpty(dest,cp)) dest = dest.downRight();
        verifyAttack(dest,cp);
    }

    private void checkCross(ChessPiece cp, Position pos){
        Position dest = pos.up();
        while(verifyEmpty(dest,cp)) dest = dest.up();
        verifyAttack(dest,cp);
        dest = pos.right();
        while(verifyEmpty(dest,cp)) dest = dest.right();
        verifyAttack(dest,cp);
        dest= pos.down();
        while(verifyEmpty(dest,cp)) dest = dest.down();
        verifyAttack(dest,cp);
        dest= pos.left();
        while(verifyEmpty(dest,cp)) dest = dest.left();
        verifyAttack(dest,cp);
    }

    // return true if empty
    public boolean verifyEmpty(Position destination, ChessPiece cp){
        if(destination==null) return false;
        if(board.getArray().get(destination)==null) {
            board.addOnArriv(destination,cp);
            return cp.addPossibleMove(destination, board);
        } else board.addOnLeav(destination,cp);
        return false;
    }

    public boolean verifyPawnAttack(Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)!= null && board.getArray().get(destination).getId()*cp.getId()<0) {
            cp.addPossibleMove(destination, board);
            board.addOnLeav(destination,cp);
        }
        else board.addOnArriv(destination,cp);// todo on arrive
        Position enPassant = new Position(cp.getPosition().row(),destination.col());
        if(enPassant!=null){
            if(board.getArray().get(enPassant)!=null && board.getArray().get(enPassant).getId()*cp.getId()<0
                    && board.getArray().get(enPassant).isEnPassant())
                cp.getPossibleMoves().add(destination); //TODO verify only when pawn isPassant move
        }
        return true;
    }

    public boolean verifyAttack(Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)!=null && board.getArray().get(destination).getId() * cp.getId()<0)
            cp.addPossibleMove(destination,board);
        else board.addOnLeav(destination,cp);
        return true;
    }

    //meant for knight and king
    public boolean verifyEmptyAttack(Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)==null||board.getArray().get(destination).getId() * cp.getId()<=0)
            cp.addPossibleMove(destination,board);
        else board.addOnLeav(destination,cp);
        return true;
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
}

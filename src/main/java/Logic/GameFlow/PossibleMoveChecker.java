package Logic.GameFlow;

import Logic.Board.Board;
import Logic.Board.Position;
import Logic.Piece.ChessPiece;

import java.util.LinkedList;

public class PossibleMoveChecker {
    private static Board board;
    public static void init(Board b){
        board=b;
    }
    public static void checkPossibleMove(ChessPiece cp){
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

    private static void verifyRockMove(ChessPiece cp) {
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

    private static void checkDiagonal(ChessPiece cp, Position pos){
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

    private static void checkCross(ChessPiece cp, Position pos){
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
    public static boolean verifyEmpty(Position destination, ChessPiece cp){
        if(destination==null) return false;
        if(board.getArray().get(destination)==null) {
            board.addOnArriv(destination,cp);
            return cp.addPossibleMove(destination, board);
        } else board.addOnLeav(destination,cp);
        return false;
    }

    public static boolean verifyPawnAttack(Position destination, ChessPiece cp){
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

    public static boolean verifyAttack(Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)!=null && board.getArray().get(destination).getId() * cp.getId()<0)
            cp.addPossibleMove(destination,board);
        else board.addOnLeav(destination,cp);
        return true;
    }

    //meant for knight and king
    public static boolean verifyEmptyAttack(Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)==null||board.getArray().get(destination).getId() * cp.getId()<=0)
            cp.addPossibleMove(destination,board);
        else board.addOnLeav(destination,cp);
        return true;
    }
    private static void enPassantCheckMove(Position pos, ChessPiece cp) {//todo move this
        Position next= pos.left();
        if(pos.row()< board.getSize() - 3 && pos.row()> 2){
            checkNextTo(pos,next,cp);
            next= pos.right();
            checkNextTo(pos,next,cp);
        }else cp.setEnPassant(false);
    }

    private static void checkNextTo(Position pos, Position next,ChessPiece cp){
        if(next != null && board.getArray().get(next)!= null && board.getArray().get(next).getName().equals("pawn") &&
                board.getArray().get(next).isEnPassant()) {
            board.getArray().get(next)
                    .getPossibleMoves().add(new Position((cp.getPosition().row()+pos.row())/2,pos.col()));
        }
    }
}

package Logic.GameFlow;

import Logic.Board.Board;
import Logic.Board.Position;
import Logic.Piece.ChessPiece;

import java.util.LinkedList;

public class PossibleMoveChecker {

    public static void checkPossibleMove(Board board,ChessPiece cp){
    Position pos = cp.getPosition();
    cp.setPossibleMoves(new LinkedList<>());
    switch(cp.getMovePattern()){
        case UP1:
            if(verifyEmpty(board,pos.up(),cp) && !cp.hasMoved())
                verifyEmpty(board,pos.up().up(),cp);
            verifyPawnAttack(board,pos.upLeft(),cp);
            verifyPawnAttack(board,pos.upRight(),cp);
            break;
        case DOWN1:
            if(verifyEmpty(board,pos.down(),cp) && !cp.hasMoved())
                verifyEmpty(board,pos.down().down(),cp);
            verifyPawnAttack(board,pos.downRight(),cp);
            verifyPawnAttack(board,pos.downLeft(),cp);
            break;
        case L:
            Position dest = pos.up();
            if(dest!=null) {
                verifyEmptyAttack(board,dest.upRight(),cp);
                verifyEmptyAttack(board,dest.upLeft(),cp);
            }
            dest = pos.right();
            if(dest!=null) {
                verifyEmptyAttack(board,dest.upRight(),cp);
                verifyEmptyAttack(board,dest.downRight(),cp);
            }
            dest = pos.down();
            if(dest!=null) {
                verifyEmptyAttack(board,dest.downLeft(),cp);
                verifyEmptyAttack(board,dest.downRight(),cp);
            }
            dest = pos.left();
            if(dest!=null) {
                verifyEmptyAttack(board,dest.upLeft(),cp);
                verifyEmptyAttack(board,dest.downLeft(),cp);
            }
            break;
        case ONE:
            verifyEmptyAttack(board,pos.right(),cp);
            verifyEmptyAttack(board,pos.left(),cp);
            verifyEmptyAttack(board,pos.up(),cp);
            verifyEmptyAttack(board,pos.down(),cp);
            verifyEmptyAttack(board,pos.upLeft(),cp);
            verifyEmptyAttack(board,pos.upRight(),cp);
            verifyEmptyAttack(board,pos.downLeft(),cp);
            verifyEmptyAttack(board,pos.downRight(),cp);
            verifyRockMove(board,cp);
            break;
        case DIAGONAL:
            checkDiagonal(board,cp,pos);
            break;
        case CROSS:
            checkCross(board,cp,pos);
            break;
        case QUEEN:
            checkDiagonal(board,cp,pos);
            checkCross(board,cp,pos);
            break;
        default:
            System.out.println("default case activated");
    }
}

    private static void verifyRockMove(Board board,ChessPiece cp) {
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

    private static void checkDiagonal(Board board,ChessPiece cp, Position pos){
        Position dest = pos.upLeft();
        while(verifyEmpty(board,dest,cp)) dest = dest.upLeft();
        verifyAttack(board,dest,cp);
        dest= pos.upRight();
        while(verifyEmpty(board,dest,cp)) dest = dest.upRight();
        verifyAttack(board,dest,cp);
        dest= pos.downLeft();
        while(verifyEmpty(board,dest,cp)) dest = dest.downLeft();
        verifyAttack(board,dest,cp);
        dest= pos.downRight();
        while(verifyEmpty(board,dest,cp)) dest = dest.downRight();
        verifyAttack(board,dest,cp);
    }

    private static void checkCross(Board board,ChessPiece cp, Position pos){
        Position dest = pos.up();
        while(verifyEmpty(board,dest,cp)) dest = dest.up();
        verifyAttack(board,dest,cp);
        dest = pos.right();
        while(verifyEmpty(board,dest,cp)) dest = dest.right();
        verifyAttack(board,dest,cp);
        dest= pos.down();
        while(verifyEmpty(board,dest,cp)) dest = dest.down();
        verifyAttack(board,dest,cp);
        dest= pos.left();
        while(verifyEmpty(board,dest,cp)) dest = dest.left();
        verifyAttack(board,dest,cp);
    }

    // return true if empty
    private static boolean verifyEmpty(Board board,Position destination, ChessPiece cp){
        if(destination==null) return false; //out of board
        if(board.getArray().get(destination)==null) { //empty
//            board.addOnArriv(destination,cp);
            return cp.addPossibleMove(destination, board);
        } else
            board.addOnLeav(destination,cp); //blocked by a piece
        return false;
    }

    private static boolean verifyPawnAttack(Board board,Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)!= null && board.getArray().get(destination).isWhite()!=cp.isWhite()) {
            cp.addPossibleMove(destination, board);
//            board.addOnLeav(destination,cp);
        }else board.addOnArriv(destination,cp);
        Position enPassant = new Position(cp.getPosition().row(),destination.col());
        if(enPassant!=null){
            if(board.getArray().get(enPassant)!=null && board.getArray().get(enPassant).isWhite()!=cp.isWhite()
                    && board.getArray().get(enPassant).isEnPassant())
                cp.getPossibleMoves().add(destination);
        }
        return true;
    }

    private static boolean verifyAttack(Board board,Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)!=null){
            if(board.getArray().get(destination).isWhite()!=cp.isWhite()) cp.addPossibleMove(destination, board);
            else { //allied piece
                board.addOnArriv(destination,cp);//allied piece gets eaten
                board.addOnLeav(destination,cp);//allied piece changes position
            }
        }

        return true;
    }

    //meant for knight and king
    private static boolean verifyEmptyAttack(Board board,Position destination, ChessPiece cp){
        if(destination == null) return false;
        if(board.getArray().get(destination)==null||board.getArray().get(destination).isWhite()!=cp.isWhite())
            cp.addPossibleMove(destination,board);
        else if(board.getArray().get(destination)!=null) {
            board.addOnLeav(destination,cp);
            board.addOnArriv(destination,cp);
        }
        return true;
    }

}

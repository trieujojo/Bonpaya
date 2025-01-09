package Logic.GameFlow;

import Logic.Board.Board;
import Logic.Piece.ChessPiece;

import static Logic.GameFlow.PossibleMoveChecker.checkPossibleMove;

public class MoveBack {
    public static void moveBack(Board board){
        if(board.getMoveLog().size()!=0) {
            Move moveLog = board.getMoveLog().removeLast();
            moveLog.chessPiece().setPosition(moveLog.start());
            board.getArray().put(moveLog.destination(),null);
            board.getArray().put(moveLog.start(),moveLog.chessPiece());
            if (moveLog.eatenPiece() != null) {
                board.getArray().put(moveLog.eatenPiece().getPosition(), moveLog.eatenPiece());
                board.uneaten();
            }
            moveLog.chessPiece().setHasMoved(moveLog.hasMoved());
            if(moveLog.promotion()){
                board.getChessPieces().get(moveLog.chessPiece().getColor()).removeLast();
                board.getChessPieces().get(moveLog.chessPiece().getColor()).add(moveLog.chessPiece());
            }
            if (moveLog.chessPiece().getName().equals("rook") && board.getMoveLog().size()>0 &&
                    board.getMoveLog().getLast().chessPiece().getName().equals("king")) {
                Move k = board.getMoveLog().getLast();
                if (Math.abs(k.start().col() - k.destination().col()) > 1) {
                    moveBack(board);
                    board.changeTurn();
                }
            }
            board.setEndGame(false);
            for (ChessPiece cp : moveLog.checkThose()) {
                checkPossibleMove(board,cp);
            }
            checkPossibleMove(board, moveLog.chessPiece());
            board.changeTurn();
        }else{
            System.out.println("outOfMoveLog!!!");
            board.print();
        }
    }
}

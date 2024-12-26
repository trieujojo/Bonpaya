package Logic;

import Logic.Board.Board;
import Logic.Piece.ChessPiece;

import static Logic.PossibleMoveChecker.checkPossibleMove;

public class MoveBack {
    public static void moveBack(Board board){
        if(board.getMoveLog().size()!=0) {
            Move moveLog = board.getMoveLog().removeLast();
            moveLog.chessPiece().setPosition(moveLog.start());
            board.getArray().remove(moveLog.destination());
            board.getArray().put(moveLog.start(),moveLog.chessPiece());
            if (moveLog.eatenPiece() != null)
                board.getArray().put(moveLog.eatenPiece().getPosition(), moveLog.eatenPiece());
            moveLog.chessPiece().setHasMoved(moveLog.hasMoved());

            for (ChessPiece cp : moveLog.checkThose()) {
                checkPossibleMove(cp);
            }
            checkPossibleMove(moveLog.chessPiece());
            if (moveLog.chessPiece().getName().equals("rook") && board.getMoveLog().getLast().chessPiece().getName().equals("king")) {
                Move k = board.getMoveLog().getLast();
                if (Math.abs(k.start().col() - k.destination().col()) > 1) {
                    moveBack(board);
                    board.changeTurn();
                }

            }
            board.changeTurn();
        }
    }
}

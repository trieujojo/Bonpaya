package Logic.Piece;

import Logic.Board.Board;
import Logic.Board.Position;

import java.util.*;

public class PieceFactory {
    public static LinkedList<ChessPiece> getPiecesSet(Board board, boolean white){
        LinkedList<ChessPiece> piecesList = new LinkedList<>();
        int pieceRow;
        int id, idInc;
        if(white) {
            pieceRow=1;
            id=1;
            idInc=1;
        } else {
            pieceRow=board.getSize()-2;
            id=-1;
            idInc=-1;
        }
        for (int i = 0; i < board.getSize(); i++) {
            ChessPiece pawn = new ChessPiece("pawn",white?"white":"black",(i+1)*id,new Position(pieceRow,i));
            if(white)pawn.setMovePattern(MovePattern.UP1);
            else pawn.setMovePattern(MovePattern.DOWN1);
//            if(!white) System.out.print(id + " id: "+ pawn.getId());
            board.getArray().put(new Position(pieceRow,i), pawn);
            piecesList.add(pawn);
        }
        id = (board.getSize())*id+idInc;
        String[] piecesname = new String[]{"rook","bishop","knight"};
        MovePattern[] patterns = new MovePattern[]{MovePattern.CROSS,MovePattern.DIAGONAL,MovePattern.L};

        if(white) pieceRow--;
        else pieceRow++;
        for (int i = 0; i < 3; i++) {
            ChessPiece p = new ChessPiece(piecesname[i],white?"white":"black",id, new Position(pieceRow,i));
            p.setMovePattern(patterns[i]);
            board.getArray().put(new Position(pieceRow,i),p);//TODO rewrite this
            id += idInc;
            piecesList.add(p);
            ChessPiece p2 = new ChessPiece(piecesname[i],white?"white":"black",id,new Position(pieceRow,board.getSize()-i-1));
            p2.setMovePattern(patterns[i]);
            board.getArray().put(new Position(pieceRow, board.getSize()-1-i),p2);//[pieceRow][board.getSize()-i-1]=id;
            id += idInc;
            piecesList.add(p2);
        }
        int queenPos= board.getSize()/2,kingPos= board.getSize()/2-1;

        ChessPiece q = new ChessPiece("queen",white?"white":"black",id,new Position(pieceRow,queenPos));
        q.setMovePattern(MovePattern.QUEEN);
        board.getArray().put(new Position(pieceRow,queenPos),q);//[pieceRow][queenPos]=id;
        if(id<0) id--;
        else id++;
        piecesList.add(q);
//        System.out.println("king is :"+ id);
        ChessPiece k = new ChessPiece("king",white?"white":"black",id, new Position(pieceRow,kingPos));
        k.setMovePattern(MovePattern.ONE);
        board.getArray().put(new Position(pieceRow,kingPos),k);//[pieceRow][kingPos]=id;
        piecesList.add(k);

        return piecesList;
    }

    public static LinkedList<ChessPiece> cloneSet(List<ChessPiece> original){
        LinkedList<ChessPiece> clone = new LinkedList<>();
        for(ChessPiece cp : original){
            clone.add(cp.clone());
        }
        return clone;
    }

    public static ChessPiece getSingle(String piece, int id,Position pos){
        ChessPiece chessPiece=new ChessPiece(piece,id>0?"white":"black",id,pos);
        switch(piece){
            case "rook":
                chessPiece.setMovePattern(MovePattern.CROSS);
                break;
            case "bishop":
                chessPiece.setMovePattern(MovePattern.DIAGONAL);
                break;
            case "queen":
                chessPiece.setMovePattern(MovePattern.QUEEN);
                break;
            case "knight":
                chessPiece.setMovePattern(MovePattern.L);
                break;
        }
        return chessPiece;
    }
}

package Logic;

import Logic.Board.Board;
import Logic.Piece.ChessPiece;
import Logic.Piece.PieceFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    }


 // TODO: add the possible "influence on other moves"
    // threaten (pawn) and block
    public void checkPossibleMove(ChessPiece cp){
        int[] pos = cp.getPosition();
        cp.setPossibleMoves(new LinkedList<>());
        switch(cp.getMovePattern()){
            case UP1:
                if(pos[0]+1<board.getSize() && board.getBoard()[pos[0]+1][pos[1]]==0)
                    cp.getPossibleMoves().add(new int[]{pos[0]+1,pos[1]});
                if(!cp.hasMoved() && board.getBoard()[pos[0]+2][pos[1]]==0)
                    cp.getPossibleMoves().add(new int[]{pos[0]+2,pos[1]});
                if(pos[0]+1<board.getSize() && pos[1]+1<board.getSize()
                && cp.getId()*board.getBoard()[pos[0]+1][pos[1]+1]<0)
                    cp.getPossibleMoves().add(new int[]{pos[0]+1,pos[1]+1});
                if(pos[0]+1<board.getSize() && pos[1]-1>0
                        && cp.getId()*board.getBoard()[pos[0]+1][pos[1]-1]<0)
                    cp.getPossibleMoves().add(new int[]{pos[0]+1,pos[1]-1});
                break;
            case DOWN1:
                if(pos[0]-1>0 && board.getBoard()[pos[0]-1][pos[1]]==0)
                    cp.getPossibleMoves().add(new int[]{pos[0]-1,pos[1]});
                if(!cp.hasMoved() && board.getBoard()[pos[0]-2][pos[1]]==0)
                    cp.getPossibleMoves().add(new int[]{pos[0]-2,pos[1]});
                if(pos[0]-1>0 && pos[1]+1<board.getSize()
                        && cp.getId()*board.getBoard()[pos[0]-1][pos[1]+1]<0)
                    cp.getPossibleMoves().add(new int[]{pos[0]-1,pos[1]+1});
                if(pos[0]-1>0 && pos[1]-1>0
                        && cp.getId()*board.getBoard()[pos[0]-1][pos[1]-1]<0)
                    cp.getPossibleMoves().add(new int[]{pos[0]-1,pos[1]-1});
                break;
            case L:
                if(pos[0]+2< board.getSize()){ // up2
                    if(pos[1]+1< board.getSize() && board.getBoard()[pos[0]+2][pos[1]+1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+2,pos[1]+1});
                    if(pos[1]-1> 0 && board.getBoard()[pos[0]+2][pos[1]-1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+2,pos[1]-1});
                }else if(pos[0]-2>0){  // down2
                    if(pos[1]+1< board.getSize() && board.getBoard()[pos[0]-2][pos[1]+1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-2,pos[1]+1});
                    if(pos[1]-1> 0 && board.getBoard()[pos[0]-2][pos[1]-1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-2,pos[1]-1});
                }
                if(pos[1]+2< board.getSize()){ // right 2
                    if(pos[0]+1< board.getSize() && board.getBoard()[pos[0]+1][pos[1]+2]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+1,pos[1]+2});
                    if(pos[0]-1> 0 && board.getBoard()[pos[0]-1][pos[1]+2]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-1,pos[1]+2});
                }else if(pos[1]-2>0){ // left 2
                    if(pos[0]+1< board.getSize() && board.getBoard()[pos[0]+1][pos[1]-2]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+1,pos[1]-2});
                    if(pos[0]-1> 0 && board.getBoard()[pos[0]-1][pos[1]-2]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-1,pos[1]-2});
                }
                break;
            case ONE:
                if(pos[0]+1< board.getSize()){
                    if(board.getBoard()[pos[0]+1][pos[1]]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+1,pos[1]});
                    if(pos[1]+1< board.getSize() && board.getBoard()[pos[0]+1][pos[1]+1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+1, pos[1]+1});
                    if(pos[1]-1> 0 && board.getBoard()[pos[0]+1][pos[1]-1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]+1, pos[1]-1});
                }
                if(pos[0]-1>0){
                    if(board.getBoard()[pos[0]-1][pos[1]]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-1,pos[1]});
                    if(pos[1]+1< board.getSize() && board.getBoard()[pos[0]-1][pos[1]+1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-1, pos[1]+1});
                    if(pos[1]-1> 0 && board.getBoard()[pos[0]-1][pos[1]-1]*cp.getId()<=0)
                        cp.getPossibleMoves().add(new int[]{pos[0]-1, pos[1]-1});
                }
                if(pos[1]+1< board.getSize() &&board.getBoard()[pos[0]][pos[1]+1]*cp.getId()<=0)
                    cp.getPossibleMoves().add(new int[]{pos[0], pos[1] + 1});
                if(pos[1]-1> 0 &&board.getBoard()[pos[0]][pos[1]-1]*cp.getId()<=0)
                    cp.getPossibleMoves().add(new int[]{pos[0], pos[1]-1});
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
        }
    }

    private void checkDiagonal(ChessPiece cp, int[] pos){
        int index=1;
        boolean empty=true;
        while(pos[0]+index< board.getSize() && pos[1]+index< board.getSize() && empty){
            if(board.getBoard()[pos[0]+index][pos[1]+index]!=0) empty = false;
            else cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]+index});
            index++;
        }
        if(pos[0]+index< board.getSize() && pos[1]+index< board.getSize()
                && board.getBoard()[pos[0]+index][pos[1]+index]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]+index});
        index=-1;
        empty=true;
        while(pos[0]+index>0 && pos[1]+index>0 && empty){
            if(board.getBoard()[pos[0]+index][pos[1]+index]!=0) empty = false;
            else cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]+index});
            index--;
        }
        if(pos[0]+index>0 && pos[1]+index>0
                && board.getBoard()[pos[0]+index][pos[1]+index]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]+index});
        index =1;
        empty = true;
        while(pos[0]-index>0 && pos[1]+index< board.getSize() && empty){
            if(board.getBoard()[pos[0]-index][pos[1]+index]!=0) empty = false;
            else cp.getPossibleMoves().add(new int[]{pos[0]-index,pos[1]+index});
            index++;
        }
        if(pos[0]-index>0 && pos[1]+index< board.getSize()
                && board.getBoard()[pos[0]-index][pos[1]+index]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0]-index,pos[1]+index});
        index =1;
        empty = true;
        while(pos[0]+index< board.getSize() && pos[1]-index>0 && empty){
            if(board.getBoard()[pos[0]+index][pos[1]-index]!=0) empty = false;
            else cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]-index});
            index++;
        }
        if(pos[0]+index< board.getSize() && pos[1]-index>0
                && board.getBoard()[pos[0]+index][pos[1]-index]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]-index});
    }

    private void checkCross(ChessPiece cp, int[] pos){
        int index=1;
        boolean empty=true;
        while(pos[0]+index< board.getSize() && empty){
            if(board.getBoard()[pos[0]+index][pos[1]]!=0) empty=false;
            else cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]});
        }
        if(pos[0]+index< board.getSize() && board.getBoard()[pos[0]+index][pos[1]]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0]+index,pos[1]});
        index=1;empty=true;
        while(pos[1]+index< board.getSize() && empty){
            if(board.getBoard()[pos[0]][pos[1]+index]!=0) empty=false;
            else cp.getPossibleMoves().add(new int[]{pos[0],pos[1]+index});
        }
        if(pos[1]+index< board.getSize() && board.getBoard()[pos[0]][pos[1]+index]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0],pos[1]+index});
        index=1;empty=true;
        while(pos[0]-index>0 && empty){
            if(board.getBoard()[pos[0]-index][pos[1]]!=0) empty=false;
            else cp.getPossibleMoves().add(new int[]{pos[0]-index,pos[1]});
        }
        if(pos[0]-index< board.getSize() && board.getBoard()[pos[0]-index][pos[1]]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0]-index,pos[1]});
        index=1;empty=true;
        while(pos[1]-index>0 && empty){
            if(board.getBoard()[pos[0]][pos[1]-index]!=0) empty=false;
            else cp.getPossibleMoves().add(new int[]{pos[0],pos[1]-index});
        }
        if(pos[1]-index< board.getSize() && board.getBoard()[pos[0]][pos[1]-index]*cp.getId()<0)
            cp.getPossibleMoves().add(new int[]{pos[0],pos[1]-index});

    }

    public Board getBoard() {
        return board;
    }
}

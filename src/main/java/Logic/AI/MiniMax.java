package Logic.AI;

import Logic.Board.Board;
import Logic.Board.Position;
import Logic.GameFlow.Move;
import Logic.GameFlow.MoveBack;
import Logic.Piece.ChessPiece;

public class MiniMax {
    private Board board;
    private int depth;
    private Move chosenMove;
    
    public MiniMax(Board board, int depth){
        this.board=board;
        this.depth=depth;
    }

    public Move evaluate(){
        Board clone = board.clone();
        evaluate(board,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,board.isWhiteTurn());
        return chosenMove;
    }

    private int evaluate(Board board, int depth, int alpha,int beta ,boolean maximize){
        if(depth==0||board.isEndGame()){
            return staticEval1(board);
        }
        if(maximize){
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < board.getChessPieces().get("white").size() && beta>alpha; i++) {
                for(Position p: board.getChessPieces().get("white").get(i).getPossibleMoves()){
                    board.movePiece(board.getChessPieces().get("white").get(i),p);
                    int eval = evaluate(board,depth-1,alpha,beta,!maximize);
                    if(eval>maxEval){
                        maxEval=eval;
                        chosenMove= board.getMoveLog().getFirst();
                    }
                    alpha=Math.max(alpha,eval);
                    MoveBack.moveBack(board);
                    if(beta<=alpha) break;
                }
            }
            return maxEval;
        }else{
            int minEval=Integer.MAX_VALUE;
            for (int i = 0; i < board.getChessPieces().get("black").size() && beta>alpha; i++) {
                for (Position p : board.getChessPieces().get("black").get(i).getPossibleMoves()) {
                    board.movePiece(board.getChessPieces().get("black").get(i),p);
                    int eval = evaluate(board,depth-1,alpha,beta,!maximize);
                    if(eval<minEval){
                        minEval=eval;
                        chosenMove=board.getMoveLog().getFirst();
                    }
                    beta = Math.min(beta,eval);
                    if(beta<=alpha) break;
                }
            }
            return minEval;
        }
    }
    
    private int staticEval1(Board board){
        int sum=0;
        for(Move move :board.getMoveLog()){

        }
        return 0;
    }

    private int addSum(int toAdd, ChessPiece cp){
        if(cp==null) return toAdd;
        int sign;
        if(cp.getColor().equals("white")) sign=-1;
        else sign=1;
        int value;
        switch(cp.getName()){
            case "pawn": value=1;
            break;
            case "knight":value=4;
            break;
            case "bishop":value=6;
            break;
            case "rook": value=8;
            break;
            case "queen": value=11;
            break;
            case "king": value=100;
            break;
            default:value=0;
        }
        return toAdd + (sign*value);
    }
}

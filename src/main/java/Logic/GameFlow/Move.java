package Logic.GameFlow;

import Logic.Board.Position;
import Logic.Piece.ChessPiece;

import java.util.*;

public record Move(Position start, Position destination, ChessPiece chessPiece, ChessPiece eatenPiece, boolean promotion, List<ChessPiece> checkThose, boolean hasMoved) {

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(chessPiece.getName());
        sb.append(chessPiece.getId());
        sb.append(" from position ");
        sb.append(start);
        sb.append(" to ");
        sb.append(destination);
        return sb.toString();
    }

    public static Map<Position,Set<ChessPiece>> cloneBlocked(Map<Position,Set<ChessPiece>> origin){
        Map<Position, Set<ChessPiece>> result = new HashMap<>();
        for(Position p: origin.keySet()){
            result.put(p,new HashSet<>());
            for(ChessPiece cp : origin.get(p))
                result.get(p).add(cp.clone());
        }
        return result;
    }

    public static List<Position> clonePositions(List<Position> origin){
        LinkedList<Position> result = new LinkedList<>();
        for(Position p:origin){
            result.add(p);
        }
        return result;
    }
}

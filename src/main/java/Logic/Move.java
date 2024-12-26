package Logic;

import Logic.Board.Position;
import Logic.Piece.ChessPiece;

import java.util.List;

public record Move(Position start, Position destination, ChessPiece chessPiece, ChessPiece eatenPiece, List<ChessPiece> checkThose, boolean hasMoved) {


}

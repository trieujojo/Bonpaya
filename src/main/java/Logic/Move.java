package Logic;

import Logic.Board.Position;
import Logic.Piece.ChessPiece;

public record Move(Position start, Position destination, ChessPiece chessPiece, ChessPiece eatenPiece) {

}

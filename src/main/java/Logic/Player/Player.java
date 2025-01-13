package Logic.Player;

import Logic.Board.Board;
import Logic.GameFlow.Move;

public interface Player {
    public void promotePiece();
    public Move getMove(Board b);
    public boolean isWhite();
    public String getName();
    public void setWhite(boolean white);
}

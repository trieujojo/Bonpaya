package Logic.Player;

import Logic.GameFlow.Move;

public interface Player {
    public void promotePiece();
    public Move getMove();
    public boolean isWhite();
    public String getName();
}

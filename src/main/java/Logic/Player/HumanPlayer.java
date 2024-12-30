package Logic.Player;

import Logic.GameFlow.Move;
import com.example.bonpaya.HelloController;

public class HumanPlayer implements Player{
    private String name;
    private boolean white;
    private HelloController controller;

    public HumanPlayer(String name, boolean whitePiece, HelloController controller){
        this.name=name;
        white=whitePiece;
        this.controller=controller;
    }

    @Override
    public void promotePiece() {
        controller.promptPiecePromotion();
//        return controller.getChoice();
    }

    @Override
    public Move getMove() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }
}

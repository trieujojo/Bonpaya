package Logic.Player;

import Logic.AI.MiniMax;
import Logic.Board.Board;
import Logic.GameFlow.Move;
import com.example.bonpaya.HelloController;

public class MiniMaxPlayer implements Player{
    private String name;
    private boolean white;
    private HelloController controller;
    private MiniMax miniMax;

    public MiniMaxPlayer(String name, boolean white, HelloController controller, Board b, int depth){
        this.name=name;
        this.white=white;
        this.controller=controller;
        miniMax=new MiniMax(b,depth);

    }
    @Override
    public void promotePiece() {
        controller.promoteAI("queen",white? "white":"black");
    }

    public void fakePromotion(){

    }

    @Override
    public Move getMove() {
        return miniMax.evaluate();
    }

    @Override
    public boolean isWhite() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public HelloController getController() {
        return controller;
    }

    public void setController(HelloController controller) {
        this.controller = controller;
    }
}

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
    private int depth;

    public MiniMaxPlayer(String name, boolean white, HelloController controller, int depth){
        this.name=name;
        this.white=white;
        this.controller=controller;
        miniMax=new MiniMax(depth);
        this.depth=depth;
    }
    @Override
    public void promotePiece() {
        controller.promote("queen",white? "white":"black");
    }

    public void fakePromotion(){

    }

    @Override
    public Move getMove(Board b) {
        return miniMax.evaluate(b,white);
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

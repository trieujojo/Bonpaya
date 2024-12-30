package com.example.bonpaya;

import Logic.Board.Position;
import Logic.GameFlow.GameLogic;
import Logic.GameFlow.Move;
import Logic.GameFlow.MoveBack;
import Logic.Parameters;
import Logic.Piece.ChessPiece;
import Logic.Player.HumanPlayer;
import Logic.Player.MiniMaxPlayer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class HelloController {
    private HelloApplication app;
    private Scene homeScene;
    private Scene optionScene;
    private Scene gameScene;

    private BorderPane gamePane;
    private BorderPane homePane;
    private BorderPane optionPane;

    private GridPane boardUI;
    private Pane[][] boardParts;
    private Map<String, ImageView> imageViewSet;

    private GameLogic gameLogic;
    private ChessPiece pawnSelected;
    private boolean computerPlay = false;


    public void initUI(Stage stage){
        createHome();
        createOption();
        gameLogic = new GameLogic();
        createGame();
        stage.setTitle("Bonpaya!");
    }

    public Scene getHomeScene() {
        return homeScene;
    }

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    private void createHome(){
        homePane = new BorderPane();
        homeScene= new Scene(homePane , Logic.Parameters.getScreenWidth(), Logic.Parameters.getScreenHeight());
        homePane.setStyle("-fx-background-color: beige;");
        Button playButton = new Button("Play");
        Button optionButton = new Button("Option");
        optionButton.setOnAction(this::optionButtonHandler);
        playButton.setOnAction(this::playButtonHandler);
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(this::exitButtonHandler);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(50));

        vbox.getChildren().addAll( playButton, optionButton, exitButton);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        homePane.setCenter(vbox);
        try{
            Image image = new Image(this.getClass().getResource("/Images/BonpayaTitle.png").toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(Logic.Parameters.getScreenWidth());
            homePane.setTop(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createOption(){
        optionPane = new BorderPane();
        optionScene = new Scene(optionPane, Parameters.getScreenWidth(), Parameters.getScreenHeight() );
        ChoiceBox<String> box = new ChoiceBox<String>();
        //Retrieving the observable list
        ObservableList<String> oslist = box.getItems();
        oslist.addAll("Player Vs Player", "Player Vs Computer");
        box.getSelectionModel().selectFirst();
        //Setting the position of the choice box
        box.setTranslateX(200);
        box.setTranslateY(15);
        //Setting the label
        Label setlabel = new Label("Select your mode of play:");
        setlabel.setTranslateX(20);
        setlabel.setTranslateY(20);
        //Adding the choice box to the group
        Group newgrp = new Group(box, setlabel);
        //Setting the stage
//        box.onActionProperty().addListener(new ChangeListener<EventHandler<ActionEvent>>() {
//            @Override //doesn't work
//            public void changed(ObservableValue<? extends EventHandler<ActionEvent>> observableValue, EventHandler<ActionEvent> actionEventEventHandler, EventHandler<ActionEvent> t1) {
//                if(box.getValue().equals("Player Vs Computer")){
//                    computerPlay=true;
//                }else computerPlay=false;
//            }
//        });
        box.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number value, Number newVal) {
                        System.out.println(newVal+ " "+ value+ " "+observableValue);
                        if(newVal.equals(1)) computerPlay=true;
                        else computerPlay=false;
                    }
                });
        optionPane.getChildren().add(newgrp);
        Button backButton = new Button("Back");
        backButton.setOnAction(this::backButtonHandler);
        optionPane.setCenter(backButton);
        optionPane.setStyle("-fx-background-color: grey;");
    }

    private void createGame(){
        pawnSelected=null;
        imageViewSet = new HashMap<>();

        gamePane = new BorderPane();
        gameScene = new Scene(gamePane,Logic.Parameters.getScreenWidth(), Logic.Parameters.getScreenHeight() );
        boardUI= new GridPane();
        boardUI.setOnMouseClicked(this::clickGrid);
        int size = gameLogic.getBoard().getSize();
        boardParts = new Pane[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Pane square = new Pane();
                String brownStyle = "-fx-background-color: brown;",
                        beigeStyle = "-fx-background-color: beige;";
                if (i % 2 == 0) {
                    if (j % 2 == 0) square.setStyle(beigeStyle);
                    else  square.setStyle(brownStyle);
                } else {
                    if (j % 2 != 0) square.setStyle(beigeStyle);
                    else square.setStyle(brownStyle);
                }
                boardParts[i][j] = square;
                boardUI.add(square, i, j);
            }
        }
        for (int i = 0; i < size; i++) {
            int w = Logic.Parameters.getScreenWidth()/8, h = Logic.Parameters.getScreenHeight()/8-2;
            boardUI.getColumnConstraints().add(new ColumnConstraints(w, w, w, Priority.ALWAYS, HPos.CENTER, true));
            boardUI.getRowConstraints().add(new RowConstraints(h, h, h, Priority.ALWAYS, VPos.CENTER, true));
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(this::backMoveHandler);
        gamePane.setBottom(backButton);
        gamePane.setCenter(boardUI);
        for (ChessPiece cp: gameLogic.getBoard().getChessPieces().get("white")) {
            chessPieceView(cp, "white");
        }
        for (ChessPiece cp: gameLogic.getBoard().getChessPieces().get("black")) {
            chessPieceView(cp, "black");
        }
        gamePane.setStyle("-fx-background-color: black;");
       createPlayers();
    } //todo

    private void createPlayers(){
        if(!computerPlay){
            gameLogic.setPlayers(new HumanPlayer("player 1",true,this),
                    new HumanPlayer("player 2",false,this));
        }else{
            gameLogic.setPlayers(new HumanPlayer("player 1",true,this),
                    new MiniMaxPlayer("computer",false,this,gameLogic.getBoard(),4));
        }
    }
    private void backMoveHandler(ActionEvent actionEvent) {
        MoveBack.moveBack(gameLogic.getBoard());
        actualizeGrid();
    }

    private void actualizeGrid() {
        clearGrid();
        for(Position pos: gameLogic.getBoard().getArray().keySet()){
            if(gameLogic.getBoard().getArray().get(pos)!=null) boardUI.add(imageViewSet.get(String.valueOf(gameLogic.getBoard().getArray().get(pos).getId())), pos.col(), pos.row());
        }
    }

    private void clearGrid() {
        for(ImageView image: imageViewSet.values()){
            boardUI.getChildren().remove(image);
        }
    }

    private void chessPieceView(ChessPiece cp, String color){
        try{
            Image image = new Image(this.getClass().getResource("/Images/"+cp.getName()+color+".png").toExternalForm(),30,30,false, false);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(Logic.Parameters.getScreenHeight()/8-5);
            imageView.setFitWidth(Logic.Parameters.getScreenWidth()/10);
            boardUI.add(imageView,cp.getPosition().col(),cp.getPosition().row());
            imageViewSet.put(String.valueOf(cp.getId()), imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void optionButtonHandler(ActionEvent actionEvent){
        app.getMainStage().setTitle("Option");
        app.getMainStage().setScene(optionScene);
    }

    private void exitButtonHandler(ActionEvent actionEvent){
        app.getMainStage().setTitle("Bonpaya!");
        System.exit(0);
    }

    private void backButtonHandler(ActionEvent actionEvent){
        System.out.println("test");
        if(!computerPlay){
            gameLogic.setPlayers(new HumanPlayer("player 1",true,this),
                    new HumanPlayer("player 2",false,this));
        }else{
            gameLogic.setPlayers(new HumanPlayer("player 1",true,this),
                    new MiniMaxPlayer("computer",false,this,gameLogic.getBoard(),4));
        }
        System.out.println(gameLogic.getBlackPlayer().getName());
        app.getMainStage().setScene(homeScene);
        app.getMainStage().show();
    }

    private void playButtonHandler(ActionEvent actionEvent) {
        app.getMainStage().setScene(gameScene);
        app.getMainStage().show();
        //todo while loop here
    }

    public void clickGrid(javafx.scene.input.MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != boardUI) {
            System.out.println(gameLogic.getBlackPlayer().getName());
            if (gameLogic.isWhiteTurn() && gameLogic.getWhitePlayer() instanceof HumanPlayer ||
                    !gameLogic.isWhiteTurn() && gameLogic.getBlackPlayer() instanceof HumanPlayer) {
                Integer colIndex = GridPane.getColumnIndex(clickedNode),
                        rowIndex = GridPane.getRowIndex(clickedNode);
//            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
                Position click = new Position(rowIndex, colIndex);
                if (pawnSelected == null && gameLogic.getBoard().getArray().get(click) != null) {
                    ChessPiece cp = gameLogic.getBoard().getArray().get(click);
                    if ((cp.getId() > 0 && gameLogic.isWhiteTurn() && gameLogic.getWhitePlayer() instanceof HumanPlayer)
                            || (cp.getId() < 0 && !gameLogic.isWhiteTurn()) && gameLogic.getBlackPlayer() instanceof HumanPlayer)
                        pawnSelected = gameLogic.getBoard().getArray().get(click);
                } else if (pawnSelected != null) {
                    if (gameLogic.movePiece(pawnSelected, click, true)) {
                        actualizeGrid();
                    }
                    pawnSelected = null;
                }
            }else{
                Move m;
                if(gameLogic.isWhiteTurn()){
                   m = gameLogic.getWhitePlayer().getMove();
                }else{
                    m = gameLogic.getBlackPlayer().getMove();
                }
                System.out.println("computing move");
                gameLogic.movePiece(m.chessPiece(),m.destination(),true);
                System.out.println("move completed");
            }
        }
    }

    public void promptPiecePromotion() {
        ChoiceBox<String> box = new ChoiceBox<String>();
        ObservableList<String> oslist = box.getItems();
        oslist.addAll("Queen", "Rook","Bishop","Knight");
        box.getSelectionModel().selectFirst();
        Button okB = new Button("Promote");
        HBox newgrp =new HBox(box,okB);
        newgrp.setPrefSize(200,500);
        newgrp.setStyle("-fx-background-color: "+gameLogic.getPromotedPawn().getColor()+";");
        boardUI.add(newgrp,gameLogic.getPromotionPos().col(),gameLogic.getPromotionPos().row());
        okB.setOnAction((actionEvent)->{
            boardUI.getChildren().remove(imageViewSet.get(String.valueOf(gameLogic.getBoard().getMoveLog().getLast().chessPiece().getId())));
            ChessPiece np = gameLogic.promote(box.getValue().toLowerCase(),(gameLogic.getPromotedPawn().getColor()));
            boardUI.getChildren().remove(newgrp);
            chessPieceView(np,np.getColor());
            actualizeGrid();
        });
    }

    public void promoteAI(String piece, String color){
        boardUI.getChildren().remove(imageViewSet.get(String.valueOf(gameLogic.getBoard().getMoveLog().getLast().chessPiece().getId())));
        ChessPiece np = gameLogic.promote(piece,(color));
    }

}
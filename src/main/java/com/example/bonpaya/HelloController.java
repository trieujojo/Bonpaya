package com.example.bonpaya;

import Logic.Board.*;
import Logic.GameFlow.*;
import Logic.Parameters;
import Logic.Piece.ChessPiece;
import Logic.Player.*;
import javafx.beans.value.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.util.*;

public class HelloController {

    private HelloApplication app;

    private Scene homeScene;
    private Scene optionScene;
    private Scene gameScene;

    private BorderPane gamePane;
    private BorderPane homePane;
    private BorderPane optionPane;
    private Stage dialog=null;

    private GridPane boardUI;
    private Pane[][] boardParts;
    private Map<String, ImageView> imageViewSet;

    private Board board;
    private ChessPiece pawnSelected;
    private boolean computerPlay = false;


    public void initUI(Stage stage){
        createHome();
        createOption();
        board = new Board(Parameters.boardSize(),false);
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
        int size = board.getSize();
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
        for (ChessPiece cp: board.getChessPieces().get("white")) {
            chessPieceView(cp, "white");
        }
        for (ChessPiece cp: board.getChessPieces().get("black")) {
            chessPieceView(cp, "black");
        }
        gamePane.setStyle("-fx-background-color: black;");
        createPlayers();
    }

    private void createPlayers(){
        if(!computerPlay){
            board.setPlayers(new HumanPlayer("player 1",true,this),
                    new HumanPlayer("player 2",false,this));
        }else{
            board.setPlayers(new HumanPlayer("player 1",true,this),
                    new MiniMaxPlayer("computer",false,this,Parameters.getDepth()));
        }
    }
    private void backMoveHandler(ActionEvent actionEvent) {
        MoveBack.moveBack(board);
        actualizeGrid();
    }

    private void actualizeGrid() {
        clearGrid();
        for(Position pos: board.getArray().keySet()){
            if(board.getArray().get(pos)!=null) boardUI.add(imageViewSet.get(String.valueOf(board.getArray().get(pos).getId())), pos.col(), pos.row());
        }
        if(board.isEndGame()) popEnd();
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
        createPlayers();
        app.getMainStage().setScene(homeScene);
        app.getMainStage().show();
        if(dialog!=null){
            dialog.close();
            this.board = board.newGame();
            createPlayers();
        }
    }

    private void playButtonHandler(ActionEvent actionEvent) {
        app.getMainStage().setScene(gameScene);
        app.getMainStage().show();
        //todo while loop here
    }

    public void clickGrid(javafx.scene.input.MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != boardUI) {
            if (board.isPlayerTurn()) {
                Integer colIndex = GridPane.getColumnIndex(clickedNode),
                        rowIndex = GridPane.getRowIndex(clickedNode);
//            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
                Position click = new Position(rowIndex, colIndex);
                if(board.getArray().get(click)!=null){
                    if(board.getArray().get(click).isWhite()==board.isWhiteTurn()) {
                        if (board.getArray().get(click).equals(pawnSelected)) System.out.println(pawnSelected);
                        pawnSelected = board.getArray().get(click);
                    }else if (pawnSelected!=null && board.movePiece(pawnSelected, click))
                        actualizeGrid();
                }else if(pawnSelected!=null){
                    if (board.movePiece(pawnSelected, click))
                        actualizeGrid();
                    pawnSelected = null;
                }
            }else{
                Move m;
                System.out.println("computing move");
                if(board.isWhiteTurn()) m = board.getWhitePlayer().getMove(board);
                else m = board.getBlackPlayer().getMove(board);
                System.out.println(m);
                if(board.movePiece(board.getArray().get(m.start()),m.destination())){
                    System.out.println("move completed");
                }else{
                    for (Position p : board.getArray().keySet()){
                        System.out.println(p+ " "+board.getArray().get(p));
                    }
                }

                actualizeGrid();
            }
        }
    }

    public void promptPiecePromotion() {
        ChoiceBox<String> box = new ChoiceBox<>();
        ObservableList<String> oslist = box.getItems();
        oslist.addAll("Queen", "Rook","Bishop","Knight");
        box.getSelectionModel().selectFirst();
        Button okB = new Button("Promote");
        HBox newgrp =new HBox(box,okB);
        newgrp.setPrefSize(200,500);
        newgrp.setStyle("-fx-background-color: "+board.getPromotedPawn().getColor()+";");
        boardUI.add(newgrp,board.getPromotionPos().col(),board.getPromotionPos().row());
        okB.setOnAction((actionEvent)->{
            promote(box.getValue().toLowerCase(),board.getPromotedPawn().getColor());
            boardUI.getChildren().remove(newgrp);
        });
    }

    public void promote(String piece, String color){
        boardUI.getChildren().remove(imageViewSet.get(String.valueOf(board.getMoveLog().getLast().chessPiece().getId())));
        ChessPiece np = board.promote(piece,color);
        chessPieceView(np,np.getColor());
        actualizeGrid();
    }

    private void popEnd(){
        //todo add going back to homescreen vs play again
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(app.getMainStage());
        VBox dialogVbox = new VBox(20);
        Button rematchB = new Button("Rematch"),homeB = new Button("Home");
        homeB.setOnAction(this::backButtonHandler);
        rematchB.setOnAction(this::newGame);
        dialogVbox.getChildren().add(new Text("Game Over! The winner is the "+board.getMoveLog().getLast().chessPiece().getColor()+" player"));
        dialogVbox.getChildren().add(homeB);
        dialogVbox.getChildren().add(rematchB);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    private void newGame(ActionEvent actionEvent){
        Player p1 = board.getWhitePlayer(),p2=board.getBlackPlayer();
        p1.setWhite(!p1.isWhite());
        p2.setWhite(!p2.isWhite());
        this.board = board.newGame();
        board.setPlayers(p1,p2);
        dialog.close();
        dialog=null;
        actualizeGrid();
    }

}
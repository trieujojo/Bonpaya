package com.example.bonpaya;

import Logic.Board.Position;
import Logic.GameLogic;
import Logic.MoveBack;
import Logic.Parameters;
import Logic.Piece.ChessPiece;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;


public class HelloApplication extends Application {
    private Stage stage;
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

    @Override
    public void start(Stage stage) {
        this.stage=stage;
        initUI();
        stage.setScene(homeScene);
        stage.show();
    }

    private void initUI(){
        createHome();
        createOption();
        gameLogic = new GameLogic();
        createGame();
        stage.setTitle("Bonpaya!");
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
        optionScene = new Scene(optionPane,800, 480 );
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
        stage.setTitle("Option");
        stage.setScene(optionScene);
    }

    private void exitButtonHandler(ActionEvent actionEvent){
        stage.setTitle("Bonpaya!");
        System.exit(0);
    }

    private void backButtonHandler(ActionEvent actionEvent){
        stage.setScene(homeScene);
        stage.show();
    }

    private void playButtonHandler(ActionEvent actionEvent) {
        stage.setScene(gameScene);
        stage.show();
    }

    public void clickGrid(javafx.scene.input.MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != boardUI) {
            Integer colIndex = GridPane.getColumnIndex(clickedNode),
                    rowIndex = GridPane.getRowIndex(clickedNode);
//            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
            Position click = new Position(rowIndex,colIndex);
            if(pawnSelected==null && gameLogic.getBoard().getArray().get(click)!=null){
                ChessPiece cp = gameLogic.getBoard().getArray().get(click);
                if((cp.getId()>0 && gameLogic.isWhiteTurn())||(cp.getId()<0 && !gameLogic.isWhiteTurn()))
                    pawnSelected= gameLogic.getBoard().getArray().get(click);
            }
            else if(pawnSelected!=null) {
                if (gameLogic.movePiece(pawnSelected, click, true)) {
                    if (gameLogic.getBoard().isEatenThisTurn()){//gameLogic.getBoard().getMoveLog().getLast().eatenPiece() != null){
                        boardUI.getChildren().remove(imageViewSet.get(String.valueOf(gameLogic.getBoard().getMoveLog().getLast().eatenPiece().getId())));
                    }
                    if(pawnSelected.getName().equals("king")&&
                            Math.abs(gameLogic.getBoard().getMoveLog().getLast().start().col()-
                                    gameLogic.getBoard().getMoveLog().getLast().destination().col())>1){
                        ChessPiece rook = gameLogic.getBoard().getMoveLog().getLast().chessPiece();
                        boardUI.getChildren().remove(imageViewSet.get(String.valueOf(rook.getId())));
                        boardUI.add(imageViewSet.get(String.valueOf(rook.getId())), rook.getPosition().col(), rook.getPosition().row());
                        pawnSelected.setEnPassant(false);
                    }
                    boardUI.getChildren().remove(imageViewSet.get(String.valueOf(pawnSelected.getId())));
                    boardUI.add(imageViewSet.get(String.valueOf(pawnSelected.getId())), colIndex, rowIndex);
                }
                pawnSelected=null;
            }
         }
    }


    public static void main(String[] args) {
        launch();
    }


}
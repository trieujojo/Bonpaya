package com.example.bonpaya;

import Logic.GameLogic;
import Logic.Parameters;
import Logic.Piece.ChessPiece;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

    private GameLogic gameLogic;

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
        homeScene= new Scene(homePane ,800, 480);
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
            imageView.setFitWidth(800);
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
        gamePane = new BorderPane();
        gameScene = new Scene(gamePane,800, 480 );
        boardUI= new GridPane();
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
        backButton.setOnAction(this::backButtonHandler);
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

    private void chessPieceView(ChessPiece cp, String color){
        try{
            Image image = new Image(this.getClass().getResource("/Images/"+cp.getName()+color+".png").toExternalForm(),30,30,false, false);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(Logic.Parameters.getScreenHeight()/8-5);
            imageView.setFitWidth(Logic.Parameters.getScreenWidth()/10);
            boardUI.add(imageView,cp.getPosition()[1],cp.getPosition()[0]);
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


    public static void main(String[] args) {
        launch();
    }


}
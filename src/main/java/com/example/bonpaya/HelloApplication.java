package com.example.bonpaya;
import javafx.application.Application;
import javafx.stage.Stage;




public class HelloApplication extends Application {
    private Stage mainStage;

    @Override
    public void start(Stage stage) {
        this.mainStage =stage;
        HelloController controller = new HelloController();
        controller.setApp(this);
        controller.initUI(stage);
        stage.setScene(controller.getHomeScene());
        stage.show();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch();
    }

}
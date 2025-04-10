package com.example.AutkaFX;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        CarShowroomGUI carShowroomUI = new CarShowroomGUI();
        carShowroomUI.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

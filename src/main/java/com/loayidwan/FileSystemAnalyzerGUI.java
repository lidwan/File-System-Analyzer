package com.loayidwan;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FileSystemAnalyzerGUI extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(new Label("JavaFX is working!"));
        Scene scene = new Scene(root, 320, 240);
        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

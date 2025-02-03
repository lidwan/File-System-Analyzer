package com.loayidwan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FileSystemAnalyzerGUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/welcomeScene.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("File System Analyzer | Loay Idwan");
        String cssFile = getClass().getResource("/FileSystemAnalyzerStyles.css").toExternalForm();
        scene.getStylesheets().add(cssFile);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

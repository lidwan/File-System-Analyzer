package com.loayidwan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


import java.io.IOException;

public class WelcomeSceneController {

    public Hyperlink githubLink;
    public Hyperlink wevsiteLink;
    public Hyperlink blogLink;

    public void switchToScene2(javafx.event.ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filePickerScene.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Prints exact error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading scene: " + e.getMessage());
            alert.showAndWait();
        }

    }

    public void handelGithubLink(ActionEvent actionEvent) {
    }

    public void handelBlogLink(ActionEvent actionEvent) {
    }

    public void handelWebsiteLink(ActionEvent actionEvent) {
    }
}

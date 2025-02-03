package com.loayidwan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FilePickerSceneController {
    @FXML
    private Label directoryLabel;
    private String dirName;

    @FXML
    private void openDirectoryPicker() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");

        // Open the directory chooser
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        // Check if a directory was selected
        if (selectedDirectory != null) {
            dirName = selectedDirectory.getName();
            directoryLabel.setText(directoryLabel.getText() +selectedDirectory+ "\"");

        } else {
            directoryLabel.setText("No directory selected.");
        }
    }

    public void switchToScene3(javafx.event.ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/processingDirectoryScene.fxml"));
            Parent root = loader.load();

            ProcessingDirectorySceneController processingDirectorySceneController = loader.getController();
            processingDirectorySceneController.setProcessingDirTitle(dirName);

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
}

package com.loayidwan;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FilePickerSceneController {
    @FXML
    private Label directoryLabel;

    @FXML
    private void openDirectoryPicker() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");

        // Open the directory chooser
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        // Check if a directory was selected
        if (selectedDirectory != null) {
//            selectedDirectory.getName();
            directoryLabel.setText(directoryLabel.getText() +selectedDirectory+ "\"");

        } else {
            directoryLabel.setText("No directory selected.");
        }
    }
}

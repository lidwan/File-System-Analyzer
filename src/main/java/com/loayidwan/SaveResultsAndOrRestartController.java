package com.loayidwan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;


public class SaveResultsAndOrRestartController {

    @FXML
    private Label saveFileLocation;

    @FXML
    private String dirName;

    @FXML
    public void setDirName(String absulotePathOfDir) {
        dirName = Paths.get(absulotePathOfDir).getFileName().toString();
    }

    @FXML
    private void openDirectoryPicker() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");

        //sets the default directory the directory chooser opens in to user home folder
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Open the directory chooser
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        // Check if a directory was selected
        if (selectedDirectory != null) {
            String absulotePathOfDir = selectedDirectory.getAbsolutePath();
            copyFile(absulotePathOfDir);
        } else {
            saveFileLocation.setText("No directory selected.");
            saveFileLocation.setStyle("-fx-text-fill: red");
        }
    }

    public void copyFile(String dir) {
        Path sourcePath = Path.of("fileAnalysisResults.txt");
        Path path = Path.of(dir, "File-Analysis-Results-On-"+dirName+".txt");
        boolean proceed = true;
        if (Files.exists(path)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "A file with the same name already exists\nDo you want to overwrite the existing file?\n");
            Optional<ButtonType> x = alert.showAndWait();
            if (x.isPresent() && !x.get().equals(ButtonType.OK)) {
                proceed = false;
            }
        }
        if(proceed) {
            try {
                Files.copy(sourcePath, path, StandardCopyOption.REPLACE_EXISTING);
                if (Files.exists(path)) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "File is now saved to the selected location");
                    alert1.setHeaderText("File Saved!");
                    saveFileLocation.setText("File Saved to \n" + path);
                    saveFileLocation.setStyle("-fx-text-fill: green");
                    alert1.showAndWait();
                }
            } catch (IOException e) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR, "An error occurred while copying the file to the destination path.");
                alert1.showAndWait();
            }
        }
    }

    @FXML
    public void switchToScene2(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filePickerScene.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading scene: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

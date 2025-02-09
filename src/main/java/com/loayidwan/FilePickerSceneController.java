package com.loayidwan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class FilePickerSceneController {
    @FXML
    private Label directoryLabel;

    @FXML
    private CheckBox topTenFilesCheckBox;

    @FXML
    private CheckBox topTenExtensionsCheckBox;

    @FXML
    private CheckBox duplicateFilesCheckBox;

    @FXML
    private CheckBox totalFileCountAndDirSizeCheckBox;

    @FXML
    private CheckBox deleteDuplicateFilesCheckBox;

    private String dirName;
    private String absulotePathOfDir;
    private final int[] userChoiceForResultFile = new int[]{1,1,0,1,0};

    @FXML
    private void openDirectoryPicker() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Open the directory chooser
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        // Check if a directory was selected
        if (selectedDirectory != null) {
            dirName = selectedDirectory.getName();
            absulotePathOfDir = selectedDirectory.getAbsolutePath();
            directoryLabel.setText("Selected directory: \"" +selectedDirectory+ "\"");
            directoryLabel.setStyle("-fx-text-fill: green");
        } else {
            directoryLabel.setText("No directory selected.");
            directoryLabel.setStyle("-fx-text-fill: red");
        }
    }

    public void switchToScene3(javafx.event.ActionEvent event) {
        try {
            if (absulotePathOfDir != null && !(!duplicateFilesCheckBox.isSelected() && deleteDuplicateFilesCheckBox.isSelected())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/processingDirectoryScene.fxml"));
                Parent root = loader.load();
                ProcessingDirectorySceneController processingDirectorySceneController = loader.getController();

                //passing directory name to next scene
                processingDirectorySceneController.setProcessingDirTitle(dirName);

                //modify user choice if not default selected
                if (!topTenFilesCheckBox.isSelected()) {userChoiceForResultFile[0] = 0;}
                if (!topTenExtensionsCheckBox.isSelected()) {userChoiceForResultFile[1] = 0;}
                if (duplicateFilesCheckBox.isSelected()) {userChoiceForResultFile[2] = 1;}
                if (!totalFileCountAndDirSizeCheckBox.isSelected()) {userChoiceForResultFile[3] = 0;}
                if (deleteDuplicateFilesCheckBox.isSelected()) {userChoiceForResultFile[4] = 1;}

                //passing user choices to next scene
                processingDirectorySceneController.setUserChoice(userChoiceForResultFile);

                //passing the absolute path of dir to next scene
                processingDirectorySceneController.setabsulotePathOfDir(absulotePathOfDir);
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                //start scanning directory
                processingDirectorySceneController.startProcessingDirectory();
            } else if (absulotePathOfDir != null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "You did NOT choose a directory, Please choose a dir before attempting to start scanning ");
                alert.showAndWait();
            } else if ((!duplicateFilesCheckBox.isSelected() && deleteDuplicateFilesCheckBox.isSelected())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The program can't delete duplicate files if you turn off duplicate detection, if you wish to delete duplicates please turn on BOTH duplicate detection and deletion.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading scene: " + e.getMessage());
            alert.showAndWait();
        }

    }
}

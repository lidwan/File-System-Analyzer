package com.loayidwan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ProcessingDirectorySceneController {

    private int[] userChoiceForResultFile = new int[4];

    @FXML
    private ListView<String> fileListView;

    @FXML
    private Label processingDirTitle;

    private String absulotePathOfDir;

//    private ObservableList<String> fileList = FXCollections.observableArrayList();
//
//    @FXML
//    public void initialize() {
//        fileListView.setItems(fileList);
//    }

    public void setProcessingDirTitle(String x) {
        processingDirTitle.setText(processingDirTitle.getText() + x +"\"..");
    }

    public void setUserChoice(int[] array) {
        userChoiceForResultFile[0] = array[0];
        userChoiceForResultFile[1] = array[1];
        userChoiceForResultFile[2] = array[2];
        userChoiceForResultFile[3] = array[3];
    }

    public void startProcessingDirectory() {
        FileScanner fileScanner = new FileScanner(absulotePathOfDir);
        new Thread(() -> fileScanner.scan(userChoiceForResultFile)).start(); //starts the scan on a new thread to avoid weird GUI artifacts
    }

    public void setabsulotePathOfDir(String absulotePathOfDir) {
        this.absulotePathOfDir = absulotePathOfDir;
    }
}

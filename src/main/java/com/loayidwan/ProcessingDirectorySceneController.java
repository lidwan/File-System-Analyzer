package com.loayidwan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ProcessingDirectorySceneController {
    @FXML
    private ListView<String> fileListView;

    @FXML
    private Label processingDirTitle;

//    private ObservableList<String> fileList = FXCollections.observableArrayList();
//
//    @FXML
//    public void initialize() {
//        fileListView.setItems(fileList);
//    }

    public void setProcessingDirTitle(String x) {
        processingDirTitle.setText(processingDirTitle.getText() + x +"\"..");
    }
}

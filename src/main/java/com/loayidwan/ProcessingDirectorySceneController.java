package com.loayidwan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ProcessingDirectorySceneController {
    @FXML
    private ListView<String> fileListView;

    private ObservableList<String> fileList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fileListView.setItems(fileList);
    }
}

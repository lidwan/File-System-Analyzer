package com.loayidwan;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class ProcessingDirectorySceneController {

    private static final int MAX_ITEMS = 21;
    private int[] userChoiceForResultFile = new int[4];

    @FXML
    private Label processingDirTitle;

    private String absulotePathOfDir;

    @FXML
    private Stage stage;
    @FXML
    private Scene scene;


    public void setStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

    @FXML
    private ListView<String> fileListView;

    private final ObservableList<String> fileItems =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fileListView.setItems(fileItems);

        // Auto-scroll when new items are added
        fileItems.addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    int lastIndex = fileItems.size() - 1;
                    Platform.runLater(() -> {
                        if (fileItems.size() > MAX_ITEMS) {
                            int overflow = fileItems.size() - MAX_ITEMS;
                            fileItems.remove(0, overflow); // Remove oldest entries
                        }
                        fileListView.scrollTo(lastIndex);
                    });
                }
            }
        });
    }

    public void setProcessingDirTitle(String x) {
        processingDirTitle.setText(processingDirTitle.getText() + x +"\"..");
    }

    public void setUserChoice(int[] array) {
        userChoiceForResultFile[0] = array[0];
        userChoiceForResultFile[1] = array[1];
        userChoiceForResultFile[2] = array[2];
        userChoiceForResultFile[3] = array[3];
    }

    public void startProcessingDirectory() throws IOException {
        FileScanner fileScanner = new FileScanner(absulotePathOfDir);

        new Thread(() -> {
            //start scan on a new thread, UI remains responsive
            fileScanner.scan(userChoiceForResultFile, (fileName, size) -> {
                Platform.runLater(() -> {
                    // Format: "Processed: filename.txt - 1.23 MB"
                    String entry = String.format("Processed: %s - %s",
                            fileName,
                            FileUtils.humanReadableSize(size)
                    );
                    fileItems.add(entry);
                });
            });
            //runs after scan is complete and user clicks ok
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Results File was created.\nYou can choose where to save it in the next page");
                alert.setHeaderText("Scan completed!");
                alert.showAndWait();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/saveResultsAndOrRestart.fxml"));
                    Parent root = loader.load();
                    SaveResultsAndOrRestartController saveResultsAndOrRestartController = loader.getController();
                    saveResultsAndOrRestartController.setDirName(absulotePathOfDir);
                    switchToScene3(loader, root);
                } catch (Exception e) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert1.showAndWait();
                    throw new RuntimeException(e);
                }
            });

        }).start();
    }

    public void setabsulotePathOfDir(String absulotePathOfDir) {
        this.absulotePathOfDir = absulotePathOfDir;
    }

    public void switchToScene3(FXMLLoader loader, Parent root) throws IOException {
        try {

            if (stage == null) {
                stage = (Stage) Stage.getWindows().get(0);
            }

            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Prints exact error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading scene: " + e.getMessage());
            alert.showAndWait();
        }

    }
}

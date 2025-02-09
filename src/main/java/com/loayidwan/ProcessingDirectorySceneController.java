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
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ProcessingDirectorySceneController {
    @FXML
    private Label processingDirTitle;

    @FXML
    private Stage stage;

    @FXML
    private Scene scene;

    @FXML
    private ListView<String> fileListView;

    @FXML
    ProgressBar progressBar;


    private static final int MAX_ITEMS = 21;
    private final int[] userChoiceForResultFile = new int[5];
    private String absulotePathOfDir;
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
                            fileItems.remove(0, overflow); // Removes oldest entries
                        }
                        fileListView.scrollTo(lastIndex);
                    });
                }
            }
        });
    }

    //Sets the Scene title to Processing "DIRECTORY".
    public void setProcessingDirTitle(String x) {
        processingDirTitle.setText(processingDirTitle.getText() + x +"\"..");
    }

    public void setUserChoice(int[] array) {
        userChoiceForResultFile[0] = array[0];
        userChoiceForResultFile[1] = array[1];
        userChoiceForResultFile[2] = array[2];
        userChoiceForResultFile[3] = array[3];
        userChoiceForResultFile[4] = array[4];
    }

    public void startProcessingDirectory() {
        FileScanner fileScanner = new FileScanner(absulotePathOfDir);
        progressBar.setProgress(-1);
        new Thread(() -> {

            //start scan on a new thread, UI remains responsive
            boolean noErrors = fileScanner.scan(userChoiceForResultFile, (fileName, _) -> Platform.runLater(() -> {
                // Format: "Processed: filename.txt"
                String entry = String.format("Processed: %s",fileName);
                fileItems.add(entry);
            }));
            if (noErrors) {
                //runs after scan is complete (and is successful) and user clicks ok
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
                        saveResultsAndOrRestartController.setTotalNumOfFiles(fileScanner.getFileStats().getNumberOfFiles());
                        saveResultsAndOrRestartController.setTotalDirSizeLabel(fileScanner.getFileStats().getTotalDictSize());

                        HBox bottomHBox = saveResultsAndOrRestartController.getBottomHBox();

                        if (userChoiceForResultFile[4] == 1 && userChoiceForResultFile[2] == 1){
                            bottomHBox.getChildren().add(new Label("Total space saved by deleting duplicates: " +
                                    FileUtils.humanReadableSize(fileScanner.getFileStats().getTotalDuplicateFilesSize().longValue())));
                        }
                        else if (userChoiceForResultFile[4] == 0 && userChoiceForResultFile[2] == 1) {
                            bottomHBox.getChildren().add(new Label("Total space you could save by deleting duplicate files: " +
                                    FileUtils.humanReadableSize(fileScanner.getFileStats().getTotalDuplicateFilesSize().longValue())));
                        }
                        saveResultsAndOrRestartController.initializeChart(fileScanner.getFileStats().getTopTenExtCommon(),
                                fileScanner.getFileStats().getTotalDictSize());

                        VBox vBox1 = saveResultsAndOrRestartController.getvBox1();
                        VBox vBox2 = saveResultsAndOrRestartController.getvBox2();

                        AtomicInteger tmpCounter = new AtomicInteger(1);

                        //dynamically adding the top ten files in size
                        fileScanner.getFileStats().getTopTenFileSizes().forEach((entry) -> {
                            if (tmpCounter.get() <= 5) {
                                vBox1.getChildren().add(new Label(tmpCounter + "- "+ entry.getKey().getFileName() +
                                        " | " + FileUtils.humanReadableSize(entry.getValue())));
                                tmpCounter.getAndIncrement();
                            }
                            else {
                                vBox2.getChildren().add(new Label(tmpCounter + "- "+ entry.getKey().getFileName() +
                                        " | " + FileUtils.humanReadableSize(entry.getValue())));
                                tmpCounter.getAndIncrement();
                            }
                        });
                        switchToScene3(root);
                    } catch (Exception e) {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR, e.getMessage());
                        alert1.showAndWait();
                        throw new RuntimeException(e);
                    }
                });
            }
            // On completion, make progress bar 100%
            Platform.runLater(() -> progressBar.setProgress(1.0));
        }).start();
    }

    public void setabsulotePathOfDir(String absulotePathOfDir) {
        this.absulotePathOfDir = absulotePathOfDir;
    }

    public void switchToScene3(Parent root) {
        try {
            if (stage == null) {
                stage = (Stage) Stage.getWindows().getFirst();
            }
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading scene: " + e.getMessage());
            alert.showAndWait();
        }

    }
}

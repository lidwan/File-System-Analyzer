package com.loayidwan;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Stream;


public class FileScanner {
    private final String dictPath;

    // Thread count used for multithreading
    // Uses all available resources on the machine
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    public FileScanner(String dictPath){
        this.dictPath = dictPath;
    }

    //Scans for every file in "dictPath", making each file a task submitted to the executor.
    //Waits for all tasks to complete, then makes the results file and alerts the user that the
    //results file was created.
    public boolean scan(int[] userChoiceForResultFile, BiConsumer<String, Long> onFileProcessed) {
        try {
            FileStats fileStats = new FileStats();

            //makes a fixed number of threads that can be used in parallel
            try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {

                //for each path on dictPath, it's submmited to the executor for processing when a thread is free
                try (Stream<Path> paths = Files.walk(Paths.get(dictPath))) {
                    paths.filter(Files::isRegularFile)
                            .forEach(filePath -> executor.submit(() -> {
                                try {
                                    long size = Files.size(filePath);
                                    fileStats.addFile(filePath, size, userChoiceForResultFile);

                                    //Once processed pass filePath and fileSize to UI for update
                                    onFileProcessed.accept(filePath.toString(), size);

                                } catch (IOException | NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                } catch (UncheckedIOException e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "You don't have access to read files:, \nrun the program with elevated permissions if you wish to continue ");
                        alert.setHeaderText("Access denied!");
                        alert.showAndWait();
                    });
                    return false;
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                        alert.showAndWait();
                    });
                    return false;
                }

                // Stop accepting new tasks
                executor.shutdown();
                try {
                    // Wait indefinitely for existing tasks to finish
                    //noinspection ResultOfMethodCallIgnored
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Task execution interrupted: " + e.getMessage());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                        alert.showAndWait();
                    });
                    return false;
                }
            }
            fileStats.makeResFile(dictPath, userChoiceForResultFile);
        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            });
            return false;
        }
        return true;
    }
}

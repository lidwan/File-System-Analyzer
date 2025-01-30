package com.loayidwan;

import java.io.IOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class FileScanner {
    private final String dictPath;
    private static final int THREAD_COUNT = 4; // Thread count used for multithreading

    public FileScanner(String dictPath){
        this.dictPath = dictPath;
    }

    //Scans for every file in "dictPath", making each file a task submitted to the executor.
    //Waits for all tasks to complete, then makes the results file.
    public void scan() {
        try {
            FileStats fileStats = new FileStats();
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT); //makes a fixed number of threads (4 in this case) that can be use in parallel

            try (Stream<Path> paths = Files.walk(Paths.get(dictPath))) {
                paths.filter(Files::isRegularFile)
                        .forEach(filePath -> executor.submit(() -> {
                            try {
                                fileStats.addFile(filePath);
                            } catch (IOException | NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                        }));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Stop accepting new tasks
            executor.shutdown();
            try {
                // Wait indefinitely for existing tasks to finish
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task execution interrupted: " + e.getMessage());
            }

            //tmp ofc, just for dev.
            fileStats.makeResFile(dictPath);
        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        }

    }
}

package com.loayidwan;

import java.io.IOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;


public class FileScanner {
    private final String dictPath;

    public FileScanner(String dictPath){
        this.dictPath = dictPath;
    }

    public void scan() {
        try {
            FileStats fileStats = new FileStats();

            Files.walk(Paths.get(dictPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        fileStats.addFile(filePath);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            //tmp ofc, just for dev.
            fileStats.makeResFile(dictPath);

        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        }

    }
}

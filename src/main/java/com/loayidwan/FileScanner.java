package com.loayidwan;

import java.io.IOException;
import java.nio.file.*;

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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println(fileStats);
            System.out.println("Total dict size: "+fileStats.getTotalDictSize());

        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        }

    }
}

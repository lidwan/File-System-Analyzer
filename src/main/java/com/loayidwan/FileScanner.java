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
            Files.walk(Paths.get(dictPath)).forEach(file -> {
                try {
                    if (Files.isRegularFile(file)) {
                        long size = Files.size(file);
                        System.out.println("Size of file "+file+" "+size/1024+" KB");
                        }
                }
                catch (IOException e) {
                    System.err.println("Error reading file: " + file);
                }
            });

        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        }

    }
}

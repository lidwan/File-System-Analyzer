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

            Files.walk(Paths.get(dictPath)).forEach(file -> {
                try {
                    if (Files.isRegularFile(file)) {
                        long size = Files.size(file);
                        fileStats.addFile(file.toAbsolutePath().toString(), size);
                        System.out.println(fileStats.getFileNameOnly(file.toString()));
                    }
                }
                catch (IOException e) {
                    System.err.println("Error reading file: " + file);
                }
            });
            System.out.println(fileStats);
            System.out.println("Total dict size: "+fileStats.getTotalDictSize());
            System.out.println("Top ten files and their sizes");
            fileStats.getTopTenFileSizes();
            System.out.println("Top ten Ext. and their sizes");
            fileStats.getTopTenExtSizes();
        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        }

    }
}

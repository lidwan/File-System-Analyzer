package com.loayidwan;

import java.io.IOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


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
                    } catch (SecurityException e){
                        System.err.println(e.getMessage());
                    }
                }
            });

            //tmp ofc, just for dev.
            fileStats.makeResFile(dictPath);
            System.out.println("Do you want to delete duplicate files? (y, n)");
            Scanner scanner = new Scanner(System.in);
            String deleteduplFilesAnswer = "";
            
            while(deleteduplFilesAnswer.isEmpty())
                deleteduplFilesAnswer = scanner.nextLine();
            
            if (deleteduplFilesAnswer.toLowerCase().charAt(0) == 'y'){
                try {
                    fileStats.deleteDuplFiles();
                } catch (SecurityException e) {
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                fileStats.getDeletedFiles();
            }
        } catch (Exception e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        }

    }
}

package com.loayidwan;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


public class FileSystemAnalyzer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose dir to analyze (Press Enter for default 'src/main/resources'): ");

        String dict = scanner.nextLine();
        if (dict.isEmpty())
            dict = "src/main/resources";

        if (Files.isDirectory(Path.of(dict))){
            FileScanner fileScanner = new FileScanner(dict);
            fileScanner.scan();
        }
        else {
            System.err.println("String you provided is NOT a directory");
        }
    }
}
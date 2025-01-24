package com.loayidwan;

public class FileSystemAnalyzer {
    public static void main(String[] args) {
        FileScanner scanner = new FileScanner("src/main/resources");
        scanner.scan();
    }
}
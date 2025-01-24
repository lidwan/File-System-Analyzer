package com.loayidwan;

public class FileUtils {
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? "unknown" : fileName.substring(lastDotIndex + 1);
    }
}

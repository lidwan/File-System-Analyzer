package com.loayidwan;
import org.apache.commons.io.FilenameUtils;

public class FileUtils {
    public static String getFileExtension(String fileName) {
        String ext = FilenameUtils.getExtension(fileName);

        return ext.isEmpty() ? "Unknown" : ext;
    }
}

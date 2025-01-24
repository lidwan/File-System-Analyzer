package com.loayidwan;

import java.util.HashMap;
import java.util.Map;

public class FileStats {
    private final Map<String, Long> fileNameToSizeMap;
    private final Map<String, Long> extensionToSizeMap;

    public FileStats() {
        this.fileNameToSizeMap = new HashMap<>();
        this.extensionToSizeMap = new HashMap<>();
    }

    public void addFile(String fileName, long size){
        String extension = FileUtils.getFileExtension(fileName);

        fileNameToSizeMap.put(fileName, size);
        extensionToSizeMap.put(extension, extensionToSizeMap.getOrDefault(extension,0L)+size);
    }

    public Map<String, Long> getFileNameToSizeMap() {
        return fileNameToSizeMap;
    }

    public Map<String, Long> getExtensionToSizeMap() {
        return extensionToSizeMap;
    }

    @Override
    public String toString() {
        return "FileStats{" +
                "fileNameToSizeMap=" + fileNameToSizeMap +
                ", extensionToSizeMap=" + extensionToSizeMap +
                '}';
    }
}

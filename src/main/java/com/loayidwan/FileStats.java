package com.loayidwan;

import java.nio.file.Path;
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

    public String getFileNameOnly(String fileName){
        return "File name: "+fileName.substring(fileName.lastIndexOf("/")+1);
    }

    public long getTotalDictSize(){
        final long[] totalSize = {0};
        extensionToSizeMap.forEach((extention, size) -> {
            totalSize[0]+=size;
        });
        return totalSize[0];
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

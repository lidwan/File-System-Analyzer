package com.loayidwan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.*;
import java.io.File;


public class FileStats {
    private final Map<String, Long> fileNameToSizeMap;
    private final Map<String, Long> extensionToSizeMap;
    private final Map<String, Long> hashCodeToFileNameMap;

    public FileStats() {
        this.fileNameToSizeMap = new HashMap<>();
        this.extensionToSizeMap = new HashMap<>();
        this.hashCodeToFileNameMap = new HashMap<>();
    }

    public void addFile(Path filePath) throws IOException {
        long size = Files.size(filePath);
        String extension = FileUtils.getFileExtension(filePath.toString());

        fileNameToSizeMap.put(filePath.toString(), size);
        extensionToSizeMap.put(extension, extensionToSizeMap.getOrDefault(extension,0L)+size);
    }

    public String getFileNameOnly(String fileName){
        return "File name: "+fileName.substring(fileName.lastIndexOf("/")+1);
    }

    public long getTotalDictSize(){
        final long[] totalSize = {0};
        extensionToSizeMap.forEach((_, size) -> totalSize[0]+=size);
        return totalSize[0];
    }

    public List<Map.Entry<String, Long>> getTopTenFileSizes(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(fileNameToSizeMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()));

        return sortedEntries.subList(0, Math.min(10, sortedEntries.size()));
    }

    public List<Map.Entry<String, Long>> getTopTenExtSizes(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(extensionToSizeMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()));

        return sortedEntries.subList(0, Math.min(10, sortedEntries.size()));
    }

    @Override
    public String toString() {
        return "FileStats{" +
                "fileNameToSizeMap=" + fileNameToSizeMap +
                ", extensionToSizeMap=" + extensionToSizeMap +
                '}';
    }
}

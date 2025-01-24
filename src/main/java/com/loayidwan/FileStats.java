package com.loayidwan;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        extensionToSizeMap.forEach((_, size) -> {
            totalSize[0]+=size;
        });
        return totalSize[0];
    }

    public void getTopTenFileSizes(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(fileNameToSizeMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()));

        List<Map.Entry<String, Long>> top10 = sortedEntries.subList(0, Math.min(10, sortedEntries.size()));

        for (Map.Entry<String, Long> entry : top10) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    public void getTopTenExtSizes(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(extensionToSizeMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()));

        List<Map.Entry<String, Long>> top10 = sortedEntries.subList(0, Math.min(10, sortedEntries.size()));

        for (Map.Entry<String, Long> entry : top10) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
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

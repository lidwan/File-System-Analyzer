package com.loayidwan;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.*;


public class FileStats {
    private final Map<String, Long> fileNameToSizeMap;
    private final Map<String, Long> extensionToSizeMap;
    private final Map<String, String> hashCodeToFileNameMap;
    private final Map<String, List<String>> duplicateFilesMap;

    public FileStats() {
        this.fileNameToSizeMap = new HashMap<>();
        this.extensionToSizeMap = new HashMap<>();
        this.hashCodeToFileNameMap = new HashMap<>();
        this.duplicateFilesMap = new HashMap<>();
    }

    public void addFile(Path filePath) throws IOException, NoSuchAlgorithmException {
        long size = Files.size(filePath);
        String extension = FileUtils.getFileExtension(filePath.toString());

        fileNameToSizeMap.put(filePath.toAbsolutePath().toString(), size);
        extensionToSizeMap.put(extension, extensionToSizeMap.getOrDefault(extension,0L)+size);

        String hashCode = FileUtils.calcHashCode(filePath);
        if (hashCodeToFileNameMap.containsKey(hashCode)) { //duplicates detected
            //adds new (current) file to the list
            duplicateFilesMap
                    .computeIfAbsent(hashCode, _ -> new ArrayList<>())
                    .add(filePath.toAbsolutePath().toString());

            //also adds original file to list
            duplicateFilesMap
                    .get(hashCode)
                    .add(hashCodeToFileNameMap.get(hashCode));
        }
        else {
            hashCodeToFileNameMap.put(hashCode, filePath.toAbsolutePath().toString());
        }
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
                "fileNameToSizeMap=\n" + fileNameToSizeMap +
                ", extensionToSizeMap=\n" + extensionToSizeMap +
                ", hashCodeToFileNameMap=\n" + hashCodeToFileNameMap +
                ", duplicateFilesMap=\n" + duplicateFilesMap +
                '}';
    }
}

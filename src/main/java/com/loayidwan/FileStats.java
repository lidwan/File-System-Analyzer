package com.loayidwan;

import java.io.File;
import java.io.FileWriter;
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

    public void makeResFile(String dictPath){
        try {
            File file = new File("fileAnalysisResults.txt");

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }

            // Step 3: Write to the file
            FileWriter writer = new FileWriter(file);
            writer.write("Result for scan on "+dictPath+": \n"+this.toString());
            writer.close();

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }
    public void deleteDuplFiles(){
        duplicateFilesMap.forEach((_, dupsList) ->{
            dupsList.forEach((filePathString) ->{
                try {
                    if (Files.deleteIfExists(Path.of(filePathString))){
                        deletedFilesList.add(filePathString);
                    }
                    else {
                        System.err.println("File "+filePathString+ " was deleted or moved before the program could deleted it.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e){
                    throw  new SecurityException(e);
                }
            });
        });
    }
    public List<Map.Entry<String, Long>> getTopTenFileSizes(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(fileNameToSizeMap.entrySet());
        sortedEntries.sort((entry1, entry2) ->
                Long.compare(entry2.getValue(), entry1.getValue()));

        return sortedEntries.subList(0, Math.min(10, sortedEntries.size()));
    }

    public List<Map.Entry<String, Long>> getTopTenExtSizes(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(extensionToSizeMap.entrySet());
        sortedEntries.sort((entry1, entry2) ->
                Long.compare(entry2.getValue(), entry1.getValue()));

        return sortedEntries.subList(0, Math.min(10, sortedEntries.size()));
    }

    @Override
    public String toString() {
        return "FileStats{" +
                "\n1- Each file in dict. and its size= " + fileNameToSizeMap +
                "\n2- Each extension in  dict. and the total size of all files using the extension = " + extensionToSizeMap +
                "\n3- Each hashcode (hashcode included only if there are duplicates) and a list of all files with the same hashcode = " + duplicateFilesMap +
                "\n4- Top 10 Files in size and their sizes = " + getTopTenFileSizes() +
                "\n5- Top 10 Extensions in size and their total sizes = " + getTopTenExtSizes() +
                "\n6- Total dict size = " + getTotalDictSize() +
                "\nNote: all sizes are in bytes"+
                '}';
    }
}

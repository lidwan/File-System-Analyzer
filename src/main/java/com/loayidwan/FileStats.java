package com.loayidwan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;


public class FileStats {
    private final ConcurrentHashMap<Path, Long> fileNameToSizeMap;
    private final ConcurrentHashMap<String, Long> extensionToSizeMap;
    private final ConcurrentHashMap<String, String> hashCodeToFileNameMap;
    private final ConcurrentHashMap<String, List<Path>> duplicateFilesMap;
    private final ConcurrentHashMap<String, Long> commonExtensionsGroupedToSize;
    private static ConcurrentHashMap<String, List<String>> commonExtensionsGrouped;

    private final LongAdder totalDictSize;


    public FileStats() {
        this.fileNameToSizeMap = new ConcurrentHashMap<>();
        this.extensionToSizeMap = new ConcurrentHashMap<>();
        this.hashCodeToFileNameMap = new ConcurrentHashMap<>();
        this.duplicateFilesMap = new ConcurrentHashMap<>();
        this.commonExtensionsGroupedToSize = new ConcurrentHashMap<>();
        commonExtensionsGrouped = new ConcurrentHashMap<>();
        this.totalDictSize = new LongAdder();

        //Thx to AI, this didn't take forever to compile.
        //This is common extension types and what they usually mean so that the program can output extensions in a humanly readable way
        commonExtensionsGrouped.put("Images", Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp", "svg", "ico", "raw"));
        commonExtensionsGrouped.put("Documents", Arrays.asList("pdf", "doc", "docx", "txt", "rtf", "odt", "xls", "xlsx", "ppt", "md", "tex", "epub", "pages", "numbers", "key"));
        commonExtensionsGrouped.put("Videos", Arrays.asList("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "mpeg", "3gp", "vob", "m4v", "ogv"));
        commonExtensionsGrouped.put("Audio", Arrays.asList("mp3", "wav", "aac", "flac", "ogg", "wma", "m4a", "aiff", "opus", "midi"));
        commonExtensionsGrouped.put("Archives", Arrays.asList("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso", "dmg", "pkg"));
        commonExtensionsGrouped.put("Executables", Arrays.asList("exe", "msi", "dmg", "apk", "app", "bat", "sh", "jar", "bin", "deb", "rpm"));
        commonExtensionsGrouped.put("Code Files", Arrays.asList("java", "py", "c", "cpp", "js", "html", "css", "php", "rb", "go", "swift", "kt", "ts", "sh", "pl", "lua", "sql", "json", "xml", "yml", "yaml", "asp"));
        commonExtensionsGrouped.put("Databases", Arrays.asList("sql", "db", "sqlite", "mdb", "accdb", "dbf", "csv", "json", "xml"));
        commonExtensionsGrouped.put("Fonts", Arrays.asList("ttf", "otf", "woff", "woff2", "eot", "fon"));
        commonExtensionsGrouped.put("System Files", Arrays.asList("dll", "sys", "ini", "cfg", "log", "bak", "tmp", "dat", "inf", "reg"));
        commonExtensionsGrouped.put("3D Models", Arrays.asList("obj", "fbx", "stl", "dae", "blend", "3ds", "max", "ma", "mb"));
        commonExtensionsGrouped.put("Virtual Machines", Arrays.asList("vmdk", "vdi", "vhd", "ova", "ovf", "qcow2"));
        commonExtensionsGrouped.put("E-books", Arrays.asList("epub", "mobi", "azw", "azw3", "fb2", "ibooks"));
        commonExtensionsGrouped.put("Spreadsheets", Arrays.asList("xls", "xlsx", "ods", "csv", "numbers"));
        commonExtensionsGrouped.put("Presentations", Arrays.asList("ppt", "pptx", "odp", "key"));
        commonExtensionsGrouped.put("Configuration Files", Arrays.asList("ini", "cfg", "conf", "yml", "yaml", "xml", "properties"));
        commonExtensionsGrouped.put("Backup Files", Arrays.asList("bak", "old", "tmp", "arc", "tar", "gz"));
        commonExtensionsGrouped.put("Disk Images", Arrays.asList("iso", "img", "dmg"));
    }
    
    //This method takes in a file Path and adds the file path (as a string) and the file size to fileNameToSizeMap.
    //Also calls getFileExtension to get the extension of the file and adds it and the file size to extensionToSizeMap.
    //Then the method calls handleDuplicateFiles and handleExtensions to handle duplicate files and extensions respectively.
    public void addFile(Path filePath) throws IOException, NoSuchAlgorithmException {
        long size = Files.size(filePath);
        String extension = FileUtils.getFileExtension(filePath.toString());

        fileNameToSizeMap.put(filePath, size);
        extensionToSizeMap.merge(extension, size, Long::sum);

        totalDictSize.add(size);

        handleDuplicateFiles(filePath);
        handleExtensions(extension, size);
    }

    // This method calls calcHashCode to get the hashcode of the file, then tries to add the file as the
    // original file to duplicateFilesMap, it wasn't the original file for this hashcode then
    // duplicates are detected, then if this is the first duplicate file for this hashcode a list is initialized
    // and the original file is added in the list, then the current file is added.
    private void handleDuplicateFiles(Path filePath) throws NoSuchAlgorithmException {
        String hashCode = FileUtils.calcHashCode(filePath);
        String currentFilePath = filePath.toAbsolutePath().toString();

        String existingFilePath = hashCodeToFileNameMap.putIfAbsent(hashCode, currentFilePath);

        if (existingFilePath != null) {
            duplicateFilesMap.computeIfAbsent(hashCode, k -> {
                // happens once per hash
                List<Path> list = new ArrayList<>();
                list.add(Path.of(existingFilePath));
                return list;
            }).add(Path.of(currentFilePath)); // Add the current duplicate
        }
    }

    //This method search in the common extensions looking to for the first cataloger of extension types to
    //match "extension", if it finds "extension" in any category it adds it and its file size to
    //commonExtensionsGroupedToSize and terminates.
    //If "extension" isn't found it adds "Unknown" and the file size to commonExtensionsGroupedToSize.
    private void handleExtensions(String extension, long size) {
        boolean found = false;

        for (Map.Entry<String, List<String>> entry : commonExtensionsGrouped.entrySet()) {
            if (entry.getValue().contains(extension)) {
                commonExtensionsGroupedToSize.merge(entry.getKey(), size, Long::sum);
                found = true;
                break;
            }
        }

        if (!found) {
            commonExtensionsGroupedToSize.merge("Unknown", size, Long::sum);
        }
    }

    public String getFileNameOnly(String fileName){
        return "File name: "+fileName.substring(fileName.lastIndexOf("/")+1);
    }

    public long getTotalDictSize(){
        return totalDictSize.sum();
    }

    public void makeResFile(String dictPath){
        try {
            File file = new File("fileAnalysisResults.txt");

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter writer = new FileWriter(file);
            writer.write("Result for scan on "+dictPath+": \n"+this.toString());
            writer.close();

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }


    public List<Map.Entry<Path, Long>> getTopTenFileSizes(){
        List<Map.Entry<Path, Long>> sortedEntries = new ArrayList<>(fileNameToSizeMap.entrySet());
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
    public List<Map.Entry<String, Long>> getTopTenExtCommon(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(commonExtensionsGroupedToSize.entrySet());
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
                "\n7- Each common extension and its size "+ commonExtensionsGroupedToSize +
                "\n8- Top 10 grouped common extension and their sizes: "+ getTopTenExtCommon() +
                "\nNote: all sizes are in bytes"+
                '}';
    }
}

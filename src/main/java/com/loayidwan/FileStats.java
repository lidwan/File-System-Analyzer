package com.loayidwan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;


public class FileStats {
    private final ConcurrentHashMap<String, Long> fileNameToSizeMap;
    private final ConcurrentHashMap<String, Long> extensionToSizeMap;
    private final ConcurrentHashMap<String, String> hashCodeToFileNameMap;
    private final ConcurrentHashMap<String, List<String>> duplicateFilesMap;
    private final List<String> deletedFilesList;
    private final ConcurrentHashMap<String, Long> commonExtensionsGroupedToSize;
    private static ConcurrentHashMap<String, List<String>> commonExtensionsGrouped;


    public FileStats() {
        this.fileNameToSizeMap = new ConcurrentHashMap<>();
        this.extensionToSizeMap = new ConcurrentHashMap<>();
        this.hashCodeToFileNameMap = new ConcurrentHashMap<>();
        this.duplicateFilesMap = new ConcurrentHashMap<>();
        this.deletedFilesList = new ArrayList<>();
        this.commonExtensionsGroupedToSize = new ConcurrentHashMap<>();
        commonExtensionsGrouped = new ConcurrentHashMap<>();

        //Thx to AI, this didn't take forever to compile.
        commonExtensionsGrouped.put("Images", Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp", "svg", "ico", "raw"));
        commonExtensionsGrouped.put("Documents", Arrays.asList("pdf", "doc", "docx", "txt", "rtf", "odt", "xls", "xlsx", "ppt", "pptx", "csv", "md", "tex", "epub", "pages", "numbers", "key"));
        commonExtensionsGrouped.put("Videos", Arrays.asList("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "mpeg", "3gp", "vob", "m4v", "ogv"));
        commonExtensionsGrouped.put("Audio", Arrays.asList("mp3", "wav", "aac", "flac", "ogg", "wma", "m4a", "aiff", "opus", "midi"));
        commonExtensionsGrouped.put("Archives", Arrays.asList("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso", "dmg", "pkg"));
        commonExtensionsGrouped.put("Executables", Arrays.asList("exe", "msi", "dmg", "apk", "app", "bat", "sh", "jar", "bin", "deb", "rpm"));
        commonExtensionsGrouped.put("Code Files", Arrays.asList("java", "py", "c", "cpp", "js", "html", "css", "php", "rb", "go", "swift", "kt", "ts", "sh", "pl", "lua", "sql", "json", "xml", "yml", "yaml"));
        commonExtensionsGrouped.put("Databases", Arrays.asList("sql", "db", "sqlite", "mdb", "accdb", "dbf", "csv", "json", "xml"));
        commonExtensionsGrouped.put("Fonts", Arrays.asList("ttf", "otf", "woff", "woff2", "eot", "fon"));
        commonExtensionsGrouped.put("System Files", Arrays.asList("dll", "sys", "ini", "cfg", "log", "bak", "tmp", "dat", "inf", "reg"));
        commonExtensionsGrouped.put("3D Models", Arrays.asList("obj", "fbx", "stl", "dae", "blend", "3ds", "max", "ma", "mb"));
        commonExtensionsGrouped.put("Virtual Machines", Arrays.asList("vmdk", "vdi", "vhd", "ova", "ovf", "qcow2"));
        commonExtensionsGrouped.put("E-books", Arrays.asList("epub", "mobi", "azw", "azw3", "fb2", "ibooks"));
        commonExtensionsGrouped.put("Spreadsheets", Arrays.asList("xls", "xlsx", "ods", "csv", "numbers"));
        commonExtensionsGrouped.put("Presentations", Arrays.asList("ppt", "pptx", "odp", "key"));
        commonExtensionsGrouped.put("Configuration Files", Arrays.asList("ini", "cfg", "conf", "yml", "yaml", "json", "xml", "properties"));
        commonExtensionsGrouped.put("Backup Files", Arrays.asList("bak", "old", "tmp", "arc", "tar", "gz"));
        commonExtensionsGrouped.put("Disk Images", Arrays.asList("iso", "img", "dmg", "vhd", "vdi"));
        commonExtensionsGrouped.put("Web Files", Arrays.asList("html", "htm", "css", "js", "php", "asp", "jsp", "aspx", "xml", "json"));
    }

    public void addFile(Path filePath) throws IOException, NoSuchAlgorithmException {
        long size = Files.size(filePath);
        String extension = FileUtils.getFileExtension(filePath.toString());

        fileNameToSizeMap.put(filePath.toAbsolutePath().toString(), size);
        extensionToSizeMap.put(extension, extensionToSizeMap.getOrDefault(extension,0L)+size);

        handleDuplicateFiles(filePath);
        handleExtensions(extension, size);
    }

    //This method calls calcHashCode to get the hashcode of the file, then checks if that file exists in hashCodeToFileNameMap
    //AKA has this file been seen before? if it hasn't it adds it to hashCodeToFileNameMap, if it has, the method adds the file to
    //duplicateFilesMap and only if the original file (filePath) doesn't exist in duplicateFilesMap it adds the original file as well.
    private void handleDuplicateFiles(Path filePath) throws NoSuchAlgorithmException {
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

    private void handleExtensions(String extension, long size) {
        boolean found = false;

        for (Map.Entry<String, List<String>> entry : commonExtensionsGrouped.entrySet()) {
            if (entry.getValue().contains(extension)) {
                commonExtensionsGroupedToSize.put(entry.getKey(),
                        commonExtensionsGroupedToSize.getOrDefault(entry.getKey(), 0L) + size);
                found = true;
                break;
            }
        }

        if (!found) {
            commonExtensionsGroupedToSize.put("Unknown",
                    commonExtensionsGroupedToSize.getOrDefault("Unknown", 0L) + size);
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
    public List<Map.Entry<String, Long>> getTopTenExtCommon(){
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(commonExtensionsGroupedToSize.entrySet());
        sortedEntries.sort((entry1, entry2) ->
                Long.compare(entry2.getValue(), entry1.getValue()));

        return sortedEntries.subList(0, Math.min(10, sortedEntries.size()));
    }

    //tmp for dev
    public void getDeletedFiles() {
        for (String file : deletedFilesList) {
            System.out.println("Deleted file: "+file);
        }

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

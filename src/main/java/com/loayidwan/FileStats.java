package com.loayidwan;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;


public class FileStats {
    private final ConcurrentHashMap<Path, Long> fileNameToSizeMap;
    private final ConcurrentHashMap<String, String> hashCodeToFileNameMap;
    private final ConcurrentHashMap<String, List<Path>> duplicateFilesMap;
    private final ConcurrentHashMap<String, Long> commonExtensionsGroupedToSize;
    private static ConcurrentHashMap<String, List<String>> commonExtensionsGrouped;
    private final LongAdder totalDictSize;
    private final AtomicInteger numberOfFiles;
    private final LongAdder totalDuplicateFilesSize;


    public FileStats() {
        this.fileNameToSizeMap = new ConcurrentHashMap<>();
        this.hashCodeToFileNameMap = new ConcurrentHashMap<>();
        this.duplicateFilesMap = new ConcurrentHashMap<>();
        this.commonExtensionsGroupedToSize = new ConcurrentHashMap<>();
        commonExtensionsGrouped = new ConcurrentHashMap<>();
        this.totalDictSize = new LongAdder();
        numberOfFiles = new AtomicInteger(0);
        this.totalDuplicateFilesSize = new LongAdder();

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
    
    //This method takes in a file Path and adds the file path and the file size to fileNameToSizeMap.
    //Also calls getFileExtension to get the extension of the file.
    //Then the method calls handleDuplicateFiles and handleExtensions to handle duplicate files and
    //extensions respectively (only computes if user specified)
    public void addFile(Path filePath, long size, int[] userChoiceForResultFile) throws IOException, NoSuchAlgorithmException {
        if (userChoiceForResultFile[0] == 1)
            fileNameToSizeMap.put(filePath.toAbsolutePath(), size);
        if (userChoiceForResultFile[1] == 1){
            String extension = FileUtils.getFileExtension(filePath.toString());
            handleExtensions(extension, size);
        }
        if (userChoiceForResultFile[2] == 1)
            handleDuplicateFiles(filePath, size);
        if (userChoiceForResultFile[3] == 1) {
            totalDictSize.add(size);
            numberOfFiles.addAndGet(1);
        }
    }

    // This method calls calcHashCode to get the hashcode of the file, then tries to add the file as the
    // original file to duplicateFilesMap, it wasn't the original file for this hashcode then
    // duplicates are detected, then if this is the first duplicate file for this hashcode a list is initialized
    // and the original file is added in the list, then the current file is added.
    private void handleDuplicateFiles(Path filePath, long size) throws NoSuchAlgorithmException {
        String hashCode = FileUtils.calcHashCode(filePath);
        String currentFilePath = filePath.toAbsolutePath().toString();
        String existingFilePath = hashCodeToFileNameMap.putIfAbsent(hashCode, currentFilePath);
        if (existingFilePath != null) {
            duplicateFilesMap.computeIfAbsent(hashCode, _ -> {
                // Only happens once per hash
                List<Path> list = new ArrayList<>();
                list.add(Path.of(existingFilePath));
                return list;
            }).add(Path.of(currentFilePath)); // Add the current duplicate
            totalDuplicateFilesSize.add(size);
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

    //Makes the result file, which is customizable by the user in GUI, user choices are passed to the method
    //as an array (userChoiceForResultFile) of int, where each index represents a user choice
    //userChoiceForResultFile[0] is userChoiceForResultFile,
    //userChoiceForResultFile[1] is TopTenExtensions,
    //userChoiceForResultFile[2] is DuplicateFiles,
    //userChoiceForResultFile[3] is TotalNumberOfFiles and TotalDictSize.
    //userChoiceForResultFile[4] is duplicates deletion.
    //userChoiceForResultFile is initialized to all 1's representing including all sections.
    //The value 0 means the user asked for this section to be removed.
    public void makeResFile(String dictPath, int[] userChoiceForResultFile){
        try {
            File file = new File("fileAnalysisResults.txt");

            AtomicInteger tmpCounter = new AtomicInteger(1);
            FileWriter writer = new FileWriter(file);
            writer.write("Result for scan on "+dictPath+": \n\n");

            if (userChoiceForResultFile[0] == 1)
                writeTopTenFiles(writer, tmpCounter);

            if (userChoiceForResultFile[1] == 1)
                writeTopTenExtensions(writer, tmpCounter);

            if (userChoiceForResultFile[2] == 1) {
                writeDuplicateFiles(writer, tmpCounter);

                if (userChoiceForResultFile[4] == 1)
                    deleteDuplicateFiles(writer);
            }

            if (userChoiceForResultFile[3] == 1){
                writeTotalNumberOfFiles(writer);
                writeTotalDictSize(writer);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred while writing to result file.");
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "An error occurred while writing to result file."));
        }
    }

    private void deleteDuplicateFiles(FileWriter writer) throws IOException {
        //removes the first file detected so it doesn't also get deleted.
        writer.write("\nOriginal files (retained and not deleted): \n");
        duplicateFilesMap.forEach((_, list) ->{
            Path removed = list.removeFirst();
            try {
                writer.write("File "+ removed.toAbsolutePath() +" ( NOT DELETED )\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        writer.write("\nDuplicate files (deleted): \n");
        duplicateFilesMap.forEach((_, list) ->{
            for (Path path : list){
                try {
                    Files.delete(path);
                    if (!Files.exists(path)) {
                        writer.write("File "+ path.toAbsolutePath() +" ( DELETED )\n");
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while writing to result file.");
                        alert.showAndWait();
                    });
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void writeTotalNumberOfFiles(FileWriter writer) throws IOException {
        writer.write("\n- Total number of files scanned: "+numberOfFiles.get()+"\n");
    }

    private void writeTotalDictSize(FileWriter writer) throws IOException {
        writer.write("\n- Total directory size " + FileUtils.humanReadableSize(getTotalDictSize()));
    }

    private void writeDuplicateFiles(FileWriter writer, AtomicInteger tmpCounter) throws IOException {
        writer.write("\n\n- Duplicate Files (based on hashcode): \n");
        for (Map.Entry<String, List<Path>> entry : duplicateFilesMap.entrySet()) {
            List<Path> listOfDuplicateFiles = entry.getValue();
            writer.write(tmpCounter + "- ");
            for (Path duplicateFilePath : listOfDuplicateFiles) {
                writer.write(duplicateFilePath.getFileName() + ", ");
            }
            writer.write(" | Each File Size = "+FileUtils.humanReadableSize(
                            fileNameToSizeMap.get(listOfDuplicateFiles.getFirst()))+"\n");
            tmpCounter.getAndIncrement();
        }
        writer.write("\n- Total size of duplicate files (without the size of the original files): " +
                FileUtils.humanReadableSize(totalDuplicateFilesSize.longValue())+"\n");
        tmpCounter.lazySet(1);
    }

    private void writeTopTenExtensions(FileWriter writer, AtomicInteger tmpCounter) throws IOException {
        writer.write("\n\n\n- Top 10 File extension types and their sizes: \n");
        for (Map.Entry<String, Long> e : getTopTenExtCommon()) {
            writer.write(tmpCounter + "- Extension type = " + e.getKey() +
                    " | File Size = " + FileUtils.humanReadableSize(e.getValue()) + "\n");
            tmpCounter.getAndIncrement();
        }
        tmpCounter.lazySet(1);
    }

    private void writeTopTenFiles(FileWriter writer, AtomicInteger tmpCounter) throws IOException {
        writer.write("- Top 10 Files and their sizes: \n");
        for (Map.Entry<Path, Long> pathLongEntry : getTopTenFileSizes()) {
            writer.write(tmpCounter + "- File name = " + pathLongEntry.getKey().getFileName() +
                    " | File Size = " + FileUtils.humanReadableSize(pathLongEntry.getValue()) + "\n");
            tmpCounter.getAndIncrement();
        }
        tmpCounter.lazySet(1);
    }

    //getters
    public AtomicInteger getNumberOfFiles() {
        return numberOfFiles;
    }

    public LongAdder getTotalDuplicateFilesSize() {
        return totalDuplicateFilesSize;
    }

    public long getTotalDictSize(){
        return totalDictSize.sum();
    }

    public List<Map.Entry<Path, Long>> getTopTenFileSizes(){
        List<Map.Entry<Path, Long>> sortedEntries = new ArrayList<>(fileNameToSizeMap.entrySet());
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
}

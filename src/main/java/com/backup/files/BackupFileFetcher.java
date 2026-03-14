package com.backup.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BackupFileFetcher {

    public static class FileInfo {
        private final String name;
        private final long size;
        private final long lastModified;

        public FileInfo(String name, long size, long lastModified) {
            this.name = name;
            this.size = size;
            this.lastModified = lastModified;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public long getLastModified() {
            return lastModified;
        }
    }

    public FileInfo[] getFiles() throws IOException {
        Path dir = Paths.get("/home/andi/Dokumente");
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IOException("Directory does not exist or is not a directory: " + dir);
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.forEach(path -> {
                try {
                    long size = Files.size(path);
                    long lastModified = Files.getLastModifiedTime(path).toMillis();
                    fileInfos.add(new FileInfo(path.getFileName().toString(), size, lastModified));
                } catch (IOException e) {
                    System.err.println("Error processing file " + path + ": " + e.getMessage());
                    // Skip problematic files instead of crashing
                }
            });
        }
        return fileInfos.toArray(new FileInfo[0]);
    }
}

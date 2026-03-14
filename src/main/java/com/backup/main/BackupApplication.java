package com.backup.main;

import com.backup.files.BackupFileFetcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BackupApplication extends Application {
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BackupApplication.class.getResource("backup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 580);
        stage.setTitle("Backup");
        stage.setScene(scene);
        stage.show();

        // Fetch files and log to console
        try {
            BackupFileFetcher.FileInfo[] files = new BackupFileFetcher().getFiles();
            for (BackupFileFetcher.FileInfo file : files) {
                Instant instant = Instant.ofEpochMilli(file.getLastModified());
                ZonedDateTime zdt = instant.atZone(ZoneId.of("CET"));
                String formattedDate = zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                System.out.println("Name: " + file.getName() + ", Size: " + formatSize(file.getSize()) + ", Last Modified: " + formattedDate);
            }
            // Summary
            int numberOfFiles = files.length;
            long totalSize = 0;
            for (BackupFileFetcher.FileInfo file : files) {
                totalSize += file.getSize();
            }
            System.out.println("Summary: " + numberOfFiles + " files, total size: " + formatSize(totalSize));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

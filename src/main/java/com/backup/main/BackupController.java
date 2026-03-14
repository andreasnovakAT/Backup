package com.backup.main;

import com.backup.files.BackupFileFetcher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BackupController {
    @FXML
    private TableView<BackupFileFetcher.FileInfo> sourceFiles;

    @FXML
    private TableColumn<BackupFileFetcher.FileInfo, String> pathColumn;

    @FXML
    private TableColumn<BackupFileFetcher.FileInfo, String> sizeColumn;

    @FXML
    private TableColumn<BackupFileFetcher.FileInfo, Long> dateColumn;

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    @FXML
    protected void onCompareButtonClick() {
        try {
            BackupFileFetcher.FileInfo[] files = new BackupFileFetcher().getFiles();
            sourceFiles.setItems(FXCollections.observableArrayList(files));

            pathColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
            sizeColumn.setCellValueFactory(data -> new SimpleStringProperty(formatSize(data.getValue().getSize())));
            dateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastModified()));
            dateColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        Instant instant = Instant.ofEpochMilli(item);
                        ZonedDateTime zdt = instant.atZone(ZoneId.of("CET"));
                        String formatted = zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                        setText(formatted);
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Error fetching files: " + e.getMessage());
        }
    }
}

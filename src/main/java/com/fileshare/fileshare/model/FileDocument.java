package com.fileshare.fileshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "files")
public class FileDocument {

    @Id
    private String id;

    private String fileName;
    private String filePath;
    private String token;

    private int downloadLimit;
    private int downloadCount;

    private LocalDateTime expiryTime;

    // analytics
    private List<String> downloadIps;
    private List<LocalDateTime> downloadTimes;
    private List<String> userAgents;

    public FileDocument() {}

    public FileDocument(String fileName, String filePath, String token) {

        this.fileName = fileName;
        this.filePath = filePath;
        this.token = token;

        this.downloadCount = 0;

        this.downloadIps = new ArrayList<>();
        this.downloadTimes = new ArrayList<>();
        this.userAgents = new ArrayList<>();
    }

    public String getId() { return id; }

    public String getFileName() { return fileName; }

    public String getFilePath() { return filePath; }

    public String getToken() { return token; }

    public int getDownloadLimit() { return downloadLimit; }

    public int getDownloadCount() { return downloadCount; }

    public LocalDateTime getExpiryTime() { return expiryTime; }

    public List<String> getDownloadIps() { return downloadIps; }

    public List<LocalDateTime> getDownloadTimes() { return downloadTimes; }

    public List<String> getUserAgents() { return userAgents; }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
}
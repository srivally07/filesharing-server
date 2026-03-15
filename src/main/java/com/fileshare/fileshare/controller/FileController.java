package com.fileshare.fileshare.controller;

import com.fileshare.fileshare.model.FileDocument;
import com.fileshare.fileshare.service.FileService;
import com.fileshare.fileshare.util.EncryptionUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    private final String uploadDir =
            System.getProperty("user.dir") + File.separator + "uploads";

    private static final int MAX_DOWNLOAD_LIMIT = 20;
    private static final int MAX_EXPIRY_HOURS = 24;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB


    // ---------------- UPLOAD API ----------------

    @ResponseBody
    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("limit") int limit,
            @RequestParam("expiry") int expiryHours) {

        try {

            if (file.getSize() > MAX_FILE_SIZE) {
                return "File too large. Max size is 10MB.";
            }

            if (limit <= 0 || limit > MAX_DOWNLOAD_LIMIT) {
                return "Download limit must be between 1 and " + MAX_DOWNLOAD_LIMIT;
            }

            if (expiryHours <= 0 || expiryHours > MAX_EXPIRY_HOURS) {
                return "Expiry must be between 1 and " + MAX_EXPIRY_HOURS + " hours";
            }

            String token = UUID.randomUUID().toString();

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = file.getOriginalFilename();
            String storedFileName = token + "_" + fileName;

            String filePath = uploadDir + File.separator + storedFileName;

            File destination = new File(filePath);

            byte[] encryptedData = EncryptionUtil.encrypt(file.getBytes());

            FileOutputStream fos = new FileOutputStream(destination);
            fos.write(encryptedData);
            fos.close();

            FileDocument fileDocument =
                    new FileDocument(fileName, filePath, token);

            fileDocument.setDownloadLimit(limit);
            fileDocument.setExpiryTime(
                    LocalDateTime.now().plusHours(expiryHours));

            fileService.saveFile(fileDocument);

            return "File uploaded successfully.\nDownload link: http://localhost:8080/f/" + token;

        } catch (Exception e) {

            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }


    // ---------------- DIRECT DOWNLOAD LINK ----------------

    @GetMapping("/f/{token}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable String token,
            HttpServletRequest request) {

        try {

            FileDocument fileDocument = fileService.getFileByToken(token);

            if (fileDocument == null) {
                return ResponseEntity.notFound().build();
            }

            if (fileDocument.getExpiryTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().build();
            }

            if (fileDocument.getDownloadCount() >=
                    fileDocument.getDownloadLimit()) {

                return ResponseEntity.badRequest().build();
            }

            File file = new File(fileDocument.getFilePath());

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] encryptedBytes = Files.readAllBytes(file.toPath());

            byte[] decryptedBytes =
                    EncryptionUtil.decrypt(encryptedBytes);

            fileDocument.setDownloadCount(
                    fileDocument.getDownloadCount() + 1);

            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            fileDocument.getDownloadIps().add(ip);
            fileDocument.getDownloadTimes().add(LocalDateTime.now());
            fileDocument.getUserAgents().add(userAgent);

            fileService.saveFile(fileDocument);

            ByteArrayResource resource =
                    new ByteArrayResource(decryptedBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    fileDocument.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
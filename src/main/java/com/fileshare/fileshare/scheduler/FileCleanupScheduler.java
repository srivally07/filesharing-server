package com.fileshare.fileshare.scheduler;

import com.fileshare.fileshare.model.FileDocument;
import com.fileshare.fileshare.repository.FileRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FileCleanupScheduler {

    private final FileRepository fileRepository;

    public FileCleanupScheduler(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    // runs every hour
    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredFiles() {

        List<FileDocument> files = fileRepository.findAll();

        for (FileDocument file : files) {

            if(file.getExpiryTime() == null){
                continue;
            }

            if (file.getExpiryTime().plusHours(4).isBefore(LocalDateTime.now())) {

                File f = new File(file.getFilePath());

                if (f.exists()) {
                    f.delete();
                }

                fileRepository.delete(file);
            }
        }
    }
}
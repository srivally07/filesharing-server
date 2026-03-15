package com.fileshare.fileshare.service;

import com.fileshare.fileshare.model.FileDocument;
import com.fileshare.fileshare.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public FileDocument saveFile(FileDocument fileDocument) {
        return fileRepository.save(fileDocument);
    }

    public FileDocument getFileByToken(String token) {
        return fileRepository.findByToken(token);
    }

}
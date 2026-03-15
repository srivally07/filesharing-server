package com.fileshare.fileshare.repository;

import com.fileshare.fileshare.model.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<FileDocument, String> {

    FileDocument findByToken(String token);

}
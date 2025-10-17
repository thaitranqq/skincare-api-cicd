package com.example.demo.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    /**
     * Saves a file and returns a unique key or path to access it.
     * @param file the file to save
     * @return a unique key for the stored file
     * @throws IOException if the file cannot be saved
     */
    String save(MultipartFile file) throws IOException;

    /**
     * Deletes a file based on its key.
     * @param fileKey the unique key of the file to delete
     */
    void delete(String fileKey);
}

package com.example.demo.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;

    public LocalFileStorageService(@Value("${file.upload-dir:./uploads/}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        if (file.getOriginalFilename() == null) {
            throw new IOException("File has no original filename");
        }
        // Generate a unique filename to avoid conflicts
        String extension = "";
        int i = file.getOriginalFilename().lastIndexOf('.');
        if (i > 0) {
            extension = file.getOriginalFilename().substring(i);
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Copy file to the target location
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return the key (in this case, just the filename)
        return uniqueFileName;
    }

    @Override
    public void delete(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }
        try {
            Path filePath = this.fileStorageLocation.resolve(fileKey).normalize();
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            log.error("Could not delete file: {}", fileKey, e);
            // Depending on the policy, you might want to re-throw or just log
        }
    }
}

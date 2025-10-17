package com.example.demo.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("File has no original filename");
        }
        // Clean the file name to prevent path traversal issues
        String fileName = StringUtils.cleanPath(originalFilename);

        // Check for path traversal characters
        if (fileName.contains("..")) {
            throw new SecurityException("Cannot store file with relative path outside current directory " + fileName);
        }

        // Generate a unique filename to avoid conflicts
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = fileName.substring(i);
        }
        String uniqueFileName = UUID.randomUUID() + extension;

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
            String safeFileKey = StringUtils.cleanPath(fileKey);
            if (safeFileKey.contains("..")) {
                log.warn("Path traversal attempt in delete: {}", fileKey);
                return;
            }
            Path filePath = this.fileStorageLocation.resolve(safeFileKey).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)) {
                log.warn("Attempt to delete file outside of storage directory: {}", fileKey);
                return;
            }
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            log.error("Could not delete file: {}", fileKey, e);
        }
    }
}

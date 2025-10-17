package com.example.demo.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Primary // This annotation makes this the primary implementation of FileStorageService
@Slf4j
public class AzureBlobStorageService implements FileStorageService {

    private final BlobContainerClient blobContainerClient;

    public AzureBlobStorageService(
            @Value("${azure.storage.blob.account-name}") String accountName,
            @Value("${azure.storage.blob.account-key}") String accountKey,
            @Value("${azure.storage.blob.container-name}") String containerName) {

        String connectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                accountName, accountKey);

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!this.blobContainerClient.exists()) {
            this.blobContainerClient.create();
        }
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        if (file.getOriginalFilename() == null) {
            throw new IOException("File has no original filename.");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }

        // Generate a unique filename to avoid conflicts
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        BlobClient blobClient = blobContainerClient.getBlobClient(uniqueFileName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // Return the full URL of the uploaded blob
        return blobClient.getBlobUrl();
    }

    @Override
    public void delete(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }

        try {
            // The fileKey is the full URL. We need to extract the blob name from it.
            String blobName = fileKey.substring(fileKey.lastIndexOf('/') + 1);
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            if (blobClient.exists()) {
                blobClient.delete();
            }
        } catch (Exception e) {
            log.error("Error deleting blob from Azure: {}", fileKey, e);
            // Depending on the policy, you might want to re-throw or just log
        }
    }
}

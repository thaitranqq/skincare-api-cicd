package com.example.demo.journal.dto;

import lombok.Data;

@Data
public class JournalPhotoCreateRequest {
    private Long entryId;
    private String fileKey;
    private String aiFeaturesJson;
}

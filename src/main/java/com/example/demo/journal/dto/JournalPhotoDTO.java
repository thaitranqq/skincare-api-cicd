package com.example.demo.journal.dto;

import lombok.Data;

@Data
public class JournalPhotoDTO {
    private Long id;
    private Long entryId;
    private String fileKey;
    private String aiFeaturesJson;
}

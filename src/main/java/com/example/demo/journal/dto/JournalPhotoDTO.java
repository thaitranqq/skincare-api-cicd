package com.example.demo.journal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JournalPhotoDTO {
    private Long id;
    private Long entryId;
    private String fileKey;
    private String imageUrl;
    private String aiFeaturesJson;
}

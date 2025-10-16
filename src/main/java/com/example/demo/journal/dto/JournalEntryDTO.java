package com.example.demo.journal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JournalEntryDTO {
    private Long id;
    private Long userId;
    private LocalDate date;
    private String textNote;
    private List<Long> photoIds; // Changed from List<JournalPhotoDTO> to List<Long>
}

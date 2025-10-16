package com.example.demo.journal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JournalEntryCreateRequest {
    private Long userId;
    private LocalDate date;
    private String textNote;
}

package com.example.demo.journal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JournalEntryUpdateRequest {
    private LocalDate date;
    private String textNote;
}

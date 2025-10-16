package com.example.demo.journal.service;

import com.example.demo.journal.dto.JournalEntryCreateRequest;
import com.example.demo.journal.dto.JournalEntryDTO;
import com.example.demo.journal.dto.JournalEntryUpdateRequest;
import com.example.demo.journal.dto.JournalPhotoCreateRequest;
import com.example.demo.journal.dto.JournalPhotoDTO;

import java.util.List;

public interface JournalService {
    // Journal Entry operations
    List<JournalEntryDTO> getAllJournalEntries();
    JournalEntryDTO getJournalEntryById(Long id);
    List<JournalEntryDTO> getJournalEntriesByUserId(Long userId);
    JournalEntryDTO createJournalEntry(JournalEntryCreateRequest request);
    JournalEntryDTO updateJournalEntry(Long id, JournalEntryUpdateRequest request);
    void deleteJournalEntry(Long id);

    // Journal Photo operations
    JournalPhotoDTO getJournalPhotoById(Long id);
    List<JournalPhotoDTO> getJournalPhotosByEntryId(Long entryId);
    JournalPhotoDTO addJournalPhoto(Long entryId, JournalPhotoCreateRequest request);
    void deleteJournalPhoto(Long id);
}

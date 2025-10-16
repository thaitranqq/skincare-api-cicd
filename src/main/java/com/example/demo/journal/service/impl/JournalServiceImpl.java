package com.example.demo.journal.service.impl;

import com.example.demo.model.JournalEntry;
import com.example.demo.model.JournalPhoto;
import com.example.demo.journal.dto.JournalEntryCreateRequest;
import com.example.demo.journal.dto.JournalEntryDTO;
import com.example.demo.journal.dto.JournalEntryUpdateRequest;
import com.example.demo.journal.dto.JournalPhotoCreateRequest;
import com.example.demo.journal.dto.JournalPhotoDTO;
import com.example.demo.journal.service.JournalService;
import com.example.demo.repository.JournalEntryRepository;
import com.example.demo.repository.JournalPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalPhotoRepository journalPhotoRepository;

    @Override
    public List<JournalEntryDTO> getAllJournalEntries() {
        return journalEntryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public JournalEntryDTO getJournalEntryById(Long id) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal Entry not found with id: " + id));
        return toDto(entry);
    }

    @Override
    public List<JournalEntryDTO> getJournalEntriesByUserId(Long userId) {
        return journalEntryRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public JournalEntryDTO createJournalEntry(JournalEntryCreateRequest request) {
        JournalEntry entry = new JournalEntry();
        entry.setUserId(request.getUserId());
        entry.setDate(request.getDate());
        entry.setTextNote(request.getTextNote());

        JournalEntry savedEntry = journalEntryRepository.save(entry);
        return toDto(savedEntry);
    }

    @Override
    public JournalEntryDTO updateJournalEntry(Long id, JournalEntryUpdateRequest request) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal Entry not found with id: " + id));

        if (request.getDate() != null) {
            entry.setDate(request.getDate());
        }
        if (request.getTextNote() != null) {
            entry.setTextNote(request.getTextNote());
        }

        JournalEntry updatedEntry = journalEntryRepository.save(entry);
        return toDto(updatedEntry);
    }

    @Override
    public void deleteJournalEntry(Long id) {
        if (!journalEntryRepository.existsById(id)) {
            throw new RuntimeException("Journal Entry not found with id: " + id);
        }
        journalEntryRepository.deleteById(id);
    }

    @Override
    public JournalPhotoDTO getJournalPhotoById(Long id) {
        JournalPhoto photo = journalPhotoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal Photo not found with id: " + id));
        return toDto(photo);
    }

    @Override
    public List<JournalPhotoDTO> getJournalPhotosByEntryId(Long entryId) {
        return journalPhotoRepository.findByEntryId(entryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public JournalPhotoDTO addJournalPhoto(Long entryId, JournalPhotoCreateRequest request) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Journal Entry not found with id: " + entryId));

        JournalPhoto photo = new JournalPhoto();
        photo.setEntry(entry);
        photo.setFileKey(request.getFileKey());
        photo.setAiFeaturesJson(request.getAiFeaturesJson());

        JournalPhoto savedPhoto = journalPhotoRepository.save(photo);
        return toDto(savedPhoto);
    }

    @Override
    public void deleteJournalPhoto(Long id) {
        if (!journalPhotoRepository.existsById(id)) {
            throw new RuntimeException("Journal Photo not found with id: " + id);
        }
        journalPhotoRepository.deleteById(id);
    }

    // DTO mapping methods
    private JournalEntryDTO toDto(JournalEntry entry) {
        JournalEntryDTO dto = new JournalEntryDTO();
        dto.setId(entry.getId());
        dto.setUserId(entry.getUserId());
        dto.setDate(entry.getDate());
        dto.setTextNote(entry.getTextNote());
        if (entry.getPhotos() != null) {
            dto.setPhotos(entry.getPhotos().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private JournalPhotoDTO toDto(JournalPhoto photo) {
        JournalPhotoDTO dto = new JournalPhotoDTO();
        dto.setId(photo.getId());
        dto.setEntryId(photo.getEntry() != null ? photo.getEntry().getId() : null);
        dto.setFileKey(photo.getFileKey());
        dto.setAiFeaturesJson(photo.getAiFeaturesJson());
        return dto;
    }
}

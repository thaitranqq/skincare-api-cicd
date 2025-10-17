package com.example.demo.journal.service.impl;

import com.example.demo.journal.dto.JournalEntryCreateRequest;
import com.example.demo.journal.dto.JournalEntryDTO;
import com.example.demo.journal.dto.JournalEntryUpdateRequest;
import com.example.demo.journal.dto.JournalPhotoCreateRequest;
import com.example.demo.journal.dto.JournalPhotoDTO;
import com.example.demo.journal.service.JournalService;
import com.example.demo.mapper.JournalMapper;
import com.example.demo.model.JournalEntry;
import com.example.demo.model.JournalPhoto;
import com.example.demo.repository.JournalEntryRepository;
import com.example.demo.repository.JournalPhotoRepository;
import com.example.demo.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalPhotoRepository journalPhotoRepository;
    private final FileStorageService fileStorageService;
    private final JournalMapper journalMapper;

    @Override
    public List<JournalEntryDTO> getAllJournalEntries() {
        return journalEntryRepository.findAll().stream()
                .map(journalMapper::toJournalEntryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JournalEntryDTO getJournalEntryById(Long id) {
        return journalEntryRepository.findById(id)
                .map(journalMapper::toJournalEntryDTO)
                .orElseThrow(() -> new RuntimeException("JournalEntry not found with id: " + id));
    }

    @Override
    public List<JournalEntryDTO> getJournalEntriesByUserId(Long userId) {
        return journalEntryRepository.findByUserId(userId).stream()
                .map(journalMapper::toJournalEntryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JournalEntryDTO createJournalEntry(JournalEntryCreateRequest request) {
        JournalEntry journalEntry = journalMapper.toJournalEntry(request);
        if (request.getTextNote() != null) {
            journalEntry.setTextNote(HtmlUtils.htmlEscape(request.getTextNote()));
        }
        JournalEntry savedEntry = journalEntryRepository.save(journalEntry);
        return journalMapper.toJournalEntryDTO(savedEntry);
    }

    @Override
    @Transactional
    public JournalEntryDTO updateJournalEntry(Long id, JournalEntryUpdateRequest request) {
        JournalEntry existingEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JournalEntry not found with id: " + id));
        existingEntry.setDate(request.getDate());
        if (request.getTextNote() != null) {
            existingEntry.setTextNote(HtmlUtils.htmlEscape(request.getTextNote()));
        }
        JournalEntry updatedEntry = journalEntryRepository.save(existingEntry);
        return journalMapper.toJournalEntryDTO(updatedEntry);
    }

    @Override
    @Transactional
    public void deleteJournalEntry(Long id) {
        // Note: Deleting an entry will also delete its photos from the database due to cascade.
        // A more robust implementation would also delete the files from storage.
        if (!journalEntryRepository.existsById(id)) {
            throw new RuntimeException("JournalEntry not found with id: " + id);
        }
        journalEntryRepository.deleteById(id);
    }

    @Override
    public JournalPhotoDTO getJournalPhotoById(Long id) {
        return journalPhotoRepository.findById(id)
                .map(journalMapper::toJournalPhotoDTO)
                .orElseThrow(() -> new RuntimeException("JournalPhoto not found with id: " + id));
    }

    @Override
    public List<JournalPhotoDTO> getJournalPhotosByEntryId(Long entryId) {
        return journalPhotoRepository.findByJournalEntryId(entryId).stream()
                .map(journalMapper::toJournalPhotoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JournalPhotoDTO addJournalPhoto(Long entryId, JournalPhotoCreateRequest request) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("JournalEntry not found with id: " + entryId));
        JournalPhoto newPhoto = new JournalPhoto();
        newPhoto.setJournalEntry(entry);
        newPhoto.setFileKey(request.getFileKey());
        JournalPhoto savedPhoto = journalPhotoRepository.save(newPhoto);
        return journalMapper.toJournalPhotoDTO(savedPhoto);
    }

    @Override
    @Transactional
    public JournalPhotoDTO addPhotoToJournal(Long entryId, MultipartFile file) throws IOException {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("JournalEntry not found with id: " + entryId));
        String fileKey = fileStorageService.save(file);
        JournalPhoto newPhoto = new JournalPhoto();
        newPhoto.setJournalEntry(entry);
        newPhoto.setFileKey(fileKey);
        JournalPhoto savedPhoto = journalPhotoRepository.save(newPhoto);
        JournalPhotoDTO dto = journalMapper.toJournalPhotoDTO(savedPhoto);
        if (dto.getFileKey() != null) {
            dto.setFileKey(HtmlUtils.htmlEscape(dto.getFileKey()));
        }
        return dto;
    }

    @Override
    @Transactional
    public JournalPhotoDTO updateJournalPhoto(Long photoId, MultipartFile file) throws IOException {
        // 1. Find the existing photo entity
        JournalPhoto existingPhoto = journalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("JournalPhoto not found with id: " + photoId));

        // 2. Get the old file key and delete the old file from storage
        String oldFileKey = existingPhoto.getFileKey();
        fileStorageService.delete(oldFileKey);

        // 3. Save the new file and get the new key
        String newFileKey = fileStorageService.save(file);

        // 4. Update the entity with the new file key
        existingPhoto.setFileKey(newFileKey);
        JournalPhoto updatedPhoto = journalPhotoRepository.save(existingPhoto);

        // 5. Return the updated DTO
        JournalPhotoDTO dto = journalMapper.toJournalPhotoDTO(updatedPhoto);
        if (dto.getFileKey() != null) {
            dto.setFileKey(HtmlUtils.htmlEscape(dto.getFileKey()));
        }
        return dto;
    }

    @Override
    @Transactional
    public void deleteJournalPhoto(Long id) {
        // 1. Find the photo to get the fileKey before deleting from DB
        JournalPhoto photo = journalPhotoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JournalPhoto not found with id: " + id));

        // 2. Delete the file from storage
        fileStorageService.delete(photo.getFileKey());

        // 3. Delete the entity from the database
        journalPhotoRepository.deleteById(id);
    }
}

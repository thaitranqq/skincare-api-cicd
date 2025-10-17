package com.example.demo.journal.controller;

import com.example.demo.journal.dto.JournalEntryCreateRequest;
import com.example.demo.journal.dto.JournalEntryDTO;
import com.example.demo.journal.dto.JournalPhotoDTO;
import com.example.demo.journal.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    // Endpoint to create a new journal entry
    @PostMapping("/entries")
    public ResponseEntity<JournalEntryDTO> createJournalEntry(@RequestBody JournalEntryCreateRequest request) {
        JournalEntryDTO createdEntry = journalService.createJournalEntry(request);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    // Endpoint to get journal entries for a user
    @GetMapping("/entries/user/{userId}")
    public ResponseEntity<List<JournalEntryDTO>> getJournalEntriesByUserId(@PathVariable Long userId) {
        List<JournalEntryDTO> entries = journalService.getJournalEntriesByUserId(userId);
        return ResponseEntity.ok(entries);
    }

    // Endpoint to upload a photo to a specific journal entry
    @PostMapping(value = "/entries/{entryId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<JournalPhotoDTO> uploadPhoto(
                @PathVariable Long entryId,
                @RequestParam("file") MultipartFile file) {
            try {
                JournalPhotoDTO savedPhoto = journalService.addPhotoToJournal(entryId, file);
                return new ResponseEntity<>(savedPhoto, HttpStatus.CREATED);
            } catch (IOException e) {
                // A more specific error response could be returned here
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    // Endpoint to update (replace) an existing photo
    @PutMapping("/photos/{photoId}")
    public ResponseEntity<JournalPhotoDTO> updatePhoto(
            @PathVariable Long photoId,
            @RequestParam("file") MultipartFile file) {
        try {
            JournalPhotoDTO updatedPhoto = journalService.updateJournalPhoto(photoId, file);
            return ResponseEntity.ok(updatedPhoto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint to delete a photo
    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long photoId) {
        journalService.deleteJournalPhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}

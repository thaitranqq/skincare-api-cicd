package com.example.demo.repository;

import com.example.demo.model.JournalPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalPhotoRepository extends JpaRepository<JournalPhoto, Long> {
    List<JournalPhoto> findByJournalEntryId(Long entryId);
}

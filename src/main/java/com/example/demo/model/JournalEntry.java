package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries")
@Data
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assuming a User entity exists, but using userId for loose coupling as in other services.
    @Column(name = "user_id")
    private Long userId;

    private LocalDate date;

    @Column(name = "text_note")
    private String textNote;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JournalPhoto> photos = new ArrayList<>();
}

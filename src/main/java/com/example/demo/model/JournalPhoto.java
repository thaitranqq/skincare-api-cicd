package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "journal_photos")
@Data
public class JournalPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private JournalEntry entry;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "ai_features_json", columnDefinition = "json")
    private String aiFeaturesJson; // Stored as JSON string
}

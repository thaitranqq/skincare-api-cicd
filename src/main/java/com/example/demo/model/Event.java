package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String type;

    @Column(name = "payload_json", columnDefinition = "json")
    private String payloadJson; // Stored as JSON string

    @Column(name = "ts")
    private OffsetDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = OffsetDateTime.now();
        }
    }
}

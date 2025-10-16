package com.example.demo.event.controller;

import com.example.demo.event.dto.EventCreateRequest;
import com.example.demo.event.dto.EventDTO;
import com.example.demo.event.dto.EventUpdateRequest;
import com.example.demo.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventDTO>> getEventsByUserId(@PathVariable Long userId) {
        List<EventDTO> events = eventService.getEventsByUserId(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EventDTO>> getEventsByType(@PathVariable String type) {
        List<EventDTO> events = eventService.getEventsByType(type);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventCreateRequest request) {
        EventDTO createdEvent = eventService.createEvent(request);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventUpdateRequest request) {
        EventDTO updatedEvent = eventService.updateEvent(id, request);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

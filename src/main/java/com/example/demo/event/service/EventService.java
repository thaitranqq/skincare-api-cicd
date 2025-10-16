package com.example.demo.event.service;

import com.example.demo.event.dto.EventCreateRequest;
import com.example.demo.event.dto.EventDTO;
import com.example.demo.event.dto.EventUpdateRequest;

import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Long id);
    List<EventDTO> getEventsByUserId(Long userId);
    List<EventDTO> getEventsByType(String type);
    EventDTO createEvent(EventCreateRequest request);
    EventDTO updateEvent(Long id, EventUpdateRequest request);
    void deleteEvent(Long id);
}

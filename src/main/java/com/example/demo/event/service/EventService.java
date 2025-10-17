package com.example.demo.event.service;

import com.example.demo.event.dto.EventDTO;

import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Long id);
    List<EventDTO> getEventsByUserId(Long userId);
    List<EventDTO> getEventsByType(String type);
}

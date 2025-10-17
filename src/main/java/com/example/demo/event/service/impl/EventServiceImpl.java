package com.example.demo.event.service.impl;

import com.example.demo.model.Event;
import com.example.demo.event.dto.EventDTO;
import com.example.demo.event.service.EventService;
import com.example.demo.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return toDto(event);
    }

    @Override
    public List<EventDTO> getEventsByUserId(Long userId) {
        return eventRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByType(String type) {
        return eventRepository.findByType(type).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private EventDTO toDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setUserId(event.getUserId());
        dto.setType(event.getType());
        dto.setPayloadJson(event.getPayloadJson());
        dto.setTimestamp(event.getTimestamp());
        return dto;
    }
}

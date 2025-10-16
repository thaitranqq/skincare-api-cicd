package com.example.demo.event.service.impl;

import com.example.demo.model.Event;
import com.example.demo.event.dto.EventCreateRequest;
import com.example.demo.event.dto.EventDTO;
import com.example.demo.event.dto.EventUpdateRequest;
import com.example.demo.event.service.EventService;
import com.example.demo.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    @Override
    public EventDTO createEvent(EventCreateRequest request) {
        Event event = new Event();
        event.setUserId(request.getUserId());
        event.setType(request.getType());
        event.setPayloadJson(request.getPayloadJson());
        event.setTimestamp(OffsetDateTime.now());

        Event savedEvent = eventRepository.save(event);
        return toDto(savedEvent);
    }

    @Override
    public EventDTO updateEvent(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        if (request.getType() != null) {
            event.setType(request.getType());
        }
        if (request.getPayloadJson() != null) {
            event.setPayloadJson(request.getPayloadJson());
        }

        Event updatedEvent = eventRepository.save(event);
        return toDto(updatedEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
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

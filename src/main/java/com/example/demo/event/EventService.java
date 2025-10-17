package com.example.demo.event;

import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper; // Jackson's ObjectMapper

    public void recordEvent(Long userId, EventType eventType) {
        recordEvent(userId, eventType, null);
    }

    public void recordEvent(Long userId, EventType eventType, Map<String, Object> payload) {
        Event event = new Event();
        event.setUserId(userId);
        event.setType(eventType.name());

        if (payload != null && !payload.isEmpty()) {
            try {
                event.setPayloadJson(objectMapper.writeValueAsString(payload));
            } catch (JsonProcessingException e) {
                // Log the error or handle it as needed
                // For now, we'll just print to stderr
                System.err.println("Error serializing event payload: " + e.getMessage());
            }
        }

        eventRepository.save(event);
    }
}

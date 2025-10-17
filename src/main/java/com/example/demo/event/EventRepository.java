package com.example.demo.event;

import com.example.demo.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("eventRepository2")
public interface EventRepository extends JpaRepository<Event, Long> {
}

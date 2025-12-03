package com.example.calendar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.calendar.model.Event;

public class EventService {
    private final List<Event> events = new ArrayList<>();

    public Event addEvent(Event event) {
        if (event.getStart() == null || event.getEnd() == null) {
            throw new IllegalArgumentException("Event start and end times must be set");
        }

        if (!event.getStart().isBefore(event.getEnd())) {
            throw new IllegalArgumentException("Event start time must be before end time");
        }

        // set ID?

        for (Event existing : events) {
            if (isOverlapping(existing, event)) {
                throw new IllegalArgumentException("Event overlaps with existing event");
            }
        }

        events.add(event);
        events.sort(Comparator.comparing(Event::getStart));

        return event;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    public List<Event> getEventsForDate(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (event.getStart().toLocalDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    public List<Event> getTodayEvents() {
        return getEventsForDate(LocalDate.now());
    }

    public List<Event> getRemainingToday() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (e.getStart().toLocalDate().equals(now.toLocalDate())
                    && e.getEnd().isAfter(now)) {
                result.add(e);
            }
        }
        return result;
    }

    private boolean isOverlapping(Event a, Event b) {
        return a.getStart().isBefore(b.getEnd()) && a.getEnd().isAfter(b.getStart());
    }
}

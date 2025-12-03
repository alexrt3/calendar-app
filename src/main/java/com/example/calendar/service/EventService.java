package com.example.calendar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.calendar.model.Event;

@Service
public class EventService {
    private final List<Event> events = new ArrayList<>();

    public Event addEvent(Event event) {
        if (event.getStart() == null || event.getEnd() == null) {
            throw new IllegalArgumentException("Event start and end times must be set");
        }

        if (!event.getStart().isBefore(event.getEnd())) {
            throw new IllegalArgumentException("Event start time must be before end time");
        }

        if (event.getId() == null) {
            event.setId(UUID.randomUUID());
        }

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

    public Event getNextAvailableSlot(int minutes, LocalDate date) {
        List<Event> dayEvents = getEventsForDate(date);
        dayEvents.sort(Comparator.comparing(Event::getStart));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59);

        // Initial check for events on that day
        if (dayEvents.isEmpty()) {
            return new Event(
                    UUID.randomUUID(),
                    "Available Slot",
                    startOfDay,
                    startOfDay.plusMinutes(minutes));
        }

        // Check before first event
        Event first = dayEvents.get(0);
        if (durationBetween(startOfDay, first.getStart()) >= minutes) {
            return new Event(
                    UUID.randomUUID(),
                    "Available Slot",
                    startOfDay,
                    startOfDay.plusMinutes(minutes));
        }

        // Check between events
        for (int i = 0; i < dayEvents.size() - 1; i++) {
            Event current = dayEvents.get(i);
            Event next = dayEvents.get(i + 1);

            LocalDateTime gapStart = current.getEnd();
            LocalDateTime gapEnd = next.getStart();

            if (durationBetween(gapStart, gapEnd) >= minutes) {
                return new Event(
                        UUID.randomUUID(),
                        "Available Slot",
                        gapStart,
                        gapStart.plusMinutes(minutes));
            }
        }

        // Check after last event
        Event last = dayEvents.get(dayEvents.size() - 1);
        if (durationBetween(last.getEnd(), endOfDay) >= minutes) {
            return new Event(
                    UUID.randomUUID(),
                    "Available Slot",
                    last.getEnd(),
                    last.getEnd().plusMinutes(minutes));
        }

        return null;
    }

    private boolean isOverlapping(Event a, Event b) {
        return a.getStart().isBefore(b.getEnd()) && a.getEnd().isAfter(b.getStart());
    }

    private long durationBetween(LocalDateTime a, LocalDateTime b) {
        return java.time.Duration.between(a, b).toMinutes();
    }
}

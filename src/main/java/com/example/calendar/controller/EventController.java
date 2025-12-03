package com.example.calendar.controller;

import com.example.calendar.model.Event;
import com.example.calendar.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            Event saved = eventService.addEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/today")
    public List<Event> getTodayEvents() {
        return eventService.getTodayEvents();
    }

    @GetMapping("/today/remaining")
    public List<Event> getRemainingToday() {
        return eventService.getRemainingToday();
    }

    @GetMapping("/day/{date}")
    public List<Event> getEventsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return eventService.getEventsForDate(date);
    }

    @GetMapping("/next-slot")
    public ResponseEntity<?> getNextAvailableToday(@RequestParam int minutes) {
        Event slot = eventService.getNextAvailableSlot(minutes, LocalDate.now());
        if (slot == null) {
            return ResponseEntity.ok("No available slot today");
        }
        return ResponseEntity.ok(slot);
    }

    @GetMapping("/day/{date}/next-slot")
    public ResponseEntity<?> getNextAvailableForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam int minutes) {
        Event slot = eventService.getNextAvailableSlot(minutes, date);
        if (slot == null) {
            return ResponseEntity.ok("No available slot on " + date);
        }
        return ResponseEntity.ok(slot);
    }
}

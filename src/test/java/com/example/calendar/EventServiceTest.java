package com.example.calendar;

import com.example.calendar.model.Event;
import com.example.calendar.service.EventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService();
    }

    private Event createEvent(String title, LocalDateTime start, LocalDateTime end) {
        Event e = new Event();
        e.setTitle(title);
        e.setStart(start);
        e.setEnd(end);
        return e;
    }

    @Test
    void givenValidEvent_whenAdded_thenItIsStoredAndIdAssigned() {
        LocalDateTime start = LocalDateTime.of(2025, 3, 20, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 20, 10, 0);
        Event event = createEvent("Morning Standup", start, end);
        Event saved = eventService.addEvent(event);
        assertNotNull(saved.getId());
        assertEquals(1, eventService.getAllEvents().size());
        assertEquals("Morning Standup", eventService.getAllEvents().get(0).getTitle());
    }

    @Test
    void givenOverlappingEvent_whenAdded_thenThrowsException() {
        LocalDateTime start1 = LocalDateTime.of(2025, 3, 20, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2025, 3, 20, 10, 0);
        eventService.addEvent(createEvent("First", start1, end1));

        LocalDateTime start2 = LocalDateTime.of(2025, 3, 20, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2025, 3, 20, 10, 30);
        Event overlapping = createEvent("Second", start2, end2);

        assertThrows(IllegalArgumentException.class, () -> eventService.addEvent(overlapping));
    }

    @Test
    void givenEventsAcrossDates_whenFilteringByDate_thenOnlyMatchingEventsReturned() {
        LocalDate targetDate = LocalDate.of(2025, 3, 20);
        LocalDate otherDate = LocalDate.of(2025, 3, 21);

        eventService.addEvent(createEvent("Target Event", targetDate.atTime(9, 0), targetDate.atTime(10, 0)));
        eventService.addEvent(createEvent("Other Event", otherDate.atTime(9, 0), otherDate.atTime(10, 0)));

        List<Event> events = eventService.getEventsForDate(targetDate);

        assertEquals(1, events.size());
        assertEquals("Target Event", events.get(0).getTitle());
    }

    @Test
    void givenEventsTodayAndTomorrow_whenGettingTodayEvents_thenOnlyTodayEventsReturned() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        eventService.addEvent(createEvent("Today Event", today.atTime(14, 0), today.atTime(15, 0)));
        eventService.addEvent(createEvent("Tomorrow Event", tomorrow.atTime(10, 0), tomorrow.atTime(11, 0)));

        List<Event> events = eventService.getTodayEvents();

        assertEquals(1, events.size());
        assertEquals("Today Event", events.get(0).getTitle());
    }

    @Test
    void givenPastAndFutureEventsToday_whenGettingRemainingEvents_thenOnlyFutureEventsReturned() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        eventService.addEvent(createEvent("Past Event", now.minusHours(3), now.minusHours(2)));
        eventService.addEvent(createEvent("Future Event", now.plusMinutes(30), now.plusHours(1)));

        List<Event> remaining = eventService.getRemainingToday();

        assertEquals(1, remaining.size());
        assertEquals("Future Event", remaining.get(0).getTitle());
    }

    @Test
    void givenNoEventsOnDay_whenGettingNextAvailableSlot_thenStartsAtStartOfDay() {
        LocalDate date = LocalDate.of(2025, 3, 20);
        Event slot = eventService.getNextAvailableSlot(60, date);
        assertNotNull(slot);
        assertEquals(date.atStartOfDay(), slot.getStart());
        assertEquals(date.atStartOfDay().plusMinutes(60), slot.getEnd());
    }

    @Test
    void givenEventsLeavingGap_whenSearchingForSlot_thenGapIsReturned() {
        LocalDate date = LocalDate.of(2025, 3, 20);

        eventService.addEvent(createEvent("Morning Meeting", date.atTime(9, 0), date.atTime(10, 0)));
        eventService.addEvent(createEvent("Late Morning Meeting", date.atTime(11, 0), date.atTime(12, 0)));

        Event slot = eventService.getNextAvailableSlot(30, date);

        assertEquals(date.atStartOfDay(), slot.getStart());
        assertEquals(date.atStartOfDay().plusMinutes(30), slot.getEnd());
    }
}

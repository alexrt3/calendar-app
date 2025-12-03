package com.example.calendar.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private UUID id;
    private String title;

    private LocalDateTime start;
    private LocalDateTime end;
}

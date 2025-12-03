# Calendar Scheduling Service

## Overview

This project is a simple Spring Boot REST API that provides basic calendar scheduling functionality.  
It allows clients to create events, prevent overlapping bookings, list events for specific dates, and compute the next available time slot for a given duration.

## Functionality

- Create events with:
  - Title
  - Start time
  - End time
- Automatically assign a unique ID to each event
- Validate that start time is before end time
- Detect and reject overlapping (conflicting) events
- List:
  - All events
  - Today’s events
  - Remaining events for today
  - Events for a specified date
- Compute the next available time slot of a specified length:
  - For today
  - For any given date

## Environment Setup and Running the Application

### Prerequisites

- Java 17 installed
- Maven installed
- Git installed

### Clone the Repository

```bash
git clone https://github.com/your-username/calendar-app.git
cd calendar-app

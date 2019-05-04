package com.example.clay.event_manager.models;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.util.Calendar;

public class DrawableCalendarEvent extends BaseCalendarEvent {
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    String eventID;
    // region Constructors

    public DrawableCalendarEvent(long id, int color, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration, int drawableId) {
        super(id, color, title, description, location, dateStart, dateEnd, allDay, duration);
    }

    public DrawableCalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay, String eventID) {
        super(title, description, location, color, startTime, endTime, allDay);
        this.eventID = eventID;
    }

    public DrawableCalendarEvent(DrawableCalendarEvent calendarEvent) {
        super(calendarEvent);
        this.eventID = calendarEvent.getEventID();
    }

    // endregion

    // region Public methods

    // endregion

    // region Class - BaseCalendarEvent

    @Override
    public CalendarEvent copy() {
        return new DrawableCalendarEvent(this);
    }

    // endregion
}
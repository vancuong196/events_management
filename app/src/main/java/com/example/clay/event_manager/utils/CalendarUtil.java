package com.example.clay.event_manager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtil {
    SimpleDateFormat sdfDayMonthYear = new SimpleDateFormat("dd/MM/yyyy");;
    SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("EEE");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
    Calendar calendar;
    static CalendarUtil instance;

    private CalendarUtil() {
        calendar = Calendar.getInstance();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    static public CalendarUtil getInstance() {
        if(instance == null) {
            instance = new CalendarUtil();
        }
        return instance;
    }

    public SimpleDateFormat getSdfDayMonthYear() {
        return sdfDayMonthYear;
    }
    public SimpleDateFormat getSdfDayOfWeek() {
        return sdfDayOfWeek;
    }
    public SimpleDateFormat getSdfTime() {
        return sdfTime;
    }
}

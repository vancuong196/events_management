package com.example.clay.event_manager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtil {
    public static SimpleDateFormat sdfDayMonthYear = new SimpleDateFormat("dd/MM/yyyy");;
    public static SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("EEE");
    public static SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
    public static SimpleDateFormat sdfDayMonth = new SimpleDateFormat("dd/MM");

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

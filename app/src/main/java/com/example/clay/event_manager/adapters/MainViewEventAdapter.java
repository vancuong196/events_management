package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.utils.CalendarUtil;

import java.util.HashMap;

public class MainViewEventAdapter extends BaseAdapter {

    private final Activity context;
    String date;
    private HashMap<String, Event> events;
    private String[] eventIds;

    public MainViewEventAdapter(Activity context, String date) {
        this.context = context;
        this.date = date;
        events = EventRepository.getInstance(null).getEventsThroughDate(date);
        eventIds = events.keySet().toArray(new String[events.size()]);
        Log.d("debug", "EventAdapter: events size on create adapter= " + events.size());
    }

    public String[] getEventIds() {
        return eventIds;
    }

    public void setEventIds(String[] eventIds) {
        this.eventIds = eventIds;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int i) {
        return events.get(eventIds[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_event_list_item, parent, false);
        }

        TextView timeTextView = (TextView) view.findViewById(R.id.event_time_text_view);
        TextView titleTextView = (TextView) view.findViewById(R.id.event_title_text_view);
        TextView locationTextView = (TextView) view.findViewById(R.id.event_location_text_view);

        Event thisEvent = events.get(eventIds[position]);

        String start = thisEvent.getGioBatDau();
        String end = thisEvent.getGioKetThuc();
        try {
            if (!thisEvent.getNgayBatDau().equals(thisEvent.getNgayKetThuc())) {
                if(CalendarUtil.sdfDayMonthYear.parse(date).compareTo(
                        CalendarUtil.sdfDayMonthYear.parse(thisEvent.getNgayBatDau())) > 0) {
                    start = CalendarUtil.sdfDayMonth.format(CalendarUtil.sdfDayMonthYear.parse(thisEvent.getNgayBatDau()));
                }
                if(CalendarUtil.sdfDayMonthYear.parse(date).compareTo(
                        CalendarUtil.sdfDayMonthYear.parse(thisEvent.getNgayKetThuc())) < 0) {
                    end = CalendarUtil.sdfDayMonth.format(CalendarUtil.sdfDayMonthYear.parse(thisEvent.getNgayKetThuc()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String time = start + " - " + end;
        timeTextView.setText(time);
        titleTextView.setText(events.get(eventIds[position]).getTen());
        locationTextView.setText(events.get(eventIds[position]).getDiaDiem());

        return view;
    }

    public void notifyDataSetChanged(String date) {
        this.date = date;
        events = EventRepository.getInstance(null).getEventsThroughDate(date);
        eventIds = events.keySet().toArray(new String[events.size()]);
        super.notifyDataSetChanged();
    }
}

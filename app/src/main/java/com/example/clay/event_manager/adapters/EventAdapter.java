package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.left.R;

import java.util.HashMap;

public class EventAdapter extends BaseAdapter {

    private final Activity context;
    private HashMap<String, Event> events;
    private String[] eventIds;

    public EventAdapter(Activity context, HashMap<String, Event> events) {
        this.context = context;
        this.events = events;
        eventIds = events.keySet().toArray(new String[events.size()]);
        Log.d("debug", "EventAdapter: events size on create adapter= "+events.size());
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


        String time = events.get(eventIds[position]).getGioBatDau() + " - " + events.get(eventIds[position]).getGioKetThuc();
        timeTextView.setText(time);
        titleTextView.setText(events.get(eventIds[position]).getTen());
        locationTextView.setText(events.get(eventIds[position]).getDiaDiem());

        return view;
    }

    public void notifyDataSetChanged(String date) {
        events = EventRepository.getInstance(null).getEventsOnDate(date);
        eventIds = events.keySet().toArray(new String[events.size()]);
        super.notifyDataSetChanged();
    }
}

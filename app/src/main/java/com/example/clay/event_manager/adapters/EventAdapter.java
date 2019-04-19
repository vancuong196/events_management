package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.left.R;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {

    private final Activity context;
    private ArrayList<Event> events;

    public EventAdapter(Activity context, ArrayList<Event> events) {
//        super(context, R.layout.event_list_item, events);
        this.context = context;
        this.events = events;
        Log.d("debug", "EventAdapter: events size = "+events.size());
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }
//        LayoutInflater inflater = context.getLayoutInflater();
//        View rowView = inflater.inflate(R.layout.event_list_item, null, true);

        TextView timeTextView = (TextView) view.findViewById(R.id.event_time_text_view);
        TextView titleTextView = (TextView) view.findViewById(R.id.event_title_text_view);
        TextView locationTextView = (TextView) view.findViewById(R.id.event_location_text_view);

        timeTextView.setText(events.get(position).getGioBatDau());
        titleTextView.setText(events.get(position).getTen());
        locationTextView.setText(events.get(position).getDiaDiem());

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        events = EventManagementFragment.currentDateEvents;
        Log.d("debug", "EventAdapter dataSetChanged:\n"+
                "events.size() = " + events.size()+ "\n"+
                "EventManagement...currentDateEvents.size() = "+EventManagementFragment.currentDateEvents.size());
        super.notifyDataSetChanged();
    }
}

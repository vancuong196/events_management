package com.example.clay.event_manager.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.activities.EventDetailsActivity;
import com.example.clay.event_manager.activities.RootActivity;
import com.example.clay.event_manager.models.Event;

import java.util.List;

public class EventListViewAdapter {
    //extends RecyclerView.Adapter<RecyclerView.ViewHolder>
 /*
    List<Event> eventList;
    private static final int RESULT_FROM_DELETE_EVENT_INTENT = 1;
    private static final int RESULT_FROM_ADD_EVENT_INTENT = 2;
    private Context context;
    public EventListViewAdapter(List<Event> data, Context context) {
        eventList = data;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_list_item, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new EventViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,final int i) {
        if (viewHolder instanceof EventViewHolder){
            ((EventViewHolder) viewHolder).name.setText(eventList.get(i).getTen());
            ((EventViewHolder) viewHolder).dateFrom.setText(eventList.get(i).getNgayBatDau());
            ((EventViewHolder) viewHolder).dateTo.setText(eventList.get(i).getNgayKetThuc());
            ((EventViewHolder) viewHolder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
                    eventDetailsIntent.putExtra("eventId", eventList.get(i).getId());
                    ((RootActivity) context).startActivityForResult(eventDetailsIntent, RESULT_FROM_DELETE_EVENT_INTENT);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView dateFrom;
        TextView dateTo;
        TextView name;
        CardView layout;
        EventViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_event_name);
            dateFrom = itemView.findViewById(R.id.tv_event_start);
            dateTo = itemView.findViewById(R.id.tv_event_stop);
            layout = itemView.findViewById(R.id.event_item_layout);
        }
    }
*/
}
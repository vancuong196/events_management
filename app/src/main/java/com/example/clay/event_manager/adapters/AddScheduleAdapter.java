package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.models.Schedule;
import com.example.clay.event_manager.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class AddScheduleAdapter extends BaseAdapter {

    private final Activity context;
    ArrayList<Schedule> schedules;
    Calendar calendar = Calendar.getInstance();

    public AddScheduleAdapter(Activity context, ArrayList<Schedule> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int i) {
        return schedules.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_add_schedule_list_item, viewGroup, false);
        }

        final EditText scheduleTimeEditText = view.findViewById(R.id.add_schedule_time_edit_text);
        EditText scheduleContentEditText = view.findViewById(R.id.add_schedule_content_edit_text);
        Button scheduleDeleteButton = view.findViewById(R.id.add_schedule_delete_button);

        //Fill information
        scheduleTimeEditText.setText(schedules.get(i).getTime());
        scheduleContentEditText.setText(schedules.get(i).getContent());

        //Add events
        scheduleDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedules.remove(i);
                notifyDataSetChanged();
            }
        });

        scheduleTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if (!scheduleTimeEditText.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(CalendarUtil.sdfTime.parse(scheduleTimeEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int HH = calendar.get(Calendar.HOUR_OF_DAY);
                int mm = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        scheduleTimeEditText.setText(CalendarUtil.sdfTime.format(calendar.getTime()));
                    }
                }, HH, mm, false).show();
            }
        });

        return view;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }
}

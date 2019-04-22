package com.example.clay.event_manager.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.activities.EventDetailsActivity;
import com.example.clay.event_manager.adapters.EventAdapter;
import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.left.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventManagementFragment extends Fragment implements IOnDataLoadComplete {

    public static ArrayList<Employee> employeeList;

    ListView eventsListView;
    TextView dayTitleTextView;
    CalendarView calendarView;
    EventAdapter eventAdapter;
    boolean isFirstLoad = true;

    public EventManagementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectViews(view);
        addEvents();

        String date = CalendarUtil.getInstance().getSdfDayMonthYear().format(calendarView.getDate());
        dayTitleTextView.setText(Constants.DAY_TITLE_MAIN_FRAGMENT + date);

        //Update eventList at EventRepository & employeeList at EmployeeRepository
        EventRepository.getInstance(this);
        EmployeeRepository.getInstance(this);

        eventAdapter = new EventAdapter(getActivity(), EventRepository.getInstance(null)
                .getEventsOnDate(date));
        eventsListView.setAdapter(eventAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Mở cửa sổ thêm sự kiện
        if (id == R.id.action_add_e) {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
            return true;
        }
        //Xem sự kiện theo danh sách dọc
        if (id == R.id.action_list_view) {
            Toast.makeText(getActivity(),"Xem theo danh sách",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectViews(View v) {
        eventsListView = (ListView) v.findViewById(R.id.events_listview);
        calendarView = (CalendarView) v.findViewById(R.id.calendar_view);
        dayTitleTextView = (TextView) v.findViewById(R.id.day_title_text_view);
    }

    private void addEvents() {
        //Cập nhật danh sách sự kiện theo ngày đã chọn
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
//                Toast.makeText(getContext(), dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
                CalendarUtil.getInstance().getCalendar().set(Calendar.YEAR, year);
                CalendarUtil.getInstance().getCalendar().set(Calendar.MONTH, month);
                CalendarUtil.getInstance().getCalendar().set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String date = CalendarUtil.getInstance().getSdfDayMonthYear()
                        .format(CalendarUtil.getInstance().getCalendar().getTime());

                dayTitleTextView.setText(Constants.DAY_TITLE_MAIN_FRAGMENT + date);
                eventAdapter.notifyDataSetChanged(date);
            }
        });

        //Xem chi tiết sự kiện
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent eventDetailsIntent = new Intent(getActivity(), EventDetailsActivity.class);
                eventDetailsIntent.putExtra("position", position);
                startActivity(eventDetailsIntent);
            }
        });
    }

    //Cập nhật danh sách sự kiện của ngày hiện tại khi mở ứng dụng
    @Override
    public void notifyOnLoadComplete() {
        if (isFirstLoad) {
            String date = CalendarUtil.getInstance().getSdfDayMonthYear().format(calendarView.getDate());
            eventAdapter.notifyDataSetChanged(date);
            isFirstLoad = false;
        }
    }

    @Override
    public void notifyError() {

    }
}

package com.example.clay.event_manager.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.example.clay.event_manager.activities.ViewEventActivity;
import com.example.clay.event_manager.activities.RootActivity;
import com.example.clay.event_manager.activities.ViewEventActivity;
import com.example.clay.event_manager.adapters.MainViewEventAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;
import com.example.clay.event_manager.repositories.ScheduleRepository;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventManagementFragment extends Fragment implements IOnDataLoadComplete {

    private static final int RESULT_FROM_DELETE_EVENT_INTENT = 1;
    private static final int RESULT_FROM_ADD_EVENT_INTENT = 2;

    CustomListView eventsListView;
    TextView dayTitleTextView;
    CalendarView calendarView;
    MainViewEventAdapter mainViewEventAdapter;
    boolean isFirstLoad = true;

    Calendar calendar = Calendar.getInstance();

    String currentDate;

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

        currentDate = CalendarUtil.sdfDayMonthYear.format(calendarView.getDate());
        Log.d("debug", "initiated current date: " + currentDate);
        dayTitleTextView.setText(Constants.DAY_TITLE_MAIN_FRAGMENT + currentDate);

        //Update eventList at EventRepository & employeeList at EmployeeRepository
        EventRepository.getInstance(this);
        EmployeeRepository.getInstance(this);
        SalaryRepository.getInstance(this);
        ScheduleRepository.getInstance(this);

        mainViewEventAdapter = new MainViewEventAdapter(getActivity(), currentDate);
        eventsListView.setAdapter(mainViewEventAdapter);
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
            startActivityForResult(intent, RESULT_FROM_ADD_EVENT_INTENT);
            return true;
        }
        //Xem sự kiện theo danh sách dọc
        if (id == R.id.action_list_view) {
            RootActivity activity = (RootActivity) getActivity();
            activity.openEventListFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectViews(View v) {
        eventsListView = (CustomListView) v.findViewById(R.id.events_listview);
        calendarView = (CalendarView) v.findViewById(R.id.calendar_view);
        dayTitleTextView = (TextView) v.findViewById(R.id.day_title_text_view);
    }

    private void addEvents() {
        //Cập nhật danh sách sự kiện theo ngày đã chọn
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                currentDate = CalendarUtil.sdfDayMonthYear.format(calendar.getTime());

                dayTitleTextView.setText(Constants.DAY_TITLE_MAIN_FRAGMENT + currentDate);
                mainViewEventAdapter.notifyDataSetChanged(currentDate);
            }
        });

        //Xem chi tiết sự kiện
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent eventDetailsIntent = new Intent(getActivity(), ViewEventActivity.class);
                eventDetailsIntent.putExtra("eventId", mainViewEventAdapter.getEventIds()[position]);
                startActivityForResult(eventDetailsIntent, RESULT_FROM_DELETE_EVENT_INTENT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FROM_DELETE_EVENT_INTENT && resultCode == Activity.RESULT_OK) {
            Log.d("debug", "delete? from EventDetails to EventManagement: " + data.getBooleanExtra("delete?", false));
            if(data.getBooleanExtra("delete?", false)) {
                EventRepository.getInstance(null).deleteEvent(data.getStringExtra("eventId"), new EventRepository.MyDeleteEventCallback() {
                    @Override
                    public void onCallback(boolean deleteEventSucceed, boolean deleteSalariesSucceed) {
                        if(deleteEventSucceed && deleteSalariesSucceed) {
                            Toast.makeText(getActivity(), "Xóa sự kiện thành công", Toast.LENGTH_SHORT).show();
                            mainViewEventAdapter.notifyDataSetChanged(currentDate);
                        } else {
                            Toast.makeText(getActivity(), "Xóa sự kiện thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else if (requestCode == RESULT_FROM_ADD_EVENT_INTENT && resultCode == Activity.RESULT_OK) {
            if(data.getBooleanExtra("added?", false)) {
                mainViewEventAdapter.notifyDataSetChanged(currentDate);
            }
        }
    }

    //Cập nhật danh sách sự kiện của ngày hiện tại khi mở ứng dụng
    @Override
    public void notifyOnLoadComplete() {
        if (isFirstLoad) {
            String date = CalendarUtil.sdfDayMonthYear.format(calendarView.getDate());
            mainViewEventAdapter.notifyDataSetChanged(date);
            isFirstLoad = false;
        }
    }

    @Override
    public void notifyError() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mainViewEventAdapter.notifyDataSetChanged(currentDate);
    }
}

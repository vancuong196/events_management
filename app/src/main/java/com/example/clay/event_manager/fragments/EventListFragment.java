package com.example.clay.event_manager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.activities.EventDetailsActivity;
import com.example.clay.event_manager.activities.RootActivity;
import com.example.clay.event_manager.adapters.EventListViewAdapter;
import com.example.clay.event_manager.adapters.MainViewEventAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.event_manager.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class EventListFragment extends Fragment implements IOnDataLoadComplete {

    EventListViewAdapter listViewAdapter;
    RecyclerView recyclerView;
    boolean isFirstLoad = true;
    List<Event> eventsList;
    String currentDate;
    private static final int RESULT_FROM_DELETE_EVENT_INTENT = 1;
    private static final int RESULT_FROM_ADD_EVENT_INTENT = 2;
    public EventListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectViews(view);
        EventRepository.getInstance(this);
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
          //  startActivityForResult(intent, RESULT_FROM_ADD_EVENT_INTENT);
            return true;
        }
        //Xem sự kiện theo danh sách dọc
        if (id == R.id.action_list_view) {
            RootActivity activity = (RootActivity) getActivity();
            activity.openEventManagementFragment();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void connectViews(View v) {
        recyclerView =  v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Event> data = new ArrayList<Event>() {
        };
        eventsList = new ArrayList<>();
        listViewAdapter = new EventListViewAdapter(eventsList,getActivity());
        recyclerView.setAdapter(listViewAdapter);
        initDataForAdapter();
    }
    private void initDataForAdapter() {
        for (String e: EventRepository.getInstance(null).getAllEvents().keySet())
        {
            eventsList.add(EventRepository.getInstance(null).getAllEvents().get(e));
        };
        listViewAdapter.notifyDataSetChanged();
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
                            listViewAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Xóa sự kiện thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else if (requestCode == RESULT_FROM_ADD_EVENT_INTENT && resultCode == Activity.RESULT_OK) {
            if(data.getBooleanExtra("added?", false)) {
                listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void addEvents() {
        /*
        //Cập nhật danh sách sự kiện theo ngày đã chọn
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                CalendarUtil.getInstance().getCalendar().set(Calendar.YEAR, year);
                CalendarUtil.getInstance().getCalendar().set(Calendar.MONTH, month);
                CalendarUtil.getInstance().getCalendar().set(Calendar.DAY_OF_MONTH, dayOfMonth);

                currentDate = CalendarUtil.getInstance().getSdfDayMonthYear()
                        .format(CalendarUtil.getInstance().getCalendar().getTime());

                dayTitleTextView.setText(Constants.DAY_TITLE_MAIN_FRAGMENT + currentDate);
                mainViewEventAdapter.notifyDataSetChanged(currentDate);
            }
        });

        //Xem chi tiết sự kiện
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent eventDetailsIntent = new Intent(getActivity(), EventDetailsActivity.class);
                eventDetailsIntent.putExtra("eventId", mainViewEventAdapter.getEventIds()[position]);
                startActivityForResult(eventDetailsIntent, RESULT_FROM_DELETE_EVENT_INTENT);
            }
        });
        */
    }
    //Cập nhật danh sách sự kiện của ngày hiện tại khi mở ứng dụng
    @Override
    public void notifyOnLoadComplete() {
    }

    @Override
    public void notifyError() {

    }
}

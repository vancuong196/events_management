package com.example.clay.event_manager.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.adapters.EventAdapter;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.example.clay.left.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventManagementFragment extends Fragment {

    public static ArrayList<Employee> employeeList;

    FloatingActionButton addButton;
    ListView eventsListView;
    CalendarView calendarView;
    EventAdapter eventAdapter;
    public static ArrayList<Event> currentDateEvents;

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
        currentDateEvents = new ArrayList<>();
        eventAdapter = new EventAdapter(getActivity(), EventManagementFragment.currentDateEvents);
        eventsListView.setAdapter(eventAdapter);

        EventRepository.getEventsOnDate(new EventRepository.MyEventCallback() {
            @Override
            public void onCallback(ArrayList<Event> eventList) {
//                currentDateEvents.clear();
//                currentDateEvents.addAll(eventList);
                currentDateEvents = eventList;
                Log.d("debug", "EventManagementFragment: got "+currentDateEvents.size()+" events on View created. " +
                        "date = "+CalendarUtil.getInstance().getSdfDayMonthYear().format(calendarView.getDate()));
                eventAdapter.notifyDataSetChanged();
            }
        }, date);
        employeeList = EmployeeRepository.getEmployeesFromServer();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_e) {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_list_view) {
            Toast.makeText(getActivity(),"Xem theo danh sách",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectViews(View v) {

        addButton = (FloatingActionButton) v.findViewById(R.id.add_button);
        eventsListView = (ListView) v.findViewById(R.id.events_listview);
        calendarView = (CalendarView) v.findViewById(R.id.calendar_view);
    }

    private void addEvents() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                startActivity(intent);
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
//                Toast.makeText(getContext(), dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
                CalendarUtil.getInstance().getCalendar().set(Calendar.YEAR, year);
                CalendarUtil.getInstance().getCalendar().set(Calendar.MONTH, month);
                CalendarUtil.getInstance().getCalendar().set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String date = CalendarUtil.getInstance().getSdfDayMonthYear()
                        .format(CalendarUtil.getInstance().getCalendar().getTime());

                EventRepository.getEventsOnDate(new EventRepository.MyEventCallback() {
                    @Override
                    public void onCallback(ArrayList<Event> eventList) {
                        currentDateEvents = eventList;
                        Log.d("debug", "EventManagementFragment: onSelectedDayChanged: currentDateEvents size = "
                        +currentDateEvents.size());
                        eventAdapter.notifyDataSetChanged();
                    }
                }, date);
            }
        });

    }

}

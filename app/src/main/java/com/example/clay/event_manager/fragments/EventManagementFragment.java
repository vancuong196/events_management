package com.example.clay.event_manager.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventManagementFragment extends Fragment {


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
        getEmployeeListFromServer();
    }





    Button menuButton;
    FloatingActionButton addButton;

    ListView eventsListView;

    CalendarView calendarView;

    public static ArrayList<Employee> employeeList;

    CollectionReference database;

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
                Toast.makeText(getContext(), dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getEmployeeListFromServer() {
        employeeList = new ArrayList<>();
        FirebaseApp.initializeApp(getActivity());
        database = FirebaseFirestore.getInstance().collection("nhanvien");
        database.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> tempHashMap = document.getData();
                        Employee tempEmployee = new Employee(document.getId(),
                                (String) tempHashMap.get("hoten"), (String) tempHashMap.get("chuyenmon"),
                                (String) tempHashMap.get("cmnd"), (String) tempHashMap.get("ngaysinh"),
                                (String) tempHashMap.get("sdt"), (String) tempHashMap.get("email"));
                        employeeList.add(tempEmployee);

                    }
                }
            }
        });
    }




}

package com.example.clay.event_manager.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.adapters.ViewSalaryAdapter;
import com.example.clay.event_manager.adapters.ViewScheduleAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.models.Schedule;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;
import com.example.clay.event_manager.repositories.ScheduleRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewEventActivity extends AppCompatActivity {
    Button addReminderButton, viewScheduleButton;
    TextView titleEditText, timeEditText, locationEditText, noteEditText;
    CustomListView employeeListView, reminderListView;
    android.support.v7.widget.Toolbar toolbar;

    HashMap<String, Salary> salaries;
    ViewSalaryAdapter viewSalaryAdapter;
    Event selectedEvent;
    String eventId;

    ArrayList<Schedule> schedules;
    ViewScheduleAdapter viewScheduleAdapter;

    int RESULT_FROM_EDIT_EVENT_INTENT = 3;
    int RESULT_FROM_EDIT_SALARY_INTENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        connectViews();

        eventId = getIntent().getStringExtra("eventId");
        selectedEvent = EventRepository.getInstance(null).getAllEvents().get(eventId);
        salaries = SalaryRepository.getInstance(null).getSalariesByEventId(eventId);
        schedules = ScheduleRepository.getInstance(null).getSchedulesInArrayListByEventId(eventId);
        Log.d("debug", "got " + schedules.size() + " schedules");
        viewSalaryAdapter = new ViewSalaryAdapter(this, salaries);
        viewScheduleAdapter = new ViewScheduleAdapter(this, schedules);
        employeeListView.setAdapter(viewSalaryAdapter);

        addEvents();
        fillInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //Add events for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Xóa sự kiện
        if (id == R.id.view_event_action_delete_event) {
            Log.d("debug", "deleting " + eventId);
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Xóa sự kiện")
                    .setMessage("Bạn có chắc chắn không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("delete?", true);
                            intent.putExtra("eventId", eventId);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent = new Intent();
//                            intent.putExtra("deleted?",false);
//                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .show();
            return true;
        }

        //Chỉnh sửa lương
        if(id==R.id.view_event_action_edit_salaries) {
            Intent intent = new Intent(this, EditSalaryFromEventDetailsActivity.class);
            intent.putExtra("eventId", eventId);
            startActivityForResult(intent, RESULT_FROM_EDIT_SALARY_INTENT);
            return true;
        }

        //Chỉnh sửa sự kiện
        if (id == R.id.view_event_action_edit_event) {
            Intent intent = new Intent(this, EditEventActivity.class);
            intent.putExtra("eventId", eventId);
            startActivityForResult(intent, RESULT_FROM_EDIT_EVENT_INTENT);
            return true;
        }
        //Gửi thông báo
        if (id == R.id.view_event_action_send_notification) {
            Toast.makeText(this, "Gửi thông báo cho nhân viên", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillInformation() {
        titleEditText.setText(selectedEvent.getTen());
        timeEditText.setText(selectedEvent.getGioBatDau() + " - " + selectedEvent.getNgayBatDau() + "\n"
                + selectedEvent.getGioKetThuc() + " - " + selectedEvent.getNgayKetThuc());
        locationEditText.setText(selectedEvent.getDiaDiem());
        noteEditText.setText(selectedEvent.getGhiChu());

    }

    private void connectViews() {
        toolbar = (Toolbar) findViewById(R.id.view_event_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chi tiết sự kiện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addReminderButton = (Button) findViewById(R.id.view_event_add_reminder_button);
        viewScheduleButton = findViewById(R.id.view_event_schedule_button);

        titleEditText = (TextView) findViewById(R.id.view_event_title_text_view);
        timeEditText = (TextView) findViewById(R.id.view_event_time_text_view);
        locationEditText = (TextView) findViewById(R.id.view_event_location_text_view);
        noteEditText = (TextView) findViewById(R.id.view_event_note_text_view);

        employeeListView = (CustomListView) findViewById(R.id.view_event_employees_listview);
        reminderListView = (CustomListView) findViewById(R.id.view_event_reminder_listview);
    }

    private void addEvents() {
        viewScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewScheduleDialog();
            }
        });
    }

    private void openViewScheduleDialog() {
        final Dialog viewScheduleDialog = new Dialog(this);
        viewScheduleDialog.setContentView(R.layout.dialog_view_schedule);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(viewScheduleDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //Connect views
        final ListView viewScheduleListView = viewScheduleDialog.findViewById(R.id.view_schedule_dialog_schedule_list_view);
        Button backButton = viewScheduleDialog.findViewById(R.id.back_button);

        viewScheduleListView.setAdapter(viewScheduleAdapter);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewScheduleDialog.dismiss();
            }
        });

        if (!isFinishing()) {
            viewScheduleDialog.show();
            viewScheduleDialog.getWindow().setAttributes(lWindowParams);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FROM_EDIT_EVENT_INTENT && resultCode == RESULT_OK) {
            if(data.getBooleanExtra("edit event", false)) {
                if(data.getBooleanExtra("edit event succeed", false)) {
                    selectedEvent = EventRepository.getInstance(null).getAllEvents().get(eventId);
                    fillInformation();
                    salaries = SalaryRepository.getInstance(null).getSalariesByEventId(eventId);
                    viewSalaryAdapter.notifyDataSetChanged(salaries);
                    Toast.makeText(this, "Cập nhật sự kiện thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật sự kiện thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == RESULT_FROM_EDIT_SALARY_INTENT && resultCode == RESULT_OK) {
            if(data.getBooleanExtra("edit salaries", false)) {
                if(data.getBooleanExtra("edit salaries succeed", false)) {
                    fillInformation();
                    salaries = SalaryRepository.getInstance(null).getSalariesByEventId(eventId);
                    viewSalaryAdapter.notifyDataSetChanged(salaries);
                    Toast.makeText(this, "Cập nhật lương thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật lương thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

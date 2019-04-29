package com.example.clay.event_manager.activities;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.adapters.ViewSalaryAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;

import java.util.HashMap;

public class EventDetailsActivity extends AppCompatActivity {
    Button addReminderButton;
    TextView titleEditText, timeEditText, locationEditText, noteEditText;
    CustomListView employeeListView, reminderListView;
    android.support.v7.widget.Toolbar toolbar;

    HashMap<String, Salary> salaries;
    ViewSalaryAdapter viewSalaryAdapter;
    Event selectedEvent;
    String eventId;

    int RESULT_FROM_EDIT_EVENT_INTENT = 3;
    int RESULT_FROM_EDIT_SALARY_INTENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        connectViews();

        eventId = getIntent().getStringExtra("eventId");
        selectedEvent = EventRepository.getInstance(null).getAllEvents().get(eventId);
        salaries = SalaryRepository.getInstance(null).getSalariesByEventId(eventId);
        Log.d("debug", "EventDetailsActivity: salaries.size() = " + salaries.size());
        viewSalaryAdapter = new ViewSalaryAdapter(this, salaries);
        employeeListView.setAdapter(viewSalaryAdapter);

        addEvents();
        fillInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //Add events for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Xóa sự kiện
        if (id == R.id.event_details_action_delete_event) {
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
        if(id==R.id.event_details_action_edit_salaries) {
            Intent intent = new Intent(this, EditSalaryFromEventDetailsActivity.class);
            intent.putExtra("eventId", eventId);
            startActivityForResult(intent, RESULT_FROM_EDIT_SALARY_INTENT);
            return true;
        }

        //Chỉnh sửa sự kiện
        if (id == R.id.event_details_action_edit_event) {
            Intent intent = new Intent(this, EditEventActivity.class);
            intent.putExtra("eventId", eventId);
            startActivityForResult(intent, RESULT_FROM_EDIT_EVENT_INTENT);
            return true;
        }
        //Gửi thông báo
        if (id == R.id.event_details_action_send_notification) {
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
        toolbar = (Toolbar) findViewById(R.id.event_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chi tiết sự kiện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addReminderButton = (Button) findViewById(R.id.event_details_add_reminder_button);

        titleEditText = (TextView) findViewById(R.id.event_details_title_text_view);
        timeEditText = (TextView) findViewById(R.id.event_details_time_text_view);
        locationEditText = (TextView) findViewById(R.id.event_details_location_text_view);
        noteEditText = (TextView) findViewById(R.id.event_details_note_text_view);

        employeeListView = (CustomListView) findViewById(R.id.event_details_employees_listview);
        reminderListView = (CustomListView) findViewById(R.id.event_details_reminder_listview);
    }

    private void addEvents() {

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

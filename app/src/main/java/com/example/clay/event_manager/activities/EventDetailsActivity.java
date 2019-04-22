package com.example.clay.event_manager.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.left.R;

public class EventDetailsActivity extends AppCompatActivity {
    Button addReminderButton;
    EditText titleEditText, timeEditText, locationEditText, noteEditText;
    ListView employeeListView, reminderListView;
    android.support.v7.widget.Toolbar toolbar;

    int position;
    boolean deleteEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        deleteEvent = false;
        connectViews();
        addEvents();
        fillInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Xóa sự kiện
        if (id == R.id.event_details_action_delete_event) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Xóa sự kiện")
                    .setMessage("Bạn có chắc chắn không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteEvent = true;
                            finish();
                        }

                    })
                    .setNegativeButton("Không", null)
                    .show();
            if(deleteEvent) {
                EventRepository.getInstance(null).deleteEvent(position, this);
                finish();
            }
            return true;
        }
        //Chỉnh sửa sự kiện
        if (id == R.id.event_details_action_edit_event) {

            return true;
        }
        //Gửi thông báo
        if (id == R.id.event_details_action_send_notification) {
            Toast.makeText(this,"Gửi thông báo cho nhân viên",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillInformation() {
        position = getIntent().getIntExtra("position", 0);
        Event selectedEvent = EventRepository.getInstance(null).getAllEvents().get(position);

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

        titleEditText = (EditText) findViewById(R.id.event_details_title_edit_text);
        timeEditText = (EditText) findViewById(R.id.event_details_time_edit_text);
        locationEditText = (EditText) findViewById(R.id.event_details_location_edit_text);
        noteEditText = (EditText) findViewById(R.id.event_details_note_edit_text);

        employeeListView = (ListView) findViewById(R.id.event_details_employees_listview);
        reminderListView = (ListView) findViewById(R.id.event_details_reminder_listview);
    }

    private void addEvents() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

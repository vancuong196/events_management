package com.example.clay.event_manager.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.adapters.EditSalaryAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class EditSalaryFromEventDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;

    TextView titleEditText, timeEditText, locationEditText, noteEditText;
    CustomListView salaryListView;

    HashMap<String, Salary> salaries;
    ArrayList<String> salariesIds;
    EditSalaryAdapter editSalaryAdapter;
    String eventId;
    Event event;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_salary_from_event_details);

        context = this;
        connectViews();

        eventId = getIntent().getStringExtra("eventId");
        event = EventRepository.getInstance(null).getAllEvents().get(eventId);
        salaries = SalaryRepository.getInstance(null).getSalariesByEventId(eventId);
        salariesIds = new ArrayList<>(salaries.keySet());

        Log.d("debug", "EventDetailsActivity: salaries.size() = " + salaries.size());

        editSalaryAdapter = new EditSalaryAdapter(this, salariesIds);
        salaryListView.setAdapter(editSalaryAdapter);

        addEvents();
        fillInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_salary_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit_salary_action_save_salaries) {
            saveSalaries();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSalaries() {
        for (int i = 0; i < salaryListView.getChildCount(); i++) {
            EditText salaryEditText = salaryListView.getChildAt(i).findViewById(R.id.edit_salary_salary_edit_text);
            CheckBox isPaidCheckBox = salaryListView.getChildAt(i).findViewById(R.id.edit_salary_paid_checkbox);

            salaries.get(salariesIds.get(i)).setSalary(Integer.parseInt(salaryEditText.getText().toString()));
            salaries.get(salariesIds.get(i)).setPaid(isPaidCheckBox.isChecked());
        }
        ArrayList<Salary> salariesList = new ArrayList<>(salaries.values());
        SalaryRepository.getInstance(null).updateSalaries(salariesList, new SalaryRepository.MyUpdateSalariesCallback() {
            @Override
            public void onCallback(boolean updateSucceed) {
                Intent intent = new Intent();
                intent.putExtra("edit salaries", true);
                intent.putExtra("edit salaries succeed", updateSucceed);
                setResult(RESULT_OK, intent);
                Log.d("debug", "EditEventActivity: update salaries complete");
                ((Activity) context).finish();
            }
        });
    }

    private void connectViews() {
        toolbar = findViewById(R.id.edit_salary_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chỉnh sửa lương");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleEditText = findViewById(R.id.edit_salary_from_event_details_title_text_view);
        timeEditText = findViewById(R.id.edit_salary_from_event_details_time_text_view);
        locationEditText = findViewById(R.id.edit_salary_from_event_details_location_text_view);
        noteEditText = findViewById(R.id.edit_salary_from_event_details_note_text_view);
        salaryListView = findViewById(R.id.edit_salary_from_event_details_salary_list_view);
    }

    private void addEvents() {

    }

    private void fillInformation() {
        titleEditText.setText(event.getTen());
        timeEditText.setText(event.getGioBatDau() + " - " + event.getNgayBatDau() + "\n"
                + event.getGioKetThuc() + " - " + event.getNgayKetThuc());
        locationEditText.setText(event.getDiaDiem());
        noteEditText.setText(event.getGhiChu());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

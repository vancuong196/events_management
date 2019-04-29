package com.example.clay.event_manager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.adapters.DeleteEmployeeAdapter;
import com.example.clay.event_manager.adapters.SelectEmployeeAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {
    Toolbar toolbar;

    EditText titleEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText,
            locationEditText, noteEditText;
    TextView startDowTextView, endDowTextView;
    Button addEmployeeButton;
    CustomListView deleteEmployeeListView;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    View currentView;

    ArrayList<String> selectedEmployeesIds;
    DeleteEmployeeAdapter deleteAdapter;
    SelectEmployeeAdapter selectAdapder;

    Calendar calendar = Calendar.getInstance();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        context = this;

        connectViews();
        addEvents();

        selectedEmployeesIds = new ArrayList<>();

        deleteAdapter = new DeleteEmployeeAdapter(this, selectedEmployeesIds);
        selectAdapder = new SelectEmployeeAdapter(this, selectedEmployeesIds);

        deleteEmployeeListView.setAdapter(deleteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.add_event_action_add_event) {
            addEvent();
        }
        return super.onOptionsItemSelected(item);
    }

    //    public static void setSelectedEmployees(ArrayList<Employee> selectedEmployees) {
//        AddEventActivity.selectedEmployees.clear();
//        AddEventActivity.selectedEmployees.addAll(selectedEmployees);
//    }

    private void connectViews() {
        toolbar = findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm sự kiện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
        startTimeEditText = (EditText) findViewById(R.id.start_time_edit_text);
        endDateEditText = (EditText) findViewById(R.id.end_date_edit_text);
        endTimeEditText = (EditText) findViewById(R.id.end_time_edit_text);
        locationEditText = (EditText) findViewById(R.id.location_edit_text);
        noteEditText = (EditText) findViewById(R.id.note_edit_text);

        startDowTextView = (TextView) findViewById(R.id.start_dow_textview);
        endDowTextView = (TextView) findViewById(R.id.end_dow_textview);

        addEmployeeButton = (Button) findViewById(R.id.add_employee_button);

        deleteEmployeeListView = (CustomListView) findViewById(R.id.employees_listview);
    }

    private void addEvent() {
        if (!titleEditText.getText().toString().isEmpty() &&
                !startDateEditText.getText().toString().isEmpty() &&
                !endDateEditText.getText().toString().isEmpty() &&
                !startTimeEditText.getText().toString().isEmpty() &&
                !endTimeEditText.getText().toString().isEmpty()) {

            Event event = new Event("",
                    titleEditText.getText().toString(),
                    startDateEditText.getText().toString(),
                    endDateEditText.getText().toString(),
                    startTimeEditText.getText().toString(),
                    endTimeEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    noteEditText.getText().toString());

            final ArrayList<Salary> salaries = new ArrayList<>();
            Log.d("debug", "AddEventActivity: deleteEmployeeListView.getChildCount() = " + deleteEmployeeListView.getChildCount());
            for (int i = 0; i < deleteEmployeeListView.getChildCount(); i++) {
                Salary tempSalary = new Salary("", "",
                        selectedEmployeesIds.get(i),
                        0,
                        false);
                salaries.add(tempSalary);
            }
            Log.d("debug", "AddEventActivity: salaries.size() = " + salaries.size());
            //Add event to sukien collection & salaries to luong collection
            EventRepository.getInstance(null).addEventToDatabase(event, salaries, new EventRepository.MyAddEventCallback() {
                @Override
                public void onCallback(String eventId) {
                    Log.d("debug", "add event salaries size = " + salaries.size());
                    Intent intent = new Intent();
                    intent.putExtra("added?", true);
                    setResult(RESULT_OK, intent);
                    ((Activity) context).finish();
                }
            });
        } else {
            if (titleEditText.getText().toString().isEmpty()) {
                titleEditText.setError("Xin mời nhập");
            } else {
                titleEditText.setError(null);
            }
            if (startDateEditText.getText().toString().isEmpty()) {
                startDateEditText.setError("Xin mời nhập");
            } else {
                startDateEditText.setError(null);
            }
            if (endDateEditText.getText().toString().isEmpty()) {
                endDateEditText.setError("Xin mời nhập");
            } else {
                endDateEditText.setError(null);
            }
            if (startTimeEditText.getText().toString().isEmpty()) {
                startTimeEditText.setError("Xin mời nhập");
            } else {
                startTimeEditText.setError(null);
            }
            if (endTimeEditText.getText().toString().isEmpty()) {
                endTimeEditText.setError("Xin mời nhập");
            } else {
                endTimeEditText.setError(null);
            }
        }
    }

    private void addEvents() {
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddEmployeeDialog();
            }
        });

        //Date set and Time set Listeners
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

//                Update TextEdits & TextViews;
                if (currentView == startDateEditText) {
                    startDateEditText.setText(CalendarUtil.getInstance().getSdfDayMonthYear()
                            .format(calendar.getTime()));
                    startDowTextView.setText(CalendarUtil.getInstance().getSdfDayOfWeek()
                            .format(calendar.getTime()));
                } else {
                    endDateEditText.setText(CalendarUtil.getInstance().getSdfDayMonthYear()
                            .format(calendar.getTime()));
                    endDowTextView.setText(CalendarUtil.getInstance().getSdfDayOfWeek()
                            .format(calendar.getTime()));
                }

//                Set (end time = start time) if (end date == start date) and (end time < start time)
                try {
                    if (endDateEditText.getText().toString().equals(startDateEditText.getText().toString())
                            && CalendarUtil.getInstance().getSdfTime().parse(endTimeEditText.getText().toString()).getTime() <
                            CalendarUtil.getInstance().getSdfTime().parse(startTimeEditText.getText().toString()).getTime()) {
                        endTimeEditText.setText(startTimeEditText.getText().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (currentView == startTimeEditText) {
                    startTimeEditText.setText(CalendarUtil.getInstance().getSdfTime().format(calendar.getTime()));
                } else {
                    endTimeEditText.setText(CalendarUtil.getInstance().getSdfTime().format(calendar.getTime()));
                }
                boolean increaseEndDateCondition = !(endTimeEditText.getText().toString().isEmpty() ||
                        startTimeEditText.getText().toString().isEmpty());
                try {
                    if (increaseEndDateCondition
                            && startDateEditText.getText().toString().equals(endDateEditText.getText().toString())
                            && (CalendarUtil.getInstance().getSdfTime().parse(startTimeEditText.getText().toString()).getTime()
                            > CalendarUtil.getInstance().getSdfTime().parse(endTimeEditText.getText().toString()).getTime())) {
                        calendar.setTime(CalendarUtil.getInstance().getSdfDayMonthYear()
                                .parse(endDateEditText.getText().toString()));
                        calendar.add(Calendar.DATE, 1);
                        endDateEditText.setText(CalendarUtil.getInstance().getSdfDayMonthYear().format(calendar.getTime()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //set start Date, start Time, end Date, end Time
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if (!startDateEditText.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(CalendarUtil.getInstance().getSdfDayMonthYear()
                                .parse(startDateEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = startDateEditText;
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, dateSetListener, y,
                        m, d);
                if (!endDateEditText.getText().toString().isEmpty()) {
                    try {
                        datePickerDialog.getDatePicker().setMaxDate(CalendarUtil.getInstance()
                                .getSdfDayMonthYear().parse(endDateEditText.getText().toString()).getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                datePickerDialog.show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if (!endDateEditText.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(CalendarUtil.getInstance().getSdfDayMonthYear()
                                .parse(endDateEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = endDateEditText;
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, dateSetListener, y,
                        m, d);
                if (!startDateEditText.getText().toString().isEmpty()) {
                    try {
                        datePickerDialog.getDatePicker().setMinDate(CalendarUtil.getInstance()
                                .getSdfDayMonthYear().parse(startDateEditText.getText().toString()).getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                datePickerDialog.show();
            }
        });
        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if (!startTimeEditText.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(CalendarUtil.getInstance().getSdfTime()
                                .parse(startTimeEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = startTimeEditText;
                int HH = calendar.get(Calendar.HOUR_OF_DAY);
                int mm = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(AddEventActivity.this, timeSetListener, HH,
                        mm, false).show();
            }
        });
        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if (!endTimeEditText.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(CalendarUtil.getInstance().getSdfTime()
                                .parse(endTimeEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = endTimeEditText;
                int HH = calendar.get(Calendar.HOUR_OF_DAY);
                int mm = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(AddEventActivity.this, timeSetListener, HH,
                        mm, false).show();
            }
        });
    }

    private void openAddEmployeeDialog() {
        final Dialog addEmployeeDialog = new Dialog(this);
        addEmployeeDialog.setContentView(R.layout.dialog_select_employee);

        //Connect views
        final ListView selectEmployeeListView = (ListView) addEmployeeDialog.findViewById(R.id.select_employee_listview);
        Button cancelButton = (Button) addEmployeeDialog.findViewById(R.id.cancel_button);
        Button okButton = (Button) addEmployeeDialog.findViewById(R.id.ok_button);

        selectEmployeeListView.setAdapter(selectAdapder);

        //Add events
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox tempCheckbox;
                for (int i = 0; i < selectEmployeeListView.getChildCount(); i++) {
                    tempCheckbox = selectEmployeeListView.getChildAt(i).findViewById(R.id.add_employee_checkbox);
                    if (tempCheckbox.isChecked() &&
                            !selectedEmployeesIds.contains(selectAdapder.getAllEmployeesIds()[i])) {
                        selectedEmployeesIds.add(selectAdapder.getAllEmployeesIds()[i]);
                    }
                    if (!tempCheckbox.isChecked() &&
                            selectedEmployeesIds.contains(selectAdapder.getAllEmployeesIds()[i])) {
                        selectedEmployeesIds.remove(selectAdapder.getAllEmployeesIds()[i]);
                    }
                }
                Log.d("debug", "selected " + selectedEmployeesIds.size() + " employees");
                deleteAdapter.notifyDataSetChanged();
                addEmployeeDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmployeeDialog.dismiss();
            }
        });
        if (!isFinishing()) {
            addEmployeeDialog.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

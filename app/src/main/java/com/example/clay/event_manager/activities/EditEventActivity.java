package com.example.clay.event_manager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.adapters.DeleteSalaryAdapter;
import com.example.clay.event_manager.adapters.EditSalaryAdapter;
import com.example.clay.event_manager.adapters.SelectEmployeeInEditEventAdapter;
import com.example.clay.event_manager.customlistviews.CustomListView;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;
import com.example.clay.event_manager.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity {
    EditText titleEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText,
            locationEditText, noteEditText;
    TextView startDowTextView, endDowTextView;
    Button addEmployeesButton, cancelButton, okButton;
    CustomListView salariesListView;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    View currentView;

    String eventId;
    Event event;
    ArrayList<String> selectedSalariesIds;
    DeleteSalaryAdapter deleteSalaryAdapter;
    SelectEmployeeInEditEventAdapter selectEmployeeAdapter;

    Calendar calendar = Calendar.getInstance();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        context = this;

        connectViews();

        eventId = getIntent().getStringExtra("eventId");
        event = EventRepository.getInstance(null).getAllEvents().get(eventId);
        selectedSalariesIds = new ArrayList<>(SalaryRepository.getInstance(null).getSalariesByEventId(eventId).keySet());

        deleteSalaryAdapter = new DeleteSalaryAdapter(this, selectedSalariesIds);
        selectEmployeeAdapter = new SelectEmployeeInEditEventAdapter(this, selectedSalariesIds);

        fillInformation();
        addEvents();
    }

    private void connectViews() {
        titleEditText = findViewById(R.id.event_edit_title_edit_text);
        startDateEditText = findViewById(R.id.event_edit_start_date_edit_text);
        startTimeEditText = findViewById(R.id.event_edit_start_time_edit_text);
        endDateEditText = findViewById(R.id.event_edit_end_date_edit_text);
        endTimeEditText = findViewById(R.id.event_edit_end_time_edit_text);

        startDowTextView = findViewById(R.id.event_edit_start_dow_textview);
        endDowTextView = findViewById(R.id.event_edit_end_dow_textview);

        locationEditText = findViewById(R.id.event_edit_location_edit_text);
        noteEditText = findViewById(R.id.event_edit_note_edit_text);

        addEmployeesButton = findViewById(R.id.event_edit_add_employee_button);
        cancelButton = findViewById(R.id.event_edit_cancel_button);
        okButton = findViewById(R.id.event_edit_ok_button);

        salariesListView = findViewById(R.id.event_edit_salaries_list_view);
    }

    private void fillInformation() {
        titleEditText.setText(event.getTen());
        startDateEditText.setText(event.getNgayBatDau());
        startTimeEditText.setText(event.getGioBatDau());
        endDateEditText.setText(event.getNgayKetThuc());
        endTimeEditText.setText(event.getGioKetThuc());

        try {
            startDowTextView.setText(CalendarUtil.getInstance().getSdfDayOfWeek()
                    .format(CalendarUtil.getInstance().getSdfDayMonthYear().parse(startDateEditText.getText().toString())));
            endDowTextView.setText(CalendarUtil.getInstance().getSdfDayOfWeek()
                    .format(CalendarUtil.getInstance().getSdfDayMonthYear().parse(endDateEditText.getText().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationEditText.setText(event.getDiaDiem());
        noteEditText.setText(event.getGhiChu());

        salariesListView.setAdapter(deleteSalaryAdapter);
    }

    private void addEvents() {
        addEmployeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddEmployeeDialog();
            }
        });

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, dateSetListener, y,
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, dateSetListener, y,
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
                new TimePickerDialog(EditEventActivity.this, timeSetListener, HH,
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
                new TimePickerDialog(EditEventActivity.this, timeSetListener, HH,
                        mm, false).show();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!titleEditText.getText().toString().isEmpty() &&
                        !startDateEditText.getText().toString().isEmpty() &&
                        !endDateEditText.getText().toString().isEmpty() &&
                        !startTimeEditText.getText().toString().isEmpty() &&
                        !endTimeEditText.getText().toString().isEmpty()) {
                    Event event = new Event(eventId,
                            titleEditText.getText().toString(),
                            startDateEditText.getText().toString(),
                            endDateEditText.getText().toString(),
                            startTimeEditText.getText().toString(),
                            endTimeEditText.getText().toString(),
                            locationEditText.getText().toString(),
                            noteEditText.getText().toString());

                    ArrayList<Salary> salaries = new ArrayList<>();
                    for (int i = 0; i < salariesListView.getChildCount(); i++) {
                        EditText salaryEditText = salariesListView.getChildAt(i)
                                .findViewById(R.id.delete_salary_salary_edit_text);
                        int salaryAmount;
                        if(salaryEditText.getText().toString().isEmpty()) {
                            salaryAmount = 0;
                        } else {
                            salaryAmount = Integer.parseInt(salaryEditText.getText().toString());
                        }
                        Salary tempSalary = new Salary("", eventId,
                                SalaryRepository.getInstance(null).getAllSalaries()
                                        .get(deleteSalaryAdapter.getSalariesIds().get(i)).getEmployeeId(),
                                salaryAmount,
                                false);
                        salaries.add(tempSalary);
                    }

                    //Save edited event to sukien collection
                    EventRepository.getInstance(null).updateEventToDatabase(event, salaries, new EventRepository.MyUpdateEventCallback() {
                        @Override
                        public void onCallback(boolean updateSucceed) {
                            Intent intent = new Intent();
                            intent.putExtra("edited?", true);
                            intent.putExtra("editSucceed", updateSucceed);
                            setResult(RESULT_OK, intent);
                            Log.d("debug", "EditEventActivity: update event complete");
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
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("edited?", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void openAddEmployeeDialog() {
        final Dialog addEmployeeDialog = new Dialog(this);
        addEmployeeDialog.setContentView(R.layout.dialog_select_employee);

        final ListView selectEmployeeListView = (ListView) addEmployeeDialog.findViewById(R.id.select_employee_listview);
        Button cancelButton = (Button) addEmployeeDialog.findViewById(R.id.cancel_button);
        Button okButton = (Button) addEmployeeDialog.findViewById(R.id.ok_button);

        selectEmployeeListView.setAdapter(selectEmployeeAdapter);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox tempCheckbox;
                ArrayList<String> selectedEmployessIds = EmployeeRepository.getInstance(null).getEmployeesIdsFromSalariesIds(selectedSalariesIds);
                for (int i = 0; i < selectEmployeeListView.getChildCount(); i++) {
                    tempCheckbox = selectEmployeeListView.getChildAt(i).findViewById(R.id.add_employee_checkbox);
                    if (tempCheckbox.isChecked() &&
                            !selectedEmployessIds.contains(selectEmployeeAdapter.getAllEmployeesIds()[i])) {
                        selectedSalariesIds.add(selectEmployeeAdapter.getAllEmployeesIds()[i]);
                    }
                    if (!tempCheckbox.isChecked() &&
                            selectedEmployessIds.contains(selectEmployeeAdapter.getAllEmployeesIds()[i])) {
                        selectedSalariesIds.remove(selectEmployeeAdapter.getAllEmployeesIds()[i]);
                    }
                }
                Log.d("debug", "selected " + selectedSalariesIds.size() + " employees");
                deleteSalaryAdapter.notifyDataSetChanged();
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
}

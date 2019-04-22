package com.example.clay.event_manager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clay.event_manager.adapters.DeleteEmployeeAdapter;
import com.example.clay.event_manager.adapters.SelectEmployeeAdapter;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.EventRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.left.R;

import java.util.Calendar;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {


    EditText titleEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText,
            locationEditText, noteEditText;
    TextView startDowTextView, endDowTextView;
    Button addEmployeeButton, cancelButton, okButton;
    ListView deleteEmployeeListView;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    View currentView;

    HashMap<String, Employee> selectedEmployees;
    DeleteEmployeeAdapter deleteAdapter;
    SelectEmployeeAdapter selectAdapder;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        connectViews();
        addEvents();

        selectedEmployees = new HashMap<>();

        deleteAdapter = new DeleteEmployeeAdapter(this, selectedEmployees);
        selectAdapder = new SelectEmployeeAdapter(this, selectedEmployees, EmployeeRepository
                .getInstance(null).getAllEmployees());

        deleteEmployeeListView.setAdapter(deleteAdapter);
    }

//    public static void setSelectedEmployees(ArrayList<Employee> selectedEmployees) {
//        AddEventActivity.selectedEmployees.clear();
//        AddEventActivity.selectedEmployees.addAll(selectedEmployees);
//    }

    private void connectViews() {
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
        cancelButton = (Button) findViewById(R.id.cancel_button);
        okButton = (Button) findViewById(R.id.ok_button);

        deleteEmployeeListView = (ListView) findViewById(R.id.employees_listview);
    }

    private void addEvents() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!titleEditText.getText().toString().isEmpty() &&
                        !startDateEditText.getText().toString().isEmpty() &&
                        !endDateEditText.getText().toString().isEmpty() &&
                        !startTimeEditText.getText().toString().isEmpty() &&
                        !endTimeEditText.getText().toString().isEmpty() &&
                        !locationEditText.getText().toString().isEmpty()) {

                    //Add event to sukien collection
                    Event event = new Event("",
                            titleEditText.getText().toString(),
                            startDateEditText.getText().toString(),
                            endDateEditText.getText().toString(),
                            startTimeEditText.getText().toString(),
                            endTimeEditText.getText().toString(),
                            locationEditText.getText().toString(),
                            noteEditText.getText().toString());

                    //Add salary record to luong collection
                    EventRepository.addEventToDatabase(event, new EventRepository.MyAddEventCallback() {
                        @Override
                        public void onCallback(String eventId) {
                            for (int i = 0; i < deleteEmployeeListView.getChildCount(); i++) {
                                EditText salaryEditText = deleteEmployeeListView.getChildAt(i)
                                        .findViewById(R.id.delete_employee_salary_edit_text);
                                Salary salary = new Salary("", eventId,
                                        deleteAdapter.getSelectedEmployeesIds()[i],
                                        Integer.parseInt(salaryEditText.getText().toString()));
                                SalaryRepository.addSalaryToDatabase(salary);
                            }
                        }
                    });
                    finish();
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
                    if (locationEditText.getText().toString().isEmpty()) {
                        locationEditText.setError("Xin mời nhập");
                    } else {
                        locationEditText.setError(null);
                    }
                }
            }
        });
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
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

        final ListView selectEmployeeListView = (ListView) addEmployeeDialog.findViewById(R.id.select_employee_listview);
        Button cancelButton = (Button) addEmployeeDialog.findViewById(R.id.cancel_button);
        Button okButton = (Button) addEmployeeDialog.findViewById(R.id.ok_button);

        selectEmployeeListView.setAdapter(selectAdapder);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox tempCheckbox;
                for (int i = 0; i < selectEmployeeListView.getChildCount(); i++) {
                    tempCheckbox = selectEmployeeListView.getChildAt(i).findViewById(R.id.add_employee_checkbox);
                    if (tempCheckbox.isChecked() &&
                            selectedEmployees.get(selectAdapder.getAllEmployeesIds()[i]) == null) {
                        selectedEmployees.put(selectAdapder.getAllEmployeesIds()[i],
                                EmployeeRepository.getInstance(null).getAllEmployees().get(selectAdapder.getAllEmployeesIds()[i]));
                    }
                    if (!tempCheckbox.isChecked() &&
                            selectedEmployees.get(selectAdapder.getAllEmployeesIds()[i]) != null) {
                        selectedEmployees.remove(selectAdapder.getAllEmployeesIds()[i]);
                    }
                }
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
}

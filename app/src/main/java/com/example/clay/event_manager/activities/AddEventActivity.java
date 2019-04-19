package com.example.clay.event_manager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clay.event_manager.adapters.DeleteEmployeeAdapter;
import com.example.clay.event_manager.adapters.SelectEmployeeAdapter;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {


    EditText titleEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText,
    locationEditText;
    TextView startDowTextView, endDowTextView;
    Button addEmployeeButton, cancelButton, okButton;
    ListView employeeListView;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdfDayMonthYear = new SimpleDateFormat("dd/MM/yyyy");;
    SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("EEE");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    View currentView;

    public static ArrayList<Employee> selectedEmployees;
    DeleteEmployeeAdapter deleteAdapter;
    SelectEmployeeAdapter selectAdapder;

    static public ArrayList<Employee> getSelectedEmployees() {
        return selectedEmployees;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_activity);

        connectViews();
        addEvents();

        selectedEmployees = new ArrayList<>();

        deleteAdapter = new DeleteEmployeeAdapter(this);
        selectAdapder = new SelectEmployeeAdapter(this);
        employeeListView.setAdapter(deleteAdapter);
    }

    public static void setSelectedEmployees(ArrayList<Employee> selectedEmployees) {
        AddEventActivity.selectedEmployees.clear();
        AddEventActivity.selectedEmployees.addAll(selectedEmployees);
    }

    private void connectViews() {
        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
        startTimeEditText = (EditText) findViewById(R.id.start_time_edit_text);
        endDateEditText = (EditText) findViewById(R.id.end_date_edit_text);
        endTimeEditText = (EditText) findViewById(R.id.end_time_edit_text);
        locationEditText = (EditText) findViewById(R.id.location_edit_text);

        startDowTextView = (TextView) findViewById(R.id.start_dow_textview);
        endDowTextView = (TextView) findViewById(R.id.end_dow_textview);

        addEmployeeButton = (Button) findViewById(R.id.add_employee_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        okButton = (Button) findViewById(R.id.ok_button);

        employeeListView = (ListView) findViewById(R.id.employees_listview);
    }
    private void addEvents() {
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddEmployeeDialog();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
                if (currentView == startDateEditText) {
                    startDateEditText.setText(sdfDayMonthYear.format(calendar.getTime()));
                    startDowTextView.setText(sdfDayOfWeek.format(calendar.getTime()));
                } else {
                    endDateEditText.setText(sdfDayMonthYear.format(calendar.getTime()));
                    endDowTextView.setText(sdfDayOfWeek.format(calendar.getTime()));
                }
            }
        };
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                if(currentView == startTimeEditText) {
                    startTimeEditText.setText(sdfTime.format(calendar.getTime()));
                } else {
                    endTimeEditText.setText(sdfTime.format(calendar.getTime()));
                }
            }
        };
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                Log.d("debug","startDateEditText clicked");
                if(!startDateEditText.getText().equals("")) {
                    try {
                        calendar.setTime(sdfDayMonthYear.parse(startDateEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                currentView = startDateEditText;
                new DatePickerDialog(AddEventActivity.this, dateSetListener, y,
                        m, d).show();
            }
        });
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if(!endDateEditText.getText().equals("")) {
                    try {
                        calendar.setTime(sdfDayMonthYear.parse(endDateEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = endDateEditText;
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                new DatePickerDialog(AddEventActivity.this, dateSetListener, y,
                        m, d).show();
            }
        });
        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                if(!startTimeEditText.getText().equals("")) {
                    try {
                        calendar.setTime(sdfTime.parse(startTimeEditText.getText().toString()));
                        Log.d("debug","get Time: "+calendar.get(Calendar.HOUR)+
                                ":"+calendar.get(Calendar.MINUTE)+"/"+calendar.get(Calendar.AM_PM));
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
                if(!endTimeEditText.getText().equals("")) {
                    try {
                        calendar.setTime(sdfTime.parse(endTimeEditText.getText().toString()));
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
//        AddEmployeeDialog addEmployeeDialog = new AddEmployeeDialog();
//        addEmployeeDialog.show(getSupportFragmentManager(),"Chọn Nhân sự");
        final Dialog addEmployeeDialog = new Dialog(this);
        addEmployeeDialog.setContentView(R.layout.add_employee_dialog);

        final ListView addListView = (ListView) addEmployeeDialog.findViewById(R.id.add_employee_listview);
        Button cancelButton = (Button) addEmployeeDialog.findViewById(R.id.cancel_button);
        Button okButton = (Button) addEmployeeDialog.findViewById(R.id.ok_button);

//        final SelectEmployeeAdapter selectAdapter = new SelectEmployeeAdapter(this);
        setSelectedEmployees(deleteAdapter.getSelectedEmployees());
        selectAdapder.setSelectedEmployees(selectedEmployees);
        addListView.setAdapter(selectAdapder);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SparseBooleanArray sp = addListView.getCheckedItemPositions();
//                for(int i=0; i<sp.size(); i++) {
//                    if(sp.valueAt(i) == true) {
//                        selectedEmployees.add(MainActivity.employeeList.get(i));
//                    }
//                }
                setSelectedEmployees(selectAdapder.getSelectedEmployees());
                deleteAdapter.notifyDataSetChanged("activity");
                addEmployeeDialog.dismiss();
                Log.d("debug", "size at AddEventActivity = " + selectedEmployees.size());
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmployeeDialog.dismiss();
            }
        });
        if(!isFinishing()) {
            addEmployeeDialog.show();
        }
    }
}

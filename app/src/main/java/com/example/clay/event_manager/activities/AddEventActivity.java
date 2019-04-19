package com.example.clay.event_manager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clay.event_manager.adapters.DeleteEmployeeAdapter;
import com.example.clay.event_manager.adapters.SelectEmployeeAdapter;
import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.example.clay.left.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {


    EditText titleEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText,
    locationEditText, noteEditText;
    TextView startDowTextView, endDowTextView;
    Button addEmployeeButton, cancelButton, okButton;
    ListView employeeListView;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    View currentView;

    ArrayList<String> selectedEmployees;
    DeleteEmployeeAdapter deleteAdapter;
    SelectEmployeeAdapter selectAdapder;

    public ArrayList<String> getSelectedEmployees() {
        return selectedEmployees;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_activity);

        connectViews();
        addEvents();

        selectedEmployees = new ArrayList<>();

        deleteAdapter = new DeleteEmployeeAdapter(this, selectedEmployees);
        selectAdapder = new SelectEmployeeAdapter(this, selectedEmployees);
        employeeListView.setAdapter(deleteAdapter);
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

        employeeListView = (ListView) findViewById(R.id.employees_listview);
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
                    String employeeIds = "";
                    if (selectedEmployees.size() > 0) {
                        for (String position : selectedEmployees) {
                            employeeIds += EventManagementFragment.employeeList
                                    .get(Integer.parseInt(position)).getId() + ",";
                        }
                        employeeIds = employeeIds.substring(0, employeeIds.length() - 2);
                    }
                    HashMap<String, String> data = new HashMap<>();
                    data.put("ten", titleEditText.getText().toString());
                    data.put("ngaybatdau", startDateEditText.getText().toString());
                    data.put("ngayketthuc", endDateEditText.getText().toString());
                    data.put("giobatdau", startTimeEditText.getText().toString());
                    data.put("gioketthuc", endTimeEditText.getText().toString());
                    data.put("diadiem", locationEditText.getText().toString());
                    data.put("nhanvienid", employeeIds);
                    data.put("ghichu", noteEditText.getText().toString());
                    DatabaseAccess.getInstance().getDatabase().collection("sukien").add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddEventActivity.this, "Thêm sự kiện thành công", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddEventActivity.this, "Thêm sự kiện thất bại", Toast.LENGTH_SHORT).show();
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
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                CalendarUtil.getInstance().getCalendar().set(Calendar.YEAR, year);
                CalendarUtil.getInstance().getCalendar().set(Calendar.MONTH, monthOfYear);
                CalendarUtil.getInstance().getCalendar().set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
                if (currentView == startDateEditText) {
                    startDateEditText.setText(CalendarUtil.getInstance().getSdfDayMonthYear()
                            .format(CalendarUtil.getInstance().getCalendar().getTime()));
                    startDowTextView.setText(CalendarUtil.getInstance().getSdfDayOfWeek()
                            .format(CalendarUtil.getInstance().getCalendar().getTime()));
                } else {
                    endDateEditText.setText(CalendarUtil.getInstance().getSdfDayMonthYear()
                            .format(CalendarUtil.getInstance().getCalendar().getTime()));
                    endDowTextView.setText(CalendarUtil.getInstance().getSdfDayOfWeek()
                            .format(CalendarUtil.getInstance().getCalendar().getTime()));
                }
            }
        };
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                CalendarUtil.getInstance().getCalendar().set(Calendar.HOUR_OF_DAY, hourOfDay);
                CalendarUtil.getInstance().getCalendar().set(Calendar.MINUTE, minute);
                if(currentView == startTimeEditText) {
                    startTimeEditText.setText(CalendarUtil.getInstance().getSdfTime()
                            .format(CalendarUtil.getInstance().getCalendar().getTime()));
                } else {
                    endTimeEditText.setText(CalendarUtil.getInstance().getSdfTime()
                            .format(CalendarUtil.getInstance().getCalendar().getTime()));
                }
            }
        };
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtil.getInstance().setCalendar(Calendar.getInstance());
                if(!startDateEditText.getText().equals("")) {
                    try {
                        CalendarUtil.getInstance().getCalendar()
                                .setTime(CalendarUtil.getInstance().getSdfDayMonthYear()
                                        .parse(startDateEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int d = CalendarUtil.getInstance().getCalendar().get(Calendar.DAY_OF_MONTH);
                int m = CalendarUtil.getInstance().getCalendar().get(Calendar.MONTH);
                int y = CalendarUtil.getInstance().getCalendar().get(Calendar.YEAR);
                currentView = startDateEditText;
                new DatePickerDialog(AddEventActivity.this, dateSetListener, y,
                        m, d).show();
            }
        });
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtil.getInstance().setCalendar(Calendar.getInstance());
                if(!endDateEditText.getText().equals("")) {
                    try {
                        CalendarUtil.getInstance().getCalendar()
                                .setTime(CalendarUtil.getInstance().getSdfDayMonthYear()
                                        .parse(endDateEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = endDateEditText;
                int d = CalendarUtil.getInstance().getCalendar().get(Calendar.DAY_OF_MONTH);
                int m = CalendarUtil.getInstance().getCalendar().get(Calendar.MONTH);
                int y = CalendarUtil.getInstance().getCalendar().get(Calendar.YEAR);
                new DatePickerDialog(AddEventActivity.this, dateSetListener, y,
                        m, d).show();
            }
        });
        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtil.getInstance().setCalendar(Calendar.getInstance());
                if(!startTimeEditText.getText().equals("")) {
                    try {
                        CalendarUtil.getInstance().getCalendar()
                                .setTime(CalendarUtil.getInstance().getSdfTime()
                                        .parse(startTimeEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = startTimeEditText;
                int HH = CalendarUtil.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
                int mm = CalendarUtil.getInstance().getCalendar().get(Calendar.MINUTE);
                new TimePickerDialog(AddEventActivity.this, timeSetListener, HH,
                        mm, false).show();
            }
        });
        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtil.getInstance().setCalendar(Calendar.getInstance());
                if(!endTimeEditText.getText().equals("")) {
                    try {
                        CalendarUtil.getInstance().getCalendar()
                                .setTime(CalendarUtil.getInstance().getSdfTime()
                                        .parse(endTimeEditText.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                currentView = endTimeEditText;
                int HH = CalendarUtil.getInstance().getCalendar().get(Calendar.HOUR_OF_DAY);
                int mm = CalendarUtil.getInstance().getCalendar().get(Calendar.MINUTE);
                new TimePickerDialog(AddEventActivity.this, timeSetListener, HH,
                        mm, false).show();
            }
        });
    }
    private void openAddEmployeeDialog() {
        final Dialog addEmployeeDialog = new Dialog(this);
        addEmployeeDialog.setContentView(R.layout.add_employee_dialog);

        final ListView addListView = (ListView) addEmployeeDialog.findViewById(R.id.add_employee_listview);
        Button cancelButton = (Button) addEmployeeDialog.findViewById(R.id.cancel_button);
        Button okButton = (Button) addEmployeeDialog.findViewById(R.id.ok_button);

        addListView.setAdapter(selectAdapder);

        Log.d("debug", "from delAdap: " + deleteAdapter.getSelectedEmployees().size());
        Log.d("debug", "addAct got: " + selectedEmployees.size());
        Log.d("debug", "selAdap got: " + selectAdapder.getSelectedEmployees().size());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox tempCheckbox;
                for(int i = 0; i < addListView.getChildCount(); i++) {
                    tempCheckbox = (CheckBox) addListView.getChildAt(i).findViewById(R.id.add_employee_checkbox);
                    if(tempCheckbox.isChecked() &&
                            !selectedEmployees.contains("" + i)) {
                        selectedEmployees.add("" + i);
                    }
                    if(!tempCheckbox.isChecked() &&
                            selectedEmployees.contains("" + i)) {
                        selectedEmployees.remove("" + i);
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
        if(!isFinishing()) {
            addEmployeeDialog.show();
        }
    }
}

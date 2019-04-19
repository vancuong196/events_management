package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Event;
import com.example.clay.left.R;

import java.util.ArrayList;

public class SelectEmployeeAdapter extends ArrayAdapter<Employee> {

    private final Activity context;
    private ArrayList<Employee> selectedEmployees;

    CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public SelectEmployeeAdapter(Activity context) {
        super(context, R.layout.select_employee_list_item, EventManagementFragment.employeeList);
        this.context = context;
        selectedEmployees = new ArrayList<>();
    }

    public void setSelectedEmployees(ArrayList<Employee> selectedEmployees) {
        this.selectedEmployees.clear();
        this.selectedEmployees.addAll(selectedEmployees);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.select_employee_list_item, null, true);

        TextView hoTenTextView = (TextView) rowView.findViewById(R.id.hoten_textview);
        TextView chuyenMonTextView = (TextView) rowView.findViewById(R.id.chuyenmon_textview);
        CheckBox addEmployeeCheckBox = (CheckBox) rowView.findViewById(R.id.add_employee_checkbox);

        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    selectedEmployees.add(EventManagementFragment.employeeList.get(position));
                    Log.d("debug", "added 1, selected size = " + selectedEmployees.size());
                } else {
                    selectedEmployees.remove(EventManagementFragment.employeeList.get(position));

                    Log.d("debug", "removed 1, selected size = " + selectedEmployees.size());
                }
            }
        };

        addEmployeeCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        hoTenTextView.setText(EventManagementFragment.employeeList.get(position).getHoTen());
        chuyenMonTextView.setText(EventManagementFragment.employeeList.get(position).getChuyenMon());

        if (selectedEmployees.contains(EventManagementFragment.employeeList.get(position))) {
            addEmployeeCheckBox.setOnCheckedChangeListener(null);
            addEmployeeCheckBox.setChecked(true);
            addEmployeeCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        }

        return rowView;
    }

    public ArrayList<Employee> getSelectedEmployees() {
        Log.d("debug", "get selected size = " + selectedEmployees.size());
        return selectedEmployees;
    }
    //    @Override
//    public int getCount() {
//        return MainActivity.employeeList.size();
//    }
//
//    @Override
//    public Employee getItem(int position) {
//        return MainActivity.employeeList.get(position);
//    }
//
//
//    public long getItemId(int position) {
//        return 0;
//    }
}

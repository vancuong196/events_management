package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.left.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectEmployeeAdapter extends BaseAdapter {

    private final Activity context;
    private ArrayList<String> selectedEmployees;

    CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public SelectEmployeeAdapter(Activity context, ArrayList<String> selectedEmployees) {
//        super(context, R.layout.select_employee_list_item, EventManagementFragment.employeeList);
        this.context = context;
        this.selectedEmployees = selectedEmployees;
//        selectedEmployees = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return EventManagementFragment.employeeList.size();
    }

    @Override
    public Object getItem(int i) {
        return EventManagementFragment.employeeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.select_employee_list_item, parent, false);
        }
//        LayoutInflater inflater = context.getLayoutInflater();
//        View rowView = inflater.inflate(R.layout.select_employee_list_item, null, true);

        TextView hoTenTextView = (TextView) view.findViewById(R.id.hoten_textview);
        TextView chuyenMonTextView = (TextView) view.findViewById(R.id.chuyenmon_textview);
        CheckBox addEmployeeCheckBox = (CheckBox) view.findViewById(R.id.add_employee_checkbox);

        hoTenTextView.setText(EventManagementFragment.employeeList.get(position).getHoTen());
        chuyenMonTextView.setText(EventManagementFragment.employeeList.get(position).getChuyenMon());

        //PROBLEM IS HERE!!!
        if(selectedEmployees.size() > 0 && selectedEmployees.contains("" + position)) {
            addEmployeeCheckBox.setChecked(true);
        } else {
            addEmployeeCheckBox.setChecked(false);
        }

//        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b == true) {
//                    selectedEmployees.add(employeeList.get(position));
//                    Log.d("debug", "added 1, selected size = " + selectedEmployees.size());
//                } else {
//                    selectedEmployees.remove(employeeList.get(position));
//                    Log.d("debug", "removed 1, selected size = " + selectedEmployees.size());
//                }
//            }
//        };
//
//        addEmployeeCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        return view;
    }

    public ArrayList<String> getSelectedEmployees() {
//        Log.d("debug", "get selected size = " + selectedEmployees.size());
        return selectedEmployees;
    }
    public void setSelectedEmployees(ArrayList<String> selectedEmployees) {
        this.selectedEmployees.clear();
        this.selectedEmployees.addAll(selectedEmployees);
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

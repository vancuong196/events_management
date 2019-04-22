package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.util.HashMap;

public class SelectEmployeeAdapter extends BaseAdapter {

    private final Activity context;
    private HashMap<String, Employee> selectedEmployees;
    private HashMap<String, Employee> allEmployees;
    private String[] allEmployeesIds;

    public SelectEmployeeAdapter(Activity context, HashMap<String, Employee> selectedEmployees,
                                 HashMap<String, Employee> allEmployees) {
        this.context = context;
        this.selectedEmployees = selectedEmployees;
        this.allEmployees = allEmployees;
        allEmployeesIds = allEmployees.keySet().toArray(new String[allEmployees.size()]);
        Log.d("debug", "SelectEmployeeAdapter: ");
    }

    @Override
    public int getCount() {
        return allEmployees.size();
    }

    @Override
    public Object getItem(int i) {
        return allEmployees.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_select_employee_list_item, parent, false);
        }

        TextView hoTenTextView = (TextView) view.findViewById(R.id.select_hoten_textview);
        TextView chuyenMonTextView = (TextView) view.findViewById(R.id.select_chuyenmon_textview);
        CheckBox addEmployeeCheckBox = (CheckBox) view.findViewById(R.id.add_employee_checkbox);

        hoTenTextView.setText(allEmployees.get(position).getHoTen());
        chuyenMonTextView.setText(allEmployees.get(position).getChuyenMon());

        //PROBLEM IS HERE!!!
        if (selectedEmployees.size() > 0 && selectedEmployees.get(allEmployeesIds[position]) != null) {
            addEmployeeCheckBox.setChecked(true);
        } else {
            addEmployeeCheckBox.setChecked(false);
        }

        return view;
    }

    public String[] getAllEmployeesIds() {
        return allEmployeesIds;
    }

    public void setAllEmployeesIds(String[] allEmployeesIds) {
        this.allEmployeesIds = allEmployeesIds;
    }
}

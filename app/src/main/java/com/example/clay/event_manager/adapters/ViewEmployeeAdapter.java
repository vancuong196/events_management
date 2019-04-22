package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewEmployeeAdapter extends BaseAdapter {

    private final Activity context;
    private HashMap<String, Employee> employees;
    private String[] employeesIds;

    public ViewEmployeeAdapter(Activity context, HashMap<String, Employee> employees) {
        this.context = context;
        this.employees = employees;
        employeesIds = employees.keySet().toArray(new String[employees.size()]);
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Override
    public Object getItem(int i) {
        return employees.get(employeesIds[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_view_employee_list_item, parent, false);
        }

        TextView hoTenTextView = (TextView) view.findViewById(R.id.select_hoten_textview);
        TextView chuyenMonTextView = (TextView) view.findViewById(R.id.select_chuyenmon_textview);
        TextView luongTextView = (TextView) view.findViewById(R.id.event_details_salary_edit_text);
        CheckBox addEmployeeCheckBox = (CheckBox) view.findViewById(R.id.add_employee_checkbox);

//        hoTenTextView.setText(employeeIDs.get(position).getHoTen());
//        chuyenMonTextView.setText(employeeIDs.get(position).getChuyenMon());
//        luongTextView.setText();

        //set checkbox

        return view;
    }
}

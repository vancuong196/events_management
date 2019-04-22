package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.util.HashMap;

public class DeleteEmployeeAdapter extends BaseAdapter {
    private final Activity context;
    private HashMap<String, Employee> selectedEmployees;
    private String[] selectedEmployeesIds;

    public DeleteEmployeeAdapter(Activity context, HashMap<String, Employee> selectedEmployees) {
        this.context = context;
        this.selectedEmployees = selectedEmployees;
        selectedEmployeesIds = selectedEmployees.keySet().toArray(new String[selectedEmployees.size()]);
    }

    @Override
    public int getCount() {
        return selectedEmployees.size();
    }

    @Override
    public Object getItem(int i) {
        return selectedEmployees.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_delete_employee_list_item, parent, false);
        }
        TextView hoTenTextView = (TextView) view.findViewById(R.id.hoten_detete_textview);
        TextView chuyenMonTextView = (TextView) view.findViewById(R.id.chuyenmon_delete_textview);
        ImageButton deleteEmployeeButton = (ImageButton) view.findViewById(R.id.delete_employee_button);

        deleteEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmployees.remove(selectedEmployeesIds[position]);
                notifyDataSetChanged();
            }
        });

        hoTenTextView.setText(selectedEmployees.get(selectedEmployeesIds[position]).getHoTen());
        chuyenMonTextView.setText(selectedEmployees.get(selectedEmployeesIds[position]).getChuyenMon());

        return view;
    }

    public String[] getSelectedEmployeesIds() {
        return selectedEmployeesIds;
    }

    public void setSelectedEmployeesIds(String[] selectedEmployeesIds) {
        this.selectedEmployeesIds = selectedEmployeesIds;
    }
}

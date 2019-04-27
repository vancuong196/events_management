package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.R;
import com.example.clay.event_manager.repositories.EmployeeRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteEmployeeAdapter extends BaseAdapter {
    private final Activity context;
    private ArrayList<String> selectedEmployeesIds;
    private HashMap<String, Employee> allEmployees;

    public DeleteEmployeeAdapter(Activity context, ArrayList<String> selectedEmployeesIds) {
        this.context = context;
        this.selectedEmployeesIds = selectedEmployeesIds;
        allEmployees = EmployeeRepository.getInstance(null).getAllEmployees();
    }

    @Override
    public int getCount() {
        return selectedEmployeesIds.size();
    }

    @Override
    public Object getItem(int i) {
        return allEmployees.get(selectedEmployeesIds.get(i));
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

        //Connect views
        TextView hoTenTextView = (TextView) view.findViewById(R.id.delete_employee_list_item_employee_name);
        TextView chuyenMonTextView = (TextView) view.findViewById(R.id.delete_employee_list_item_employee_speciality);
        ImageButton deleteEmployeeButton = (ImageButton) view.findViewById(R.id.delete_employee_list_item_delete_button);

        //Fill information
        hoTenTextView.setText(allEmployees.get(selectedEmployeesIds.get(position)).getHoTen());
        chuyenMonTextView.setText(allEmployees.get(selectedEmployeesIds.get(position)).getChuyenMon());

        //Delete event
        deleteEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmployeesIds.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public ArrayList<String> getSelectedEmployeesIds() {
        return selectedEmployeesIds;
    }

    public void setSelectedEmployeesIds(ArrayList<String> selectedEmployeesIds) {
        this.selectedEmployeesIds = selectedEmployeesIds;
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("debug", "deleteEmployeeAdapter: dataSetChanged: selected employees size = " + selectedEmployeesIds.size());
        super.notifyDataSetChanged();
    }
}

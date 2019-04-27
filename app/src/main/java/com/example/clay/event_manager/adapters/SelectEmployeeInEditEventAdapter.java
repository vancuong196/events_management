package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectEmployeeInEditEventAdapter extends BaseAdapter {
    private final Activity context;
    private HashMap<String, Employee> allEmployees;
    private String[] allEmployeesIds;
    private ArrayList<String> selectedSalariesIds;
    private HashMap<String, Salary> allSalaries;

    public SelectEmployeeInEditEventAdapter(Activity context, ArrayList<String> selectedSalariesIds) {
        this.context = context;
        this.selectedSalariesIds = selectedSalariesIds;
        allEmployees = EmployeeRepository.getInstance(null).getAllEmployees();
        allEmployeesIds = allEmployees.keySet().toArray(new String[allEmployees.size()]);
        allSalaries = SalaryRepository.getInstance(null).getAllSalaries();
    }

    @Override
    public int getCount() {
        return allEmployees.size();
    }

    @Override
    public Object getItem(int i) {
        return allEmployees.get(allEmployeesIds[i]);
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

        hoTenTextView.setText(allEmployees.get(allEmployeesIds[position]).getHoTen());
        chuyenMonTextView.setText(allEmployees.get(allEmployeesIds[position]).getChuyenMon());

        //PROBLEM IS HERE!!!
        if (selectedSalariesIds.size() > 0) {
            for(String salaryId : selectedSalariesIds) {
                if(allSalaries.get(salaryId).getEmployeeId().equals(allEmployeesIds[position])) {
                    addEmployeeCheckBox.setChecked(true);
                    break;
                }
            }
        } else {
            addEmployeeCheckBox.setChecked(false);
        }

        return view;
    }

    public String[] getAllEmployeesIds() {
        return allEmployeesIds;
    }

    public HashMap<String, Employee> getAllEmployees() {
        return allEmployees;
    }

    public void setAllEmployeesIds(String[] allEmployeesIds) {
        this.allEmployeesIds = allEmployeesIds;
    }


    public void notifyDataSetChanged(ArrayList<String> selectedSalariesIds) {
        this.selectedSalariesIds = selectedSalariesIds;
        super.notifyDataSetChanged();
    }
}

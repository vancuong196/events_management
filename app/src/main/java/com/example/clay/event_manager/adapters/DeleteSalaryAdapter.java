package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.clay.event_manager.R;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.repositories.EmployeeRepository;
import com.example.clay.event_manager.repositories.SalaryRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteSalaryAdapter extends BaseAdapter {
    private final Activity context;
    private ArrayList<String> salariesIds;
    private HashMap<String, Employee> allEmployees;
    private HashMap<String, Salary> allSalaries;

    public DeleteSalaryAdapter(Activity context, ArrayList<String> salariesIds) {
        this.context = context;
        this.salariesIds = salariesIds;
        allSalaries = SalaryRepository.getInstance(null).getAllSalaries();
        allEmployees = EmployeeRepository.getInstance(null).getAllEmployees();
    }

    public ArrayList<String> getSalariesIds() {
        return salariesIds;
    }

    public void setSalariesIds(ArrayList<String> salariesIds) {
        this.salariesIds = salariesIds;
    }

    @Override
    public int getCount() {
        return salariesIds.size();
    }

    @Override
    public Object getItem(int i) {
        return allSalaries.get(salariesIds.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_delete_salary_list_item, viewGroup, false);
        }

        //Connect views
        TextView hoTenTextView = view.findViewById(R.id.delete_salary_employee_name_text_view);
        TextView chuyenMonTextView = view.findViewById(R.id.delete_salary_employee_speciality_text_view);
        EditText luongEditText = view.findViewById(R.id.delete_salary_salary_edit_text);
        CheckBox dathanhtoanCheckBox = view.findViewById(R.id.delete_salary_paid_checkbox);
        ImageButton deleteSalaryButton = view.findViewById(R.id.delete_salary_delete_button);

        //Fill information
        hoTenTextView.setText(allEmployees.get(allSalaries.get(salariesIds.get(i)).getEmployeeId()).getHoTen());
        chuyenMonTextView.setText(allEmployees.get(allSalaries.get(salariesIds.get(i)).getEmployeeId()).getChuyenMon());
        luongEditText.setText("" + allSalaries.get(salariesIds.get(i)).getSalary());
        dathanhtoanCheckBox.setChecked(allSalaries.get(salariesIds.get(i)).isPaid());

        //Add events
        deleteSalaryButton.setOnClickListener(new View.OnClickListener() {
            int tempI;
            @Override
            public void onClick(View view) {
                salariesIds.remove(tempI);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}

package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.util.ArrayList;

public class DeleteEmployeeAdapter extends ArrayAdapter<Employee> {
    private final Activity context;
    private ArrayList<Employee> selectedEmployees;

    public DeleteEmployeeAdapter(Activity context) {
        super(context, R.layout.delete_employee_list_item, AddEventActivity.getSelectedEmployees());
        this.context = context;
        this.selectedEmployees = new ArrayList<>();
        this.selectedEmployees.addAll(AddEventActivity.getSelectedEmployees());
    }

    public ArrayList<Employee> getSelectedEmployees() {
        return selectedEmployees;
    }

    public void setSelectedEmployees(ArrayList<Employee> selectedEmployees) {
        this.selectedEmployees.clear();
        this.selectedEmployees.addAll(selectedEmployees);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.delete_employee_list_item, null, true);

        TextView hoTenTextView = (TextView) rowView.findViewById(R.id.hoten_detete_textview);
        TextView chuyenMonTextView = (TextView) rowView.findViewById(R.id.chuyenmon_delete_textview);
        ImageButton deleteEmployeeButton = (ImageButton) rowView.findViewById(R.id.delete_employee_button);

        deleteEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmployees.remove(position);
                notifyDataSetChanged("adapter");
            }
        });

        hoTenTextView.setText(selectedEmployees.get(position).getHoTen());
        chuyenMonTextView.setText(selectedEmployees.get(position).getChuyenMon());

        return rowView;
    }

    public void notifyDataSetChanged(String from) {
        Log.d("debug", "size at delete Adapter = " + selectedEmployees.size());
        switch (from) {
            case "adapter":
                AddEventActivity.setSelectedEmployees(selectedEmployees);
                Log.d("debug", "from adapter, adapter size = " + selectedEmployees.size()
                        + ", activity size = " + AddEventActivity.getSelectedEmployees().size());
                break;
            case "activity":
                setSelectedEmployees(AddEventActivity.selectedEmployees);
                Log.d("debug", "from activity, adapter size = " + selectedEmployees.size()
                        + ", activity size = " + AddEventActivity.getSelectedEmployees().size());
                break;
        }
        super.notifyDataSetChanged();
    }
}

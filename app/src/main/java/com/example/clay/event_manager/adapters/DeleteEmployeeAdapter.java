package com.example.clay.event_manager.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.util.ArrayList;

public class DeleteEmployeeAdapter extends BaseAdapter {
    private final Activity context;
    private ArrayList<String> selectedEmployees;

    public DeleteEmployeeAdapter(Activity context, ArrayList<String> selectedEmployees) {
//        super(context, R.layout.delete_employee_list_item, AddEventActivity.getSelectedEmployees());
        this.context = context;
        this.selectedEmployees = selectedEmployees;
//        this.selectedEmployees = new ArrayList<>();
//        this.selectedEmployees.addAll(AddEventActivity.getSelectedEmployees());
    }

    public ArrayList<String> getSelectedEmployees() {
        return selectedEmployees;
    }

    public void setSelectedEmployees(ArrayList<String> selectedEmployees) {
        this.selectedEmployees.clear();
        this.selectedEmployees.addAll(selectedEmployees);
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
//        LayoutInflater inflater = context.getLayoutInflater();
//        View rowView = inflater.inflate(R.layout.delete_employee_list_item, null, true);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.delete_employee_list_item, parent, false);
        }
        TextView hoTenTextView = (TextView) view.findViewById(R.id.hoten_detete_textview);
        TextView chuyenMonTextView = (TextView) view.findViewById(R.id.chuyenmon_delete_textview);
        ImageButton deleteEmployeeButton = (ImageButton) view.findViewById(R.id.delete_employee_button);

        deleteEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmployees.remove("" + selectedEmployees.get(position));
                notifyDataSetChanged();
            }
        });

        hoTenTextView.setText(EventManagementFragment.employeeList
                .get(Integer.parseInt(selectedEmployees.get(position))).getHoTen());
        chuyenMonTextView.setText(EventManagementFragment.employeeList
                .get(Integer.parseInt(selectedEmployees.get(position))).getChuyenMon());

        return view;
    }

//    public void notifyDataSetChanged(String from) {
//        Log.d("debug", "size at delete Adapter = " + selectedEmployees.size());
//        switch (from) {
//            case "adapter":
////                AddEventActivity.setSelectedEmployees(selectedEmployees);
//                Log.d("debug", "from adapter, adapter size = " + selectedEmployees.size()
//                        + ", activity size = " + AddEventActivity.getSelectedEmployees().size());
//                break;
//            case "activity":
////                setSelectedEmployees(AddEventActivity.selectedEmployees);
//                Log.d("debug", "from activity, adapter size = " + selectedEmployees.size()
//                        + ", activity size = " + AddEventActivity.getSelectedEmployees().size());
//                break;
//        }
//        super.notifyDataSetChanged();
//    }
}

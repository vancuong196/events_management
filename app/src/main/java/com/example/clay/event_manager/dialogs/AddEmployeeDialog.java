package com.example.clay.event_manager.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.clay.event_manager.adapters.SelectEmployeeAdapter;
import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.left.R;

import java.util.ArrayList;

public class AddEmployeeDialog extends AppCompatDialogFragment {

    ListView addEmployeeDialogListView;
    Button cancelButton, okButton;

    ArrayList<Employee> selectedEmployees;

    public AddEmployeeDialog() {
        selectedEmployees = new ArrayList<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View addEmployeeView = inflater.inflate(R.layout.add_employee_dialog, null);
        addEmployeeDialogListView = (ListView) addEmployeeView.findViewById(R.id.add_employee_listview);
        cancelButton = (Button) addEmployeeView.findViewById(R.id.cancel_button);
        okButton = (Button) addEmployeeView.findViewById(R.id.ok_button);

        addEmployeeDialogListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray sp = addEmployeeDialogListView.getCheckedItemPositions();
                for (int i = 0; i < sp.size(); i++) {
                    if (sp.valueAt(i) == true) {
                        selectedEmployees.add(EventManagementFragment.employeeList.get(i));
                    }
                }
                //Send (ArrayList<Employee>) selectedEmployees back to AddEventActivity
            }
        });

        SelectEmployeeAdapter employeeListAdapter = new SelectEmployeeAdapter(getActivity());
        addEmployeeDialogListView.setAdapter(employeeListAdapter);
        builder.setView(addEmployeeView)
                .setTitle("Chọn Nhân sự");
        return builder.create();
    }

    public static interface MyDialogListener {
        public void selectedEmployees(ArrayList<Employee> selectedEmployees);

        public void userCanceled();
    }
}


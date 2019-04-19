package com.example.clay.event_manager.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class EmployeeRepository {

    static EmployeeRepository instance;
    ArrayList<Employee> allEmployees;

    private EmployeeRepository() {
        allEmployees = new ArrayList<>();
        getAllEmployesFromServer(new MyEmployeeCallback() {
            @Override
            public void onCallback(ArrayList<Employee> employeeList) {
                allEmployees.addAll(employeeList);
            }
        });
    }

    static public EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    public ArrayList<Employee> getAllEmployees() {
        return allEmployees;
    }

    private void getAllEmployesFromServer(final MyEmployeeCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EMPLOYEE_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList employeeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> tempHashMap = document.getData();
                                Employee tempEmployee = new Employee(document.getId(),
                                        (String) tempHashMap.get(Constants.EMPLOYEE_NAME),
                                        (String) tempHashMap.get(Constants.EMPLOYEE_SPECIALITY),
                                        (String) tempHashMap.get(Constants.EMPLOYEE_IDENTITY),
                                        (String) tempHashMap.get(Constants.EMPLOYEE_DAY_OF_BIRTH),
                                        (String) tempHashMap.get(Constants.EMPLOYEE_PHONE_NUMBER),
                                        (String) tempHashMap.get(Constants.EMPLOYEE_EMAIL));
                                employeeList.add(tempEmployee);
                            }
                            callback.onCallback(employeeList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("debug", "EventRepository: get events failed");
                    }
                });
    }

    private interface MyEmployeeCallback {
        void onCallback(ArrayList<Employee> employeeList);
    }
}

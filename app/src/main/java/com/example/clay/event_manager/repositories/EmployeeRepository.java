package com.example.clay.event_manager.repositories;

import android.util.Log;

import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class EmployeeRepository {

    static EmployeeRepository instance;
    HashMap<String, Employee> allEmployees;
    IOnDataLoadComplete listener;

    private EmployeeRepository(final IOnDataLoadComplete listener) {
        this.listener = listener;
        allEmployees = new HashMap<>();
        addListener(new MyEmployeeCallback() {
            @Override
            public void onCallback(HashMap<String, Employee> employeeList) {
                if (employeeList != null) {
                    allEmployees = employeeList;
                    if (EmployeeRepository.this.listener != null) {
                        EmployeeRepository.this.listener.notifyOnLoadComplete();
                    }
                    Log.d("debug", "set allEmployees.size() = " + allEmployees.size());
                }
            }
        });
        if (allEmployees == null) {
            allEmployees = new HashMap<>();
            Log.d("debug", "set allEmployees = new....");
        }
    }

    static public EmployeeRepository getInstance(IOnDataLoadComplete listener) {
        if (instance == null) {
            instance = new EmployeeRepository(listener);
        }
        return instance;
    }

    public HashMap<String, Employee> getAllEmployees() {
        return allEmployees;
    }

    private void addListener(final EmployeeRepository.MyEmployeeCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EMPLOYEE_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("debug", "Employees listen failed.", e);
                            return;
                        }
                        HashMap<String, Employee> employees = new HashMap<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> tempHashMap = doc.getData();
                            Employee tempEmployee = new Employee(doc.getId(),
                                    (String) tempHashMap.get(Constants.EMPLOYEE_NAME),
                                    (String) tempHashMap.get(Constants.EMPLOYEE_SPECIALITY),
                                    (String) tempHashMap.get(Constants.EMPLOYEE_IDENTITY),
                                    (String) tempHashMap.get(Constants.EMPLOYEE_DAY_OF_BIRTH),
                                    (String) tempHashMap.get(Constants.EMPLOYEE_PHONE_NUMBER),
                                    (String) tempHashMap.get(Constants.EMPLOYEE_EMAIL));
                            employees.put(tempEmployee.getId(), tempEmployee);
                        }
                        callback.onCallback(employees);
                        Log.d("debug", "got " + employees.size() + " employees in total");
                    }
                });
    }

    public ArrayList<String> getEmployeesIdsFromSalariesIds(ArrayList<String> salariesIds) {
        ArrayList<String> employeesIds = new ArrayList<>();
        for (String salaryId : salariesIds) {
            employeesIds.add(SalaryRepository.getInstance(null).getAllSalaries().get(salaryId).getEmployeeId());
        }
        return employeesIds;
    }

    public ArrayList<String> getEmployeesIdsByEventId(String eventId) {
        ArrayList<String> employeesIds = new ArrayList<>();
        for (Salary s : SalaryRepository.getInstance(null).getSalariesByEventId(eventId).values()) {
            employeesIds.add(s.getEmployeeId());
        }
        return employeesIds;
    }

    private interface MyEmployeeCallback {
        void onCallback(HashMap<String, Employee> employeeList);
    }
}

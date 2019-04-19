package com.example.clay.event_manager.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clay.event_manager.models.Employee;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class EmployeeRepository {

    public interface MyEmployeeCallback {
        void onCallback(ArrayList<Employee> employeeList);
    }

    public static ArrayList<Employee> getEmployeesFromServer() {
        final ArrayList employeeList = new ArrayList<>();
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EMPLOYEE_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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
                        }
                    }
                });
        return employeeList;
    }

    public static boolean isContained(Employee employee, ArrayList<Employee> arr) {
        Log.d("debug", "employeeList.size() = "+ arr.size());
        for(Employee e : arr) {
//            Log.d("debug",e.getId()+" / "+employee.getId());
            if(e.getId().equals(employee.getId())) {
                return true;
            }
        }
        return false;
    };
}

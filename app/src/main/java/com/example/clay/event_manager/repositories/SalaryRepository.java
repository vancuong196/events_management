package com.example.clay.event_manager.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class SalaryRepository {
    static SalaryRepository instance;
    private HashMap<String, Salary> allSalaries;
    IOnDataLoadComplete listener;

    private SalaryRepository(final IOnDataLoadComplete listener) {
//        allEvents = new ArrayList<>();
        this.listener = listener;
        addListener(new SalaryRepository.MySalaryCallback() {
            @Override
            public void onCallback(HashMap<String, Salary> salaryList) {
                if(salaryList != null) {
                    allSalaries = salaryList;
                    if (SalaryRepository.this.listener!=null) {
                        SalaryRepository.this.listener.notifyOnLoadComplete();
                    }
                }
            }
        });
        if (allSalaries == null) {
            allSalaries = new HashMap<>();
        }
    }

    static public SalaryRepository getInstance(IOnDataLoadComplete listener) {
        if (instance == null) {
            instance = new SalaryRepository(listener);
        }
        return instance;
    }

    private void addListener(final SalaryRepository.MySalaryCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("debug", "Events listen failed.", e);
                            return;
                        }
                        HashMap<String, Salary> salaryList = new HashMap<>();
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> tempHashMap = doc.getData();
                            Salary tempSalary = new Salary(doc.getId(),
                                    (String) tempHashMap.get(Constants.SALARY_EVENT_ID),
                                    (String) tempHashMap.get(Constants.SALARY_EMPLOYEE_ID),
                                    Integer.parseInt((String) tempHashMap.get(Constants.SALARY_SALARY)));
                            salaryList.put(tempSalary.getSalaryId(), tempSalary);
                        }
                        callback.onCallback(salaryList);
                    }
                });
    }

    public HashMap<String, Salary> getAllSalaries() {
        return allSalaries;
    }

    public static void addSalaryToDatabase(Salary salary) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.SALARY_EVENT_ID, salary.getEventId());
        data.put(Constants.SALARY_EMPLOYEE_ID, salary.getEmployeeId());
        data.put(Constants.SALARY_SALARY, "" + salary.getSalary());
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public boolean deleteSalary(String salaryId, final Context context) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .document(salaryId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Đã xóa bản ghi lương", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Xóa bản ghi lương thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
        return true;
    }

    /*public HashMap<String, Salary> getSalaryOnEventAndEmployee(String eventId, String employeeId) {
        Log.d("debug", "EventRepository: getting event on date: " + date);
        HashMap<String, Salary> salaries = new HashMap<>();
        if(getAllSalaries().size() > 0) {
            for (String salaryID : salaries.keySet()) {
                if (salaries.get(eventId).getNgayBatDau().equals(date)) {
                    events.put(eventID, allEvents.get(eventID));
                }
            }
        }
        return salaries;
    }*/

    private interface MySalaryCallback {
        void onCallback(HashMap<String, Salary> salaryList);
    }
}

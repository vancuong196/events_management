package com.example.clay.event_manager.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class SalaryRepository {
    static SalaryRepository instance;
    IOnDataLoadComplete listener;
    private HashMap<String, Salary> allSalaries;

    private SalaryRepository(final IOnDataLoadComplete listener) {
//        allEvents = new ArrayList<>();
        this.listener = listener;
        addListener(new SalaryRepository.MySalaryCallback() {
            @Override
            public void onCallback(HashMap<String, Salary> salaryList) {
                if (salaryList != null) {
                    allSalaries = salaryList;
                    if (SalaryRepository.this.listener != null) {
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

    public void updateSalaries(final ArrayList<Salary> salaries, final MyUpdateSalariesCallback callback) {
        for (int i = 0; i < salaries.size(); i++) {
            Map<String, Object> data = new HashMap<>();
            data.put(Constants.SALARY_SALARY, "" + salaries.get(i).getSalary());
            data.put(Constants.SALARY_PAID, Boolean.toString(salaries.get(i).isPaid()));
            final int tempI = i;
            DatabaseAccess.getInstance().getDatabase()
                    .collection(Constants.SALARY_COLLECTION)
                    .document(salaries.get(i).getSalaryId())
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (tempI == salaries.size() - 1) {
                                callback.onCallback(true);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onCallback(false);
                        }
                    });
        }
    }

    private void addListener(final SalaryRepository.MySalaryCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("debug", "Salary collection listen failed.", e);
                            return;
                        }
                        HashMap<String, Salary> salaryList = new HashMap<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.size() > 0) {
                                Map<String, Object> data = doc.getData();
                                int salary;
                                if (((String) data.get(Constants.SALARY_SALARY)).isEmpty()) {
                                    salary = 0;
                                } else {
                                    salary = Integer.parseInt((String) data.get(Constants.SALARY_SALARY));
                                }
                                Salary tempSalary = new Salary(doc.getId(),
                                        (String) data.get(Constants.SALARY_EVENT_ID),
                                        (String) data.get(Constants.SALARY_EMPLOYEE_ID),
                                        salary,
                                        Boolean.parseBoolean((String) data.get(Constants.SALARY_PAID)));
                                salaryList.put(tempSalary.getSalaryId(), tempSalary);
                            }
                        }
                        callback.onCallback(salaryList);
                    }
                });
    }

    public HashMap<String, Salary> getAllSalaries() {
        return allSalaries;
    }

    public void addSalariesToDatabase(final ArrayList<Salary> salaries, final MyAddSalaryCallback callback) {
        Log.d("debug", "SalaryRepository: addSalariesToDatabase(): salaries.size() = " + salaries.size());
        for (int i = 0; i < salaries.size(); i++) {
            final int tempI = i;
            addSalaryToDatabase(salaries.get(i), new MyAddSalaryCallback() {
                @Override
                public void onCallback(String lastSalaryId) {
                    if (tempI == salaries.size() - 1) {
                        callback.onCallback(lastSalaryId);
                    }
                }
            });
        }
    }

    public void addSalaryToDatabase(Salary salary, final MyAddSalaryCallback callback) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.SALARY_EVENT_ID, salary.getEventId());
        data.put(Constants.SALARY_EMPLOYEE_ID, salary.getEmployeeId());
        data.put(Constants.SALARY_SALARY, "" + salary.getSalary());
        data.put(Constants.SALARY_PAID, Boolean.toString(salary.isPaid()));
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callback.onCallback(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public HashMap<String, Salary> getSalariesByEventId(String eventId) {
        HashMap<String, Salary> salaries = new HashMap<>();
        for (HashMap.Entry<String, Salary> entry : allSalaries.entrySet()) {
            if (entry.getValue().getEventId().equals(eventId)) {
                salaries.put(entry.getKey(), entry.getValue());
            }
        }
        Log.d("debug", "SalaryRepository: getSalariesByEventId: salaries.size() = " + salaries.size());
        return salaries;
    }

    public void deleteSalaryByEventIdAndEmployeeId(String eventId, String employeeId, final MyDeleteSalaryByEventIdAndEmployeeIdCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .whereEqualTo(Constants.SALARY_EVENT_ID, eventId)
                .whereEqualTo(Constants.SALARY_EMPLOYEE_ID, employeeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                DatabaseAccess.getInstance().getDatabase()
                                        .collection(Constants.SALARY_COLLECTION)
                                        .document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("debug", "SalaryRepository: deleteSalariesByEventId: delete succeed");
                                                callback.onCallback(true);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                callback.onCallback(false);
                                            }
                                        });
                            }
                        } else {
                            callback.onCallback(false);
                            Log.d("debug", "Error getting salary records: ", task.getException());
                        }
                    }
                });
    }

    public void deleteSalariesByEventId(String eventId, final MyDeleteSalariesByEventIdCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.SALARY_COLLECTION)
                .whereEqualTo(Constants.SALARY_EVENT_ID, eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                DatabaseAccess.getInstance().getDatabase()
                                        .collection(Constants.SALARY_COLLECTION)
                                        .document(document.getId())
                                        .delete();
                            }
                            Log.d("debug", "SalaryRepository: deleteSalariesByEventId: delete succeed");
                            callback.onCallback(true);
                        } else {
                            callback.onCallback(false);
                            Log.d("debug", "Error getting salary records: ", task.getException());
                        }
                    }

//                    @Override
//                    public void onComplete(Void aVoid) {
//                        Toast.makeText(context, "Đã xóa bản ghi lương", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "Xóa bản ghi lương thất bại", Toast.LENGTH_SHORT).show();
//                    }
                });
    }

    public void deleteSalary(String salaryId, final Context context) {
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

    public interface MyAddSalaryCallback {
        void onCallback(String salaryId);
    }

    public interface MyDeleteSalariesByEventIdCallback {
        void onCallback(boolean deleteSucceed);
    }

    public interface MyDeleteSalaryByEventIdAndEmployeeIdCallback {
        void onCallback(boolean deleteSucceed);
    }

    public interface MyUpdateSalariesCallback {
        void onCallback(boolean updateSucceed);
    }
}

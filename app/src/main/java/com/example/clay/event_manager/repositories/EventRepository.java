package com.example.clay.event_manager.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.clay.event_manager.activities.AddEventActivity;
import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class EventRepository{

    static EventRepository instance;
    private HashMap<String, Event> allEvents;
    IOnDataLoadComplete listener;

    private EventRepository(final IOnDataLoadComplete listener) {
//        allEvents = new ArrayList<>();
        this.listener = listener;
        addListener(new MyEventCallback() {
            @Override
            public void onCallback(HashMap<String, Event> eventList) {
                if(eventList != null) {
                    allEvents = eventList;
                    if (EventRepository.this.listener != null) {
                        EventRepository.this.listener.notifyOnLoadComplete();
                    }
                }
            }
        });
        if (allEvents == null) {
            allEvents = new HashMap<>();
        }
    }

    static public EventRepository getInstance(IOnDataLoadComplete listener) {
        if (instance == null) {
            instance = new EventRepository(listener);
        }
        return instance;
    }

    public void addEventToDatabase(Event event, final ArrayList<Salary> salaries, final MyAddEventCallback callback) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.EVENT_NAME, event.getTen());
        data.put(Constants.EVENT_START_DATE, event.getNgayBatDau());
        data.put(Constants.EVENT_END_DATE, event.getNgayKetThuc());
        data.put(Constants.EVENT_START_TIME, event.getGioBatDau());
        data.put(Constants.EVENT_END_TIME, event.getGioKetThuc());
        data.put(Constants.EVENT_LOCATION, event.getDiaDiem());
        data.put(Constants.EVENT_NOTE, event.getGhiChu());
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EVENT_COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    ArrayList<Salary> tempSalaries = new ArrayList<>();
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        tempSalaries.addAll(salaries);
                        Log.d("debug", "EventRepository: salaries.sizs() = " + tempSalaries.size());
                        for(int i = 0; i < tempSalaries.size(); i++) {
                            tempSalaries.get(i).setEventId(documentReference.getId());
                            final int tempI = i;
                            SalaryRepository.getInstance(null).addSalaryToDatabase(tempSalaries.get(i), new SalaryRepository.MyAddSalaryCallback() {
                                @Override
                                public void onCallback(String salaryId) {
                                    if(tempI == tempSalaries.size() - 1) {
                                        callback.onCallback(documentReference.getId());
                                    }
                                }
                            });
                        }
//                        SalaryRepository.getInstance(null).addSalariesToDatabase(tempSalaries, new SalaryRepository.MyAddSalaryCallback() {
//                            @Override
//                            public void onCallback(String lastSalaryId) {
//                                callback.onCallback(documentReference.getId());
//                                Log.d("debug","Thêm sự kiện thành công");
//                            }
//                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("debug","Thêm sự kiện thất bại");
                    }
                });
    }

    private void addListener(final MyEventCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EVENT_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("debug", "Events listen failed.", e);
                            return;
                        }
                        HashMap<String, Event> events = new HashMap<>();
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> tempHashMap = doc.getData();
                            Event tempEvent = new Event(doc.getId(),
                                    (String) tempHashMap.get(Constants.EVENT_NAME),
                                    (String) tempHashMap.get(Constants.EVENT_START_DATE),
                                    (String) tempHashMap.get(Constants.EVENT_END_DATE),
                                    (String) tempHashMap.get(Constants.EVENT_START_TIME),
                                    (String) tempHashMap.get(Constants.EVENT_END_TIME),
                                    (String) tempHashMap.get(Constants.EVENT_LOCATION),
                                    (String) tempHashMap.get(Constants.EVENT_NOTE));
                            events.put(tempEvent.getId(), tempEvent);
                        }
                        callback.onCallback(events);
                    }
                });
    }

    public HashMap<String, Event> getAllEvents() {
        return allEvents;
    }

    public void deleteEvent(final String eventId, final MyDeleteEventCallback callback) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EVENT_COLLECTION)
                .document(eventId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("debug", "Xóa sự kiện " + eventId + " thành công");
                        SalaryRepository.getInstance(null).deleteSalaryByEventId(eventId, new SalaryRepository.MyDeleteSalaryByEventIdCallback() {
                            @Override
                            public void onCallback(boolean deleteSucceed) {
                                callback.onCallback(true, deleteSucceed);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("debug", "Xóa sự kiện " + eventId + " thất bại");
                        callback.onCallback(false, false);
                    }
                });
    }

    public void updateEventToDatabase(final Event event, final ArrayList<Salary> salaries, final MyUpdateEventCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.EVENT_NAME, event.getTen());
        data.put(Constants.EVENT_START_DATE, event.getNgayBatDau());
        data.put(Constants.EVENT_END_DATE, event.getNgayKetThuc());
        data.put(Constants.EVENT_START_TIME, event.getGioBatDau());
        data.put(Constants.EVENT_END_TIME, event.getGioKetThuc());
        data.put(Constants.EVENT_LOCATION, event.getDiaDiem());
        data.put(Constants.EVENT_NOTE, event.getGhiChu());
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EVENT_COLLECTION)
                .document(event.getId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SalaryRepository.getInstance(null).deleteSalaryByEventId(event.getId(), new SalaryRepository.MyDeleteSalaryByEventIdCallback() {
                            @Override
                            public void onCallback(boolean deleteSucceed) {
                                if(salaries.size() > 0) {
                                    SalaryRepository.getInstance(null).addSalariesToDatabase(salaries, new SalaryRepository.MyAddSalaryCallback() {
                                        @Override
                                        public void onCallback(String lastSalaryId) {
                                            callback.onCallback(true);
                                            Log.d("debug", "EventRepository: edit event succeed");
                                        }
                                    });
                                } else {
                                    callback.onCallback(true);
                                    Log.d("debug", "EventRepository: edit event succeed");
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onCallback(false);
                        Log.d("debug", "EventRepository: edit event failed");
                    }
                });
    }

    public HashMap<String, Event> getEventsOnDate(String date) {
        Log.d("debug", "EventRepository: getting event on date: " + date);
        HashMap<String, Event> events = new HashMap<>();
        if(getAllEvents().size() > 0) {
            for (String eventID : allEvents.keySet()) {
                if (allEvents.get(eventID).getNgayBatDau().equals(date)) {
                    events.put(eventID, allEvents.get(eventID));
                }
            }
        }
        return events;
    }

    private interface MyEventCallback {
        void onCallback(HashMap<String, Event> eventList);
    }
    public interface MyAddEventCallback {
        void onCallback(String eventId);
    }

    public interface MyDeleteEventCallback {
        void onCallback(boolean deleteEventSucceed, boolean deleteSalariesSucceed);
    }
    public interface MyUpdateEventCallback {
        void onCallback(boolean updateEventSucceed);
    }
}

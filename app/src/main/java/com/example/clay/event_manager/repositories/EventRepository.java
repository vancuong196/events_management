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

    public static void addEventToDatabase(Event event, final MyAddEventCallback callback) {
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
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callback.onCallback(documentReference.getId());
                        Log.d("debug","Thêm sự kiện thành công");
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

    public boolean deleteEvent(int position, final Context context) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EVENT_COLLECTION)
                .document(allEvents.get(position).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Xóa sự kiện thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Xóa sự kiện thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
        return true;
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
}

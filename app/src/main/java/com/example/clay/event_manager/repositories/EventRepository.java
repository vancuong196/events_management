package com.example.clay.event_manager.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class EventRepository {

    public static void getEventsOnDate(final MyEventCallback callback, String date) {
        DatabaseAccess.getInstance().getDatabase()
                .collection(Constants.EVENT_COLLECTION)
                .whereEqualTo(Constants.EVENT_START_DATE, date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Event> eventList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> tempHashMap = document.getData();
                                Event tempEvent = new Event(document.getId(),
                                        (String) tempHashMap.get(Constants.EVENT_NAME),
                                        (String) tempHashMap.get(Constants.EVENT_START_DATE),
                                        (String) tempHashMap.get(Constants.EVENT_END_DATE),
                                        (String) tempHashMap.get(Constants.EVENT_START_TIME),
                                        (String) tempHashMap.get(Constants.EVENT_END_TIME),
                                        (String) tempHashMap.get(Constants.EVENT_LOCATION),
                                        (String) tempHashMap.get(Constants.EVENT_EMPLOYEE_ID),
                                        (String) tempHashMap.get(Constants.EVENT_NOTE));
                                eventList.add(tempEvent);
                                Log.d("debug", "EventRepository: found 1 events");
                            }
                            Log.d("debug", "EventRepository: end of searching. got "
                                    + eventList.size() + " events");
                            callback.onCallback(eventList);
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

    public interface MyEventCallback {
        void onCallback(ArrayList<Event> eventList);
    }

//    public static ArrayList<Event> getEventsOnDate(String date) {
//        Log.d("debug", "EventRepository: getting event on date: " + date);
//        final ArrayList<Event> events = new ArrayList<>();
//
//        DatabaseAccess.getInstance().getDatabase()
//                .collection(Constants.EVENT_COLLECTION)
//                .whereEqualTo(Constants.EVENT_START_DATE, date)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> tempHashMap = document.getData();
//                                Event tempEvent = new Event(document.getId(),
//                                        (String) tempHashMap.get(Constants.EVENT_NAME),
//                                        (String) tempHashMap.get(Constants.EVENT_START_DATE),
//                                        (String) tempHashMap.get(Constants.EVENT_END_DATE),
//                                        (String) tempHashMap.get(Constants.EVENT_START_TIME),
//                                        (String) tempHashMap.get(Constants.EVENT_END_TIME),
//                                        (String) tempHashMap.get(Constants.EVENT_LOCATION),
//                                        (String) tempHashMap.get(Constants.EVENT_EMPLOYEE_ID),
//                                        (String) tempHashMap.get(Constants.EVENT_NOTE));
//                                events.add(tempEvent);
//                                Log.d("debug", "EventRepository: found 1 events");
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("debug", "EventRepository: get events failed");
//                    }
//                });
//        Log.d("debug", "EventRepository: end of searching. got " + events.size() + " events");
//        return events;
//    }
}

package com.example.clay.event_manager.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clay.event_manager.interfaces.IOnDataLoadComplete;
import com.example.clay.event_manager.models.Event;
import com.example.clay.event_manager.models.Salary;
import com.example.clay.event_manager.models.Schedule;
import com.example.clay.event_manager.utils.CalendarUtil;
import com.example.clay.event_manager.utils.Constants;
import com.example.clay.event_manager.utils.DatabaseAccess;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    boolean isLoaded;

    private EventRepository(final IOnDataLoadComplete listener) {
//        allEvents = new ArrayList<>();
        this.listener = listener;
        addListener(new MyEventCallback() {
            @Override
            public void onCallback(HashMap<String, Event> eventList) {
                if(eventList != null) {
                    allEvents = eventList;
                    if (EventRepository.this.listener != null) {
                        isLoaded = true;
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
        } else  {
            instance.listener = listener;
            if (instance.isLoaded&&listener!=null) {
                instance.listener.notifyOnLoadComplete();
            }
        }
        return instance;
    }

    public void addEventToDatabase(Event event, final ArrayList<Salary> salaries, final ArrayList<Schedule> schedules,
                                   final MyAddEventCallback callback) {
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
                    ArrayList<Salary> tempSalaries = new ArrayList<>(salaries);
                    ArrayList<Schedule> tempSchedules = new ArrayList<>(schedules);
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        Log.d("debug", "EventRepository: salaries.size() = " + tempSalaries.size());
                        for(int i = 0; i < tempSalaries.size(); i++) {
                            tempSalaries.get(i).setEventId(documentReference.getId());
                            final int tempI = i;
                            SalaryRepository.getInstance(null).addSalaryToDatabase(tempSalaries.get(i), new SalaryRepository.MyAddSalaryCallback() {
                                @Override
                                public void onCallback(String salaryId) {
                                    if(tempI == tempSalaries.size() - 1) {
                                        for(int j = 0; j < tempSchedules.size(); j++) {
                                            tempSchedules.get(j).setEventId(documentReference.getId());
                                            final int tempJ = j;
                                            ScheduleRepository.getInstance(null).addScheduleToDatabase(tempSchedules.get(j), new ScheduleRepository.MyAddScheduleCallback() {
                                                @Override
                                                public void onCallback(String scheduleId) {
                                                    if(tempJ == tempSchedules.size() - 1) {
                                                        callback.onCallback(documentReference.getId());
                                                    }
                                                }
                                            });
                                        }
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
                        SalaryRepository.getInstance(null).deleteSalariesByEventId(eventId, new SalaryRepository.MyDeleteSalariesByEventIdCallback() {
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

    public void updateEventToDatabase(final Event event, final MyUpdateEventCallback callback) {
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
                        callback.onCallback(true);
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

    public HashMap<String, Event> getEventsByDate(String date) {
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

    public HashMap<String, Event> getEventsThroughDate(String date) {
        Log.d("debug", "EventRepository: getting event through date: " + date);
        HashMap<String, Event> events = new HashMap<>();
        if(getAllEvents().size() > 0) {
            for (Event tempE : allEvents.values()) {
                try {
                    if (CalendarUtil.sdfDayMonthYear.parse(tempE.getNgayBatDau()).compareTo(
                            CalendarUtil.sdfDayMonthYear.parse(date)) <= 0 &&
                            CalendarUtil.sdfDayMonthYear.parse(tempE.getNgayKetThuc()).compareTo(
                                    CalendarUtil.sdfDayMonthYear.parse(date)) >= 0) {
                        events.put(tempE.getId(), tempE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

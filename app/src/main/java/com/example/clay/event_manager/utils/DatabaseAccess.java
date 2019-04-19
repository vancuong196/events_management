package com.example.clay.event_manager.utils;

import com.example.clay.event_manager.models.Employee;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DatabaseAccess {

    FirebaseFirestore database;
    static DatabaseAccess instance;

    private DatabaseAccess() {
        database = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    static public DatabaseAccess getInstance() {
        if(instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }

}

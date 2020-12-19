package com.example.whatsappmessenger;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineFirebase extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

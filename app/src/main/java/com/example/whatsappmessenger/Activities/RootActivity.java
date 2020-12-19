package com.example.whatsappmessenger.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RootActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users/" + phoneNumber);
        database.child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Date currentTime = Calendar.getInstance().getTime();
        String datetime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(currentTime);

        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users/" + phoneNumber);
        database.child("status").setValue(datetime);
    }
}

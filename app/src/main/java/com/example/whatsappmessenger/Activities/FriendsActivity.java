package com.example.whatsappmessenger.Activities;

import androidx.annotation.NonNull;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.whatsappmessenger.Classes.Contacts;
import com.example.whatsappmessenger.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsActivity extends RootActivity implements View.OnClickListener {

    int READ_CONTACTS_REQ_CODE = 1;
    public Map<String, String> contactsMap;
    Contacts contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ImageView backImageView = findViewById(R.id.backImageView);
        backImageView.setOnClickListener(this);

        contacts =  new Contacts(this);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},READ_CONTACTS_REQ_CODE);
//        }else {
//            contacts.getContacts();
//        }

        contactsMap = (HashMap) getIntent().getSerializableExtra("contacts_map");
        contacts.friendsDisplay();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_CONTACTS_REQ_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                contacts.getContacts();
            }
        }
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }
}
package com.example.whatsappmessenger.Activities;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.whatsappmessenger.Adapters.ChatsViewPagerAdapter;
import com.example.whatsappmessenger.Classes.Chats;
import com.example.whatsappmessenger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ChatsActivity extends RootActivity implements View.OnClickListener {

    int READ_CONTACTS_REQ_CODE = 1;
    public Map<String, String> contactsMap = new HashMap<>();
    Chats chats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        final TabLayout chatsTabLayout = findViewById(R.id.chatsTabLayout);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);

        chats =  new Chats(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},READ_CONTACTS_REQ_CODE);
        }else {
            contactsMap = chats.getMapOfUsersNumberName();
        }

        ChatsViewPagerAdapter chatsViewPagerAdapter = new ChatsViewPagerAdapter(getSupportFragmentManager(),chatsTabLayout.getTabCount());
        viewPager.setAdapter(chatsViewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(chatsTabLayout));
        chatsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.select();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_CONTACTS_REQ_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                contactsMap = chats.getMapOfUsersNumberName();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,FriendsActivity.class);
        intent.putExtra("contacts_map", (Serializable) contactsMap);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(phoneNumber).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
    }
}
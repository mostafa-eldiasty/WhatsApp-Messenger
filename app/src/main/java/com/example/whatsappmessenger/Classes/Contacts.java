package com.example.whatsappmessenger.Classes;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappmessenger.Activities.FriendsActivity;
import com.example.whatsappmessenger.Adapters.ContactsAdapter;
import com.example.whatsappmessenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class Contacts {
    Context context;

    HashSet<Contact> contactSet = new HashSet<>();

    TextView numOfContactsTextView;
    RecyclerView contactsRecyclerView;

    public Contacts(Context context) {
        this.context = context;

        numOfContactsTextView = ((Activity) context).findViewById(R.id.numOfContactsTextView);
        contactsRecyclerView = ((Activity) context).findViewById(R.id.contactsRecyclerView);
    }

    public void getContacts() {
        Set<String> phoneNumberSet = new HashSet<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim();
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replace(" ", "");

            if (number.trim().charAt(0) != '+') {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

                try {
                    Phonenumber.PhoneNumber currentContactNumber = PhoneNumberUtil.createInstance(context).parse(number, manager.getSimCountryIso().toUpperCase());
                    number = ("+" + currentContactNumber.getCountryCode() + currentContactNumber.getNationalNumber());

                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
            }

            phoneNumberSet.add(number);
            if(contactSet.size() < phoneNumberSet.size())
                contactSet.add(new Contact(name,"","",number));
        }
    }

    public void friendsDisplay() {
        final List<Contact> contactsList = new ArrayList<>();
        Map<String,String> contactsMap = (HashMap)((FriendsActivity)context).contactsMap;

        for (final Map.Entry<String, String> entry : contactsMap.entrySet()) {
            FirebaseDatabase.getInstance().getReference("users/" + entry.getKey()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        ContactsAdapter contactsAdapter;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() == null)
                                return;

                            contactsList.add(new Contact(entry.getValue(), snapshot.child("about").getValue(String.class),
                                    snapshot.child("profile_image_path").getValue(String.class), snapshot.getKey()));

                            numOfContactsTextView.setText(contactsList.size() + " contacts");

                            contactsAdapter = new ContactsAdapter(context, contactsList);
                            contactsRecyclerView.setAdapter(contactsAdapter);
                            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}
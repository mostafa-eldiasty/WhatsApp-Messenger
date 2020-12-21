package com.example.whatsappmessenger.Classes;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappmessenger.Activities.ChatRoomActivity;
import com.example.whatsappmessenger.Adapters.ChatAdapter;
import com.example.whatsappmessenger.Adapters.ChatsAdapter;
import com.example.whatsappmessenger.Activities.ChatsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class Chats {
    Context context;
    DatabaseReference myRef;

    public Chats(Context context) {
        this.context = context;
    }

    public Map<String,String> getMapOfUsersNumberName() {
        Map<String,String> mapNumberName = new HashMap<>();
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
            mapNumberName.put(number,name);
        }

        return mapNumberName;
    }

    public void loadChats(final RecyclerView chatsRecyclerView){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String currentUserPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        final Map<String,String> mapNumberName = ((ChatsActivity)context).contactsMap;

        String senderPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Query query = FirebaseDatabase.getInstance().getReference("chats").endAt(senderPhoneNumber + "\uf8ff","sender_receiver");
        query.addValueEventListener(new ValueEventListener() {
            Map<String,Chat> chatsMap = new LinkedHashMap<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot == null || snapshot.getChildren() == null)
                    return;

                Iterator it = snapshot.getChildren().iterator();
                while (it.hasNext()){
                    DataSnapshot chat = (DataSnapshot) it.next();
                    String[] sender_receiver = chat.child("sender_receiver").getValue().toString().split("_");

                    String phone = "";
                    if(sender_receiver[0].equals(currentUserPhoneNumber)) {
                        phone = sender_receiver[1];

                        chatsMap.remove(phone);
                        chatsMap.put(phone,new Chat(mapNumberName.get(phone),phone,chat.child("message").getValue().toString(),
                                chat.child("time").getValue().toString(), chat.child("type").getValue().toString()));
                    }
                    else if(sender_receiver[1].equals(currentUserPhoneNumber)) {
                        phone = sender_receiver[0];

                        chatsMap.remove(phone);
                        chatsMap.put(phone,new Chat(mapNumberName.get(phone),phone,chat.child("message").getValue().toString(),
                                chat.child("time").getValue().toString(), chat.child("type").getValue().toString()));
                    }
                }
                List<Chat> chatList = new ArrayList(chatsMap.values());
                Collections.reverse(chatList);
                ChatsAdapter chatsAdapter = new ChatsAdapter(context,chatList);
                chatsRecyclerView.setAdapter(chatsAdapter);
                chatsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

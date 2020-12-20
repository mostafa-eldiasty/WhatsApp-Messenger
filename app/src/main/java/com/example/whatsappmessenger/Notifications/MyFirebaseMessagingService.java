package com.example.whatsappmessenger.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.whatsappmessenger.Activities.ChatsActivity;
import com.example.whatsappmessenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        databaseReference.child(userPhoneNumber).child("token").setValue(s);
        //save new token under it's user
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        showNotification();
    }

    private void showNotification() {
        final String userPhonenumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("chats").endAt(userPhonenumber + "\uf8ff","sender_receiver");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            List<String> messages = new ArrayList<>();
            Set<String> senders = new HashSet<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator it = snapshot.getChildren().iterator();
                while(it.hasNext()){
                    DataSnapshot receivedMessage = (DataSnapshot) it.next();
                    if(receivedMessage.child("sender_receiver").getValue().toString().split("_")[1].equals(userPhonenumber) &&
                            (receivedMessage.child("status").getValue().equals("sent") || receivedMessage.child("status").getValue().equals("delivered"))){
                        messages.add(receivedMessage.child("message").getValue().toString());
                        senders.add(receivedMessage.child("sender_receiver").getValue().toString().split("_")[0]);
                        receivedMessage.child("status").getRef().setValue("delivered");
                    }
                }

                if(messages.size() > 0) {
                    Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
                    PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(), 2, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.notification_image)
                            .setContentTitle("WhatsApp")
                            .setContentText(messages.size() + " messages from " + senders.size() + " chats")
                            .setAutoCancel(true).setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //build notification and pending the new intent
        //in another class
    }
}

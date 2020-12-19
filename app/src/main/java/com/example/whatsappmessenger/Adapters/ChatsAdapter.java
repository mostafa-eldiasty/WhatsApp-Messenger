package com.example.whatsappmessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappmessenger.Activities.ChatRoomActivity;
import com.example.whatsappmessenger.Activities.ChatsActivity;
import com.example.whatsappmessenger.Classes.Chat;
import com.example.whatsappmessenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    Context context;
    List<Chat> chatList;
//    Map<String,String> chatsProfileImagesMap;

    public ChatsAdapter(Context context,List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_chats_list,parent,false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position) {
        holder.contactNameTextView.setText(chatList.get(position).getContactName());

        if(chatList.get(position).getMessageType().equals("image"))
            holder.messageTextView.setText("\uD83D\uDCF7 photo");
        else if(chatList.get(position).getMessageType().equals("contact"))
            holder.messageTextView.setText("\uD83D\uDC64 contact");
        else if(chatList.get(position).getMessageType().equals("loc"))
            holder.messageTextView.setText("\uD83D\uDCCC location");
        else if(chatList.get(position).getMessageType().equals("record"))
            holder.messageTextView.setText("\uD83C\uDF99 record");
        else if(chatList.get(position).getMessageType().equals("document"))
            holder.messageTextView.setText("\uD83D\uDCC4 document");
        else
            holder.messageTextView.setText(chatList.get(position).getMessageContent());

        assignDate(holder.dateTextView,position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(chatList.get(position).getContactNumber())
                .child("profile_image_path");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {

                Glide.with(context).load(snapshot.getValue().toString())
//                            .apply(RequestOptions.circleCropTransform())
                        .centerInside()
                        .dontAnimate()
                        .into(holder.contactImageView);

                holder.contactConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ChatRoomActivity.class);

                        intent.putExtra("name",chatList.get(position).getContactName());
                        intent.putExtra("image",snapshot.getValue().toString());
                        intent.putExtra("number",chatList.get(position).getContactNumber());
                        intent.putExtra("contacts_map", (Serializable) ((ChatsActivity)context).contactsMap);

                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void assignDate(TextView dateTextView, int position) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //get current date
        String currentDateTime = dateFormat.format(Calendar.getInstance().getTime());
        String[] messageDateTime = chatList.get(position).getMessageTime().split(" ");

        //get yesterday date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        if(currentDateTime.equals(messageDateTime[0]))
            dateTextView.setText(messageDateTime[1] + " " + messageDateTime[2]);
        else if(dateFormat.format(cal.getTime()).equals(messageDateTime[0]))
            dateTextView.setText("Yesterday");
        else
            dateTextView.setText(messageDateTime[0]);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout contactConstraintLayout;
        TextView contactNameTextView,messageTextView,dateTextView;
        ImageView contactImageView;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            contactConstraintLayout = itemView.findViewById(R.id.contactConstraintLayout);
            contactNameTextView = itemView.findViewById(R.id.contactNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contactImageView = itemView.findViewById(R.id.contactImageView);
        }
    }
}

package com.example.whatsappmessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappmessenger.Activities.ChatRoomActivity;
import com.example.whatsappmessenger.Activities.ChatsActivity;
import com.example.whatsappmessenger.DocumentViewActivity;
import com.example.whatsappmessenger.R;
import com.example.whatsappmessenger.Classes.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    Context context;
    List<Message> messageList;
    Map<String,String> contactsMap;

    int MSG_Text_Sent = 0;
    int MSG_Text_Recieved = 1;
    int MSG_Image_Sent = 2;
    int MSG_Image_Recieved = 3;
    int MSG_Contact_Sent = 4;
    int MSG_Contact_Recieved = 5;
    int MSG_Loc_Sent = 6;
    int MSG_Loc_Recieved = 7;
    int MSG_record_Sent = 8;
    int MSG_record_Recieved = 9;
    int MSG_document_Sent = 10;
    int MSG_document_Recieved = 11;

    public ChatAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
        contactsMap = (HashMap) ((ChatRoomActivity)context).getIntent().getSerializableExtra("contacts_map");
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_Text_Sent){
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_sender_message_container,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_Text_Recieved) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_reciever_message_container,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_Image_Sent) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_image_message_sent_container,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_Image_Recieved) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_image_message_received_container,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_Contact_Sent) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_contact_message_sent_container,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_Contact_Recieved) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_contact_message_received_container,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_Loc_Sent) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_location_message_sent,parent,false);
            return new ChatViewHolder(view);
        }
        else if (viewType == MSG_Loc_Recieved) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_location_message_received,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_record_Sent) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_record_message_sent,parent,false);
            return new ChatViewHolder(view);
        }
        else if (viewType == MSG_record_Recieved) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_record_message_received,parent,false);
            return new ChatViewHolder(view);
        }
        else if(viewType == MSG_document_Sent) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_document_messsage_sent,parent,false);
            return new ChatViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_document_messsage_received,parent,false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position) {
        if(messageList.get(position).getType().equals("text")) {
            holder.messageTextView.setText(messageList.get(position).getContent());
            holder.seenTextView.setTextColor(Color.BLACK);
        }
        else if(messageList.get(position).getType().equals("image")) {
            Glide.with(context).load(messageList.get(position).getContent()).centerInside().into(holder.messageImageView);
            holder.seenTextView.setTextColor(Color.WHITE);
        }
        else if(messageList.get(position).getType().equals("contact")) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!(snapshot.child(messageList.get(position).getContent()).getValue() == null)) {
                        String contactImage = snapshot.child(messageList.get(position).getContent()).child("profile_image_path").getValue().toString();
                        Glide.with(context).load(contactImage).centerInside().into(holder.contactImageView);
                    }
                    String contactName = contactsMap.get(messageList.get(position).getContent());
                    String contactNumber = messageList.get(position).getContent();
                    holder.contactNameTextView.setText(contactName);
                    holder.contactNumberTextView.setText(contactNumber);
                    holder.seenTextView.setTextColor(Color.BLACK);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(messageList.get(position).getType().equals("loc")) {
            holder.itemView.findViewById(R.id.locLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:" + messageList.get(position).getContent());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }
                }
            });
            holder.seenTextView.setTextColor(Color.BLACK);
        }
        else if(messageList.get(position).getType().equals("record")) {
            if(messageList.get(position).getStatus().equals("seen")){
                holder.micImageView.setImageResource(R.drawable.ic_baseline_mic_lightblue_24);
            }
            String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            if(phoneNumber.equals(messageList.get(position).getSender())){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(phoneNumber).child("profile_image_path");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Glide.with(context).load(snapshot.getValue()).centerInside().into(holder.contactImageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Intent intent = ((ChatRoomActivity) context).getIntent();
                String imageURL = intent.getStringExtra("image");
                Glide.with(context).load(imageURL).centerInside().into(holder.contactImageView);
            }
            playAudio(holder,position);

            holder.seenTextView.setTextColor(Color.BLACK);
        }

        else if(messageList.get(position).getType().equals("document")) {
            holder.itemView.findViewById(R.id.adapterDocumentMessageContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageList.get(position).getContent()));
                    context.startActivity(browserIntent);
                }
            });
            holder.seenTextView.setTextColor(Color.BLACK);
        }

        String[] messageTimeSplitted = messageList.get(position).getTime().split(" ");
        holder.timeTextView.setText(messageTimeSplitted[1] + " " + messageTimeSplitted[2]);

        String thePreviousDate = (position > 0)? messageList.get(position - 1).getTime().split(" ")[0]:null;
        holder.dateTextView.setVisibility(View.GONE);
        if(position == 0 || !messageTimeSplitted[0].equals(thePreviousDate)){
            String date = messageTimeSplitted[0];
            holder.dateTextView.setVisibility(View.VISIBLE);

            Calendar cal = Calendar.getInstance();
            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
            cal.add(Calendar.DATE,-1);
            String yesterdayDate = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
            if(currentDate.equals(date))
                holder.dateTextView.setText("TODAY");
            else if(yesterdayDate.equals(date))
                holder.dateTextView.setText("YESTERDAY");
            else
                holder.dateTextView.setText(date);
        }

        if(messageList.get(position).getStatus().equals("sent"))
            holder.seenTextView.setText("✓");
        else if(messageList.get(position).getStatus().equals("delivered"))
            holder.seenTextView.setText("✓✓");
        else if(messageList.get(position).getStatus().equals("seen")){
            holder.seenTextView.setText("✓✓");
            holder.seenTextView.setTextColor(Color.parseColor("#3FB6F2"));
        }
    }

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private void playAudio(final ChatViewHolder holder, final int position) {
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(mediaPlayer != null){
                            int mCurrentPosition = mediaPlayer.getCurrentPosition();
                            holder.playSeekBar.setProgress(mCurrentPosition);

                            if(mediaPlayer.getDuration() == mCurrentPosition) {
                                handler.removeCallbacks(this);
                                holder.playSeekBar.setOnSeekBarChangeListener(null);
                                holder.playSeekBar.setProgress(0);
                                holder.playButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                                mediaPlayer.stop();
                                return;
                            }
                        }
                        handler.postDelayed(this,1000);
                    }
                };


                if(mediaPlayer != null && mediaPlayer.isPlaying() && holder.playSeekBar.getProgress() != 0){
                    holder.playButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                    return;
                }
                else if(mediaPlayer != null && !mediaPlayer.isPlaying() && holder.playSeekBar.getProgress() != 0){
                    holder.playButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                    return;
                }

                holder.playButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(messageList.get(position).getContent());
                    mediaPlayer.prepare();
                    holder.playSeekBar.setMax(mediaPlayer.getDuration());
//                    ((ChatRoomActivity)context).runOnUiThread(runnable);
                    runnable.run();
                    holder.playSeekBar.setProgress(0);
                    mediaPlayer.start();
                    holder.playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if(mediaPlayer != null && fromUser){
                                mediaPlayer.seekTo(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String senderPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        if(messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("text"))
            return MSG_Text_Sent;
        else if(!messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("text"))
            return MSG_Text_Recieved;
        else if(messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("image"))
            return MSG_Image_Sent;
        else if(!messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("image"))
            return MSG_Image_Recieved;
        else if(messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("contact"))
            return MSG_Contact_Sent;
        else if(!messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("contact"))
            return MSG_Contact_Recieved;
        else if(messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("loc"))
            return MSG_Loc_Sent;
        else if(!messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("loc"))
            return MSG_Loc_Recieved;
        else if(messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("record"))
            return MSG_record_Sent;
        else if(!messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("record"))
            return MSG_record_Recieved;
        else if(messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("document"))
            return MSG_document_Sent;
        else if(!messageList.get(position).getSender().equals(senderPhoneNumber) && messageList.get(position).getType().equals("document"))
            return MSG_document_Recieved;
        return -1;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView, seenTextView;
        TextView contactNameTextView, contactNumberTextView;
        TextView dateTextView;
        ImageView messageImageView;
        ImageView contactImageView;
        ImageView micImageView;
        Button playButton;
        SeekBar playSeekBar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.getId() == R.id.adapterTextMessageContainer) {
                messageTextView = itemView.findViewById(R.id.messageTextView);
            }
            else if(itemView.getId() == R.id.adapterImageMessageContainer){
                messageImageView = itemView.findViewById(R.id.messageImageView);
            }
            else if(itemView.getId() == R.id.adapterContactMessageContainer){
                contactImageView = itemView.findViewById(R.id.contactImageView);
                contactNameTextView = itemView.findViewById(R.id.contactNameTextView);
                contactNumberTextView = itemView.findViewById(R.id.contactNumberTextView);
            }
            else if(itemView.getId() == R.id.adapterRecordMessageContainer){
                contactImageView = itemView.findViewById(R.id.contactImageView);
                micImageView = itemView.findViewById(R.id.micImageView);
                playButton = itemView.findViewById(R.id.playButton);
                playSeekBar = itemView.findViewById(R.id.playSeekBar);
            }
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            seenTextView = itemView.findViewById(R.id.seenTextView);
        }
    }
}

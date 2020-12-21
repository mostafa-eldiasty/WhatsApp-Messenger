package com.example.whatsappmessenger.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappmessenger.Adapters.ChatAdapter;
import com.example.whatsappmessenger.Classes.Chat;
import com.example.whatsappmessenger.Notifications.ApiClient;
import com.example.whatsappmessenger.Notifications.ApiInterface;
import com.example.whatsappmessenger.Notifications.RootModel;
import com.example.whatsappmessenger.Classes.Message;
import com.example.whatsappmessenger.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public class ChatRoomActivity extends RootActivity implements View.OnClickListener {
    private static final int PICK_IMAGE = 1;
    private static final int PICK_CONTACT = 2;
    private static final int CAPTURE_IMAGE = 3;
    private static final int PICK_DOCUMENT = 4;
    private static final int RECORD_REQUEST_CODE = 5;
    private static final int LOCATION_REQUEST_CODE = 6;
    private MediaRecorder myAudioRecorder;

    FloatingActionButton sendActionButton, recordingActionButton;
    EditText messageEditText;
    ImageView takePicImageView;
    ChatAdapter chatAdapter;
    RecyclerView chatRecyclerView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Intent intent;
    String senderPhoneNumber, receiverPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendActionButton = findViewById(R.id.sendActionButton);
        recordingActionButton = findViewById(R.id.recordingActionButton);
        messageEditText = findViewById(R.id.messageEditText);
        takePicImageView = findViewById(R.id.takePicImageView);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        database = FirebaseDatabase.getInstance();
        intent = getIntent();

        messageEditText.addTextChangedListener(textWatcher);
        sendActionButton.setOnClickListener(this);
        findViewById(R.id.sendAttatchmentImageView).setOnClickListener(this);
        findViewById(R.id.backImageView).setOnClickListener(this);
        takePicImageView.setOnClickListener(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        senderPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        receiverPhoneNumber = intent.getStringExtra("number");

        recordingActionButton.setOnTouchListener(new View.OnTouchListener() {
            String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (ContextCompat.checkSelfPermission(ChatRoomActivity.this, Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ChatRoomActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(ChatRoomActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_REQUEST_CODE);
                }
                else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        try {
                            myAudioRecorder = new MediaRecorder();
                            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                            myAudioRecorder.setAudioEncodingBitRate(16);
                            myAudioRecorder.setAudioSamplingRate(44100);
                            myAudioRecorder.setOutputFile(outputFile);

                            recordingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        recordingActionButton.setSize(FloatingActionButton.SIZE_MINI);
                        myAudioRecorder.stop();
                        myAudioRecorder.release();

                        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("messages_records").child(UUID.randomUUID() + ".mp3");
                        Uri uri = Uri.fromFile(new File(outputFile));
                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        sendMessageToServer(uri.toString(), "record");
                                    }
                                });
                            }
                        });

                        return true;
                    }
                }

                return false;
            }
        });

        Query query = database.getReference("chats").endAt(senderPhoneNumber + "\uf8ff","sender_receiver");
        query.addValueEventListener(new ValueEventListener() {
            List<Message> messageList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getChildren() == null)
                    return;

                messageList = new ArrayList<>();
                Iterator it = snapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot chat = (DataSnapshot) it.next();
                    String[] sender_receiver = chat.child("sender_receiver").getValue().toString().split("_");
                    if (sender_receiver[1].equals(receiverPhoneNumber) && sender_receiver[0].equals(senderPhoneNumber)) {
                        messageList.add(new Message(chat.child("message").getValue().toString(), sender_receiver[0], chat.child("time").getValue().toString(),
                                chat.child("status").getValue().toString(), chat.child("type").getValue().toString()));
                    } else if (sender_receiver[0].equals(receiverPhoneNumber) && sender_receiver[1].equals(senderPhoneNumber)) {
                        if (getLifecycle().getCurrentState().name().equals("RESUMED"))
                            chat.child("status").getRef().setValue("seen");

                        messageList.add(new Message(chat.child("message").getValue().toString(), sender_receiver[0], chat.child("time").getValue().toString(),
                                "no_status", chat.child("type").getValue().toString()));
                    }
                }
                chatAdapter = new ChatAdapter(ChatRoomActivity.this, messageList);
                chatRecyclerView.setAdapter(chatAdapter);
                chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));
                ((LinearLayoutManager) chatRecyclerView.getLayoutManager()).setStackFromEnd(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                sendActionButton.hide();
                recordingActionButton.show();
                takePicImageView.setVisibility(View.VISIBLE);
            } else {
                sendActionButton.show();
                recordingActionButton.hide();
                takePicImageView.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        TextView contactNameTextView = findViewById(R.id.contactNameTextView);
        String name = intent.getStringExtra("name");
        contactNameTextView.setText(name);

        myRef = FirebaseDatabase.getInstance().getReference("users/" + receiverPhoneNumber);
        myRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Date currentTime = Calendar.getInstance().getTime();
                String date = new SimpleDateFormat("dd/MM/yyyy").format(currentTime);

                String[] status = snapshot.getValue(String.class).split(" ");

                TextView onlineStatusTextView = findViewById(R.id.onlineStatusTextView);
                if (status[0].equals("online"))
                    onlineStatusTextView.setText(status[0]);
                else if (status[0].equals(date))
                    onlineStatusTextView.setText("last seen today at " + status[1] + " " + status[2]);
                else
                    onlineStatusTextView.setText("last seen " + status[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView contactImageView = findViewById(R.id.contactImageView);
        String imageURL = intent.getStringExtra("image");
        Glide.with(this).load(imageURL).centerInside().into(contactImageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.backImageView):
                finish();
                break;

            case (R.id.takePicImageView):
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE);
                break;

            case (R.id.sendActionButton):
                sendMessageToServer(messageEditText.getText().toString(), "text");
                messageEditText.setText("");
                break;

            case (R.id.sendAttatchmentImageView):
                GridLayout attatchmentLayout = findViewById(R.id.attatchmentLayout);
                if (attatchmentLayout.getVisibility() == View.VISIBLE)
                    attatchmentLayout.setVisibility(View.GONE);
                else
                    attatchmentLayout.setVisibility(View.VISIBLE);

                findViewById(R.id.galleryAttachmentLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    }
                });

                findViewById(R.id.documentAttachmentLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/*");
                        startActivityForResult(intent, PICK_DOCUMENT);
                    }
                });

                findViewById(R.id.contactAttachmentLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_CONTACT);
                    }
                });

                findViewById(R.id.loactionAttachmentLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ContextCompat.checkSelfPermission(ChatRoomActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ChatRoomActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                        } else {
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    if (location != null) {
                                        String latitude = String.valueOf(location.getLatitude());
                                        String longitude = String.valueOf(location.getLongitude());

                                        Date currentTime = Calendar.getInstance().getTime();
                                        String datetime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(currentTime);

                                        sendMessageToServer(latitude + "," + longitude, "loc");
                                    } else {
                                        new AlertDialog.Builder(ChatRoomActivity.this)
                                                .setTitle("Issue")
                                                .setMessage("Something went wrong\nCheck your internet connection and your GPS")
                                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {}});
                                    }
                                }
                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {}
                                @Override
                                public void onProviderEnabled(String provider) {}
                                @Override
                                public void onProviderDisabled(String provider) {}
                            },null);

                        }

                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest);

                        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(ChatRoomActivity.this).checkLocationSettings(builder.build());
                        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                                try {
                                    LocationSettingsResponse response = task.getResult(ApiException.class);
                                } catch (ApiException exception) {
                                    if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                                        try {
                                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                                            resolvable.startResolutionForResult(
                                                    ChatRoomActivity.this,
                                                    LocationRequest.PRIORITY_HIGH_ACCURACY);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                }
                            }
                        });
                    }
                });
                break;
        }
    }

    private void sendMessageToServer(String messageContent, String messageType){
        Date currentTime = Calendar.getInstance().getTime();
        String datetime = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(currentTime);

        myRef = FirebaseDatabase.getInstance().getReference("chats");
        String key = myRef.push().getKey();
        Map<String, String> chat = new HashMap<>();
        chat.put("id", key);
        chat.put("sender_receiver", senderPhoneNumber + "_" + receiverPhoneNumber);
        chat.put("message", messageContent);
        chat.put("time", datetime);
        chat.put("status", "sent");
        chat.put("type", messageType);

        myRef.child(key).setValue(chat);

        sendNotification();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        return;

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                            0, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    String latitude = String.valueOf(location.getLatitude());
                                    String longitude = String.valueOf(location.getLongitude());
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                }

                                @Override
                                public void onProviderEnabled(String provider) {

                                }

                                @Override
                                public void onProviderDisabled(String provider) {

                                }
                            });
                } else {
                    //not granted
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri profileImageURI = data.getData();
            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                    .child("messages_images/" + UUID.randomUUID().toString());

            profileImageRef.putFile(profileImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sendMessageToServer(uri.toString(), "image");
                        }
                    });
                }
            });
        }
        else if (requestCode == PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = this.getContentResolver().query(contactUri, projection,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex).trim().replace(" ", "");

                if (number.trim().charAt(0) != '+') {
                    TelephonyManager manager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);

                    try {
                        Phonenumber.PhoneNumber currentContactNumber = PhoneNumberUtil.createInstance(this).parse(number, manager.getSimCountryIso().toUpperCase());
                        number = ("+" + currentContactNumber.getCountryCode() + currentContactNumber.getNationalNumber());

                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }
                }

                sendMessageToServer(number, "contact");
            }
        }

        else if (requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("messages_images").child(UUID.randomUUID().toString());
            storageReference.putBytes(bitmapdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                sendMessageToServer(uri.toString(), "image");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatRoomActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
        }

        else if (requestCode == PICK_DOCUMENT && resultCode == RESULT_OK && data != null) {
            Uri documentURI = data.getData();
            final StorageReference documentRef = FirebaseStorage.getInstance().getReference()
                    .child("messages_documents/" + UUID.randomUUID().toString());

            documentRef.putFile(documentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    documentRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sendMessageToServer(uri.toString(), "document");
                        }
                    });
                }
            });
        }
    }

    private void sendNotification() {

        myRef = FirebaseDatabase.getInstance().getReference("users").child(receiverPhoneNumber).child("token");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                RootModel rootModel = new RootModel(snapshot.getValue().toString());

                ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
                retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(rootModel);

                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        Log.d("TAG", "onResponse: ");
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
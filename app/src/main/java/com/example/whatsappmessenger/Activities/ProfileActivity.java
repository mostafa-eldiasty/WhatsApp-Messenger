package com.example.whatsappmessenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.whatsappmessenger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends RootActivity implements View.OnClickListener {

    BottomSheetDialog bottomSheetDialog;
    FloatingActionButton changePic;
    ConstraintLayout userNameLayout;
    ConstraintLayout userAboutLayout;

    TextView nameTextView,aboutTextView;
    ImageView userImageView;
    TextView bottomSheetEditText;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadUserProfileInfo();

        changePic = findViewById(R.id.changePicFloatingActionButton);
        userNameLayout = findViewById(R.id.userNameLayout);
        userAboutLayout = findViewById(R.id.userAboutLayout);
        nameTextView = findViewById(R.id.nameTextView);
        aboutTextView = findViewById(R.id.aboutTextView);
        userImageView = findViewById(R.id.userImageView);
        bottomSheetDialog = new BottomSheetDialog(this);

        changePic.setOnClickListener(this);
        userNameLayout.setOnClickListener(this);
        userAboutLayout.setOnClickListener(this);
        findViewById(R.id.backImageView).setOnClickListener(this);

    }

    private void loadUserProfileInfo() {

        String currentUSerPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUSerPhoneNumber);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,String> userProfileInfo = ((HashMap)snapshot.getValue());
                String name = userProfileInfo.get("name");
                String about = userProfileInfo.get("about");
                String profileImagePath = userProfileInfo.get("profile_image_path");

                nameTextView.setText(name);
                aboutTextView.setText(about);
                try {
                    Glide.with(ProfileActivity.this).load(profileImagePath).centerInside().into(userImageView);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.changePicFloatingActionButton:
                View view = getLayoutInflater().inflate(R.layout.dialog_change_profile_image,null);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.findViewById(R.id.deleteLayout).setOnClickListener(this);
                bottomSheetDialog.findViewById(R.id.setGalleryLayout).setOnClickListener(this);
                bottomSheetDialog.show();
                break;

            case R.id.userNameLayout:
                bottomSheetDialog.setContentView(R.layout.dialog_change_profile_name);
                bottomSheetDialog.findViewById(R.id.saveTextView).setOnClickListener(this);
                bottomSheetDialog.findViewById(R.id.cancelTextView).setOnClickListener(this);
                bottomSheetDialog.show();

                bottomSheetEditText = bottomSheetDialog.findViewById(R.id.bottomSheetEditText);
                bottomSheetEditText.setText(nameTextView.getText());

                String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userPhoneNumber).child("name");

                bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;

            case R.id.userAboutLayout:
                bottomSheetDialog.setContentView(R.layout.dialog_change_profile_about);
                bottomSheetDialog.findViewById(R.id.saveTextView).setOnClickListener(this);
                bottomSheetDialog.findViewById(R.id.cancelTextView).setOnClickListener(this);
                bottomSheetDialog.show();

                bottomSheetEditText = bottomSheetDialog.findViewById(R.id.bottomSheetEditText);
                bottomSheetEditText.setText(aboutTextView.getText());

                userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userPhoneNumber).child("about");

                bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;

            case R.id.deleteLayout:
                Uri profileImageURI = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + this.getResources().getResourceEntryName(
                        R.drawable.profile_icon_round));
                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                        .child("profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                profileImageRef.putFile(profileImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                database.child("users/" +  FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child("profile_image_path").setValue(uri.toString());
                            }
                        });
                    }
                });
                break;

            case R.id.setGalleryLayout:
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
                break;

            case R.id.saveTextView:
                databaseReference.setValue(bottomSheetEditText.getText().toString().trim());
                bottomSheetDialog.cancel();
            break;

            case R.id.cancelTextView:
                bottomSheetDialog.cancel();
            break;

            case R.id.backImageView:
                finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userImageView.setImageURI(result.getUri());
                Uri profileImageURI = result.getUri();

                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                        .child("profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                profileImageRef.putFile(profileImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                database.child("users/" +  FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child("profile_image_path").setValue(uri.toString());
                            }
                        });
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                try {
                    throw result.getError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
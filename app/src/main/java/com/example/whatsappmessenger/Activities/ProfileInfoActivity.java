package com.example.whatsappmessenger.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.whatsappmessenger.Classes.User;
import com.example.whatsappmessenger.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileInfoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView profileImage;
    EditText userName;
    Button nextButton;
    Uri profileImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        userName = findViewById(R.id.bottomSheetEditText);
        nextButton = findViewById(R.id.nextButton);
        profileImage = findViewById(R.id.profileImageView);
        profileImageURI = Uri.parse("android.resource://" + getPackageName()+ "/" + R.drawable.camera_icon);

        profileImage.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileImageView:
                 CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                break;

            case R.id.nextButton:
                User user = new User();
                user.setContext(ProfileInfoActivity.this);
                user.saveProfileInfo(profileImageURI, userName.getText().toString());
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImage.setImageURI(result.getUri());
                profileImageURI = result.getUri();
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
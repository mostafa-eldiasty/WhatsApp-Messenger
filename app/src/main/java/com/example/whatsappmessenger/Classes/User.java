package com.example.whatsappmessenger.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.whatsappmessenger.Activities.ChatsActivity;
import com.example.whatsappmessenger.Activities.ProfileInfoActivity;
import com.example.whatsappmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class User {
    Context context;
    String verificationCodeBytheSystem;

    public void setContext(Context context) {
        this.context = context;
    }

    public String getVerificationCodeBytheSystem() {
        return verificationCodeBytheSystem;
    }

    public Map<String, String> loadCountries(Spinner countrySpinner) {
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
        List<String> countriesInList = new ArrayList<>(countries.keySet());
        Collections.sort(countriesInList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, countriesInList);
        countrySpinner.setAdapter(adapter);
        countrySpinner.setSelection(adapter.getPosition("Egypt"));

        return countries;
    }

    public void sendSms(Intent smsCodeConfirmationIntent){
        TextView verifyNumberTextView = ((Activity)context).findViewById(R.id.verifyNumberTextView);

        String userPhoneNumber = smsCodeConfirmationIntent.getStringExtra("userPhoneNumber");
        verifyNumberTextView.setText("Verify " + userPhoneNumber);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userPhoneNumber,
                60,
                TimeUnit.SECONDS,
                (Activity) context,
                mCallbacks());
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            EditText smsEditText = ((Activity) context).findViewById(R.id.smsEditText);

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                final String code = credential.getSmsCode();
                if (code != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCodeBytheSystem, code);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                smsEditText.setText("" + code);

                                Intent intent = new Intent(context, ProfileInfoActivity.class);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                verificationCodeBytheSystem = verificationId;
            }
        };
    }

    public void saveProfileInfo(Uri profileImageURI, final String userName){
        if(userName.trim() == "") {
            Toast.makeText(context, "user name field can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference profileImageRef = storageRef.child("profile_images/" + auth.getCurrentUser().getUid());

        profileImageRef.putFile(profileImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();

                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        database.child("users/" + auth.getCurrentUser().getPhoneNumber()).child("name").setValue(userName);
                        database.child("users/" + auth.getCurrentUser().getPhoneNumber()).child("about").setValue("Hey, I'm using WhatsappMessenger");
                        database.child("users/" + auth.getCurrentUser().getPhoneNumber()).child("profile_image_path").setValue(uri.toString());

                        Intent intent = new Intent(context, ChatsActivity.class);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }
}

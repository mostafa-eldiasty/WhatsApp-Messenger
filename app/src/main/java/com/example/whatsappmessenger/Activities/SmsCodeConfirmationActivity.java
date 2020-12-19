package com.example.whatsappmessenger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.whatsappmessenger.Classes.User;
import com.example.whatsappmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class SmsCodeConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    Button verifyButton;
    EditText smsEditText;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_code_confirmation);

        user = new User();
        user.setContext(this);
        user.sendSms(getIntent());

        verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(this);

        smsEditText = findViewById(R.id.smsEditText);
        smsEditText.addTextChangedListener(new TextWatcher() {
            String text;
            boolean delete = false;
            static final int CODE_SIZE = 6;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text = smsEditText.getText().toString();
                if (count > after)
                    delete = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringBuilder sb = new StringBuilder(s.toString());
                int replacePosition = smsEditText.getSelectionEnd();

                if (s.length() != CODE_SIZE) {
                    if (!delete) {
                        if (replacePosition < s.length())
                            sb.deleteCharAt(replacePosition);
                    } else {
                        sb.insert(replacePosition, '_');
                    }

                    if (replacePosition < s.length() || delete) {
                        smsEditText.setText(sb.toString());
                        smsEditText.setSelection(replacePosition);
                    } else {
                        smsEditText.setText(text);
                        smsEditText.setSelection(replacePosition - 1);
                    }
                }
                delete = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        final String userInputCode = "" + smsEditText.getText();

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(user.getVerificationCodeBytheSystem(), userInputCode);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(SmsCodeConfirmationActivity.this, ProfileInfoActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SmsCodeConfirmationActivity.this, "wrong code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
package com.example.whatsappmessenger.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.whatsappmessenger.Classes.User;
import com.example.whatsappmessenger.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner countrySpinner;
    EditText countryCodeEditText;
    Button nextButton;
    EditText phoneNumberEditText;
    Map<String,String> countries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this,ChatsActivity.class);
            startActivity(intent);
        }

        countrySpinner = findViewById(R.id.countrySpinner);
        countryCodeEditText = findViewById(R.id.countryCodeEditText);
        nextButton = findViewById(R.id.nextButton);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        User user = new User();
        user.setContext(this);
        countries = user.loadCountries(countrySpinner);
        String countryCode = "" + PhoneNumberUtil.createInstance(this).getCountryCodeForRegion(countries.get(countrySpinner.getSelectedItem()));
        countryCodeEditText.setText("+ " + countryCode);

        countrySpinner.setOnItemSelectedListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String countryCode = "" + PhoneNumberUtil.createInstance(this).getCountryCodeForRegion(countries.get(countrySpinner.getSelectedItem()));
        countryCodeEditText.setText("+" + countryCode);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        String number = phoneNumberEditText.getText().toString();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(this);
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(number, countries.get(countrySpinner.getSelectedItem()));
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        if(phoneNumber != null)
            if(phoneUtil.isValidNumber(phoneNumber)) {
                Intent smsCodeConfirmationIntent = new Intent(LoginActivity.this,SmsCodeConfirmationActivity.class);
                smsCodeConfirmationIntent.putExtra("userPhoneNumber", countryCodeEditText.getText().toString() + phoneNumberEditText.getText().toString());
                startActivity(smsCodeConfirmationIntent);
            }
            else
                Toast.makeText(this,"phone number you entered is not valid",Toast.LENGTH_SHORT).show();
    }
}
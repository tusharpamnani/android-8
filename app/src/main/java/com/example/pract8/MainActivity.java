package com.example.pract8;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    private EditText usernameField, bloodGroupField, cityField;
    private Button addButton, fetchButton, fetchAllButton, sendSmsButton;
    private TextView displayData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameField = findViewById(R.id.usernameField);
        bloodGroupField = findViewById(R.id.bloodGroupField);
        cityField = findViewById(R.id.cityField);
        addButton = findViewById(R.id.addButton);
        fetchButton = findViewById(R.id.fetchButton);
        fetchAllButton = findViewById(R.id.fetchAllButton);
        sendSmsButton = findViewById(R.id.sendSmsButton);
        displayData = findViewById(R.id.displayData);

        dbHelper = new DatabaseHelper(this);

        // Add data to the database
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String bloodGroup = bloodGroupField.getText().toString();
                String city = cityField.getText().toString();

                if (!username.isEmpty() && !bloodGroup.isEmpty() && !city.isEmpty()) {
                    boolean isInserted = dbHelper.insertData(username, bloodGroup, city);
                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Fetch single user data
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                Cursor cursor = dbHelper.getData(username);
                if (cursor.moveToFirst()) {
                    String data = "Username: " + cursor.getString(1) + "\nBlood Group: " + cursor.getString(2) + "\nCity: " + cursor.getString(3);
                    displayData.setText(data);
                } else {
                    displayData.setText("No data found!");
                }
            }
        });

        // Fetch all data
        fetchAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = dbHelper.getAllData();
                StringBuilder data = new StringBuilder();
                if (cursor.moveToFirst()) {
                    do {
                        data.append("Username: ").append(cursor.getString(1))
                                .append("\nBlood Group: ").append(cursor.getString(2))
                                .append("\nCity: ").append(cursor.getString(3)).append("\n\n");
                    } while (cursor.moveToNext());
                    displayData.setText(data.toString());
                } else {
                    displayData.setText("No data found!");
                }
            }
        });

        // Send SMS
        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                } else {
                    sendSMS();
                }
            }
        });
    }

    // Sending SMS
    private void sendSMS() {
        String phoneNumber = ((EditText) findViewById(R.id.phoneNumberField)).getText().toString();  // Get the phone number from the input field
        String message = "Hello, this is a test message from the Blood Bank app.";

        if (phoneNumber.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(MainActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "SMS Failed to Send", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    // Handling the permission result for SMS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);  // Call to superclass

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();  // If permission is granted, send the SMS
            } else {
                Toast.makeText(MainActivity.this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

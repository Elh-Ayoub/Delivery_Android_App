package com.ElHaddadi.orderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ElHaddadi.orderapp.Common.Common;
import com.ElHaddadi.orderapp.Database.Database;
import com.ElHaddadi.orderapp.Model.Complaint;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Help extends AppCompatActivity {
Button send;
MaterialEditText editComplaint;
FirebaseDatabase database;
DatabaseReference complaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        editComplaint = findViewById(R.id.editComplaint);
        send = findViewById(R.id.send_btn);
        database = FirebaseDatabase.getInstance();
        complaints = database.getReference("Complaints");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editComplaint.getText().toString().isEmpty()){
                Complaint complaint = new Complaint(Common.currentUser.getName(),
                        Common.currentUser.getPhone(),
                        editComplaint.getText().toString());
                complaints.child(String.valueOf(System.currentTimeMillis())).setValue(complaint);
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Help.this, "Thank you! we got your message", Toast.LENGTH_SHORT).show();
                finish();
                }else{
                    Toast.makeText(Help.this, "Complaints field is empty!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
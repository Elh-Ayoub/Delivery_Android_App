package com.ElHaddadi.orderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ElHaddadi.orderapp.Common.Common;
import com.ElHaddadi.orderapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class sign_Up extends AppCompatActivity {
MaterialEditText editPhone2, editPassword2, editName;
Button btnSignUp2;
TextView txtSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);
        editName = findViewById(R.id.editName);
        editPassword2 = findViewById(R.id.editPassword2);
        editPhone2 = findViewById(R.id.editPhone2);
        btnSignUp2 = findViewById(R.id.btnSignUp2);
        txtSignIn = findViewById(R.id.txtSignIn);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet( getBaseContext() )) {
                    final ProgressDialog mDialog = new ProgressDialog( sign_Up.this );
                    mDialog.setMessage( "Please Wait..." );
                    mDialog.show();
                    table_user.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child( editPhone2.getText().toString() ).exists()) {
                                mDialog.dismiss();
                                Toast.makeText( sign_Up.this, "This phone number already exist", Toast.LENGTH_SHORT ).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User( editName.getText().toString(), editPassword2.getText().toString() );
                                table_user.child( editPhone2.getText().toString() ).setValue( user );
                                Toast.makeText( sign_Up.this, "Sign up successfully!", Toast.LENGTH_SHORT ).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                }else{
                    Toast.makeText( sign_Up.this, "Please Check your connection", Toast.LENGTH_SHORT ).show();
                    return;
                }
            }
        });
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(sign_Up.this , sign_In.class);
                startActivity(in);
            }
        });
    }
}
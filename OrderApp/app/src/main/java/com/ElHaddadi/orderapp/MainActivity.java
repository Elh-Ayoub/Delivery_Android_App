package com.ElHaddadi.orderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ElHaddadi.orderapp.Common.Common;
import com.ElHaddadi.orderapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {
Button btnSignIn, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        Paper.init( this );
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_Up.class);
                startActivity(intent);
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_In.class);
                startActivity(intent);
            }
        });

        String user = Paper.book().read( Common.USER_KEY );
        String pwd = Paper.book().read( Common.PWD_KEY );
        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }
        }

    }

    private void login(final String phone, final String pwd) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        if(Common.isConnectedToInternet( getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog( MainActivity.this );
            mDialog.setMessage( "Please Wait..." );
            mDialog.show();

            table_user.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDialog.dismiss();
                    if (dataSnapshot.child(phone).exists()) {
                        User user = dataSnapshot.child(phone).getValue( User.class );
                        user.setPhone( phone );
                        if (user.getPassword().equals(pwd)){
                            {
                                Toast.makeText( MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT ).show();
                                Intent homeintent = new Intent( MainActivity.this, Home.class );
                                Common.currentUser = user;
                                startActivity( homeintent );
                                finish();
                            }

                        } else {
                            Toast.makeText( MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        Toast.makeText( MainActivity.this, "User not exist", Toast.LENGTH_SHORT ).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }else{
            Toast.makeText( MainActivity.this, "Please Check your connection", Toast.LENGTH_SHORT ).show();
            return;
        }
    }
}
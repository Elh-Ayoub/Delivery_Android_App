package com.ElHaddadi.orderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ElHaddadi.orderapp.Common.Common;
import com.ElHaddadi.orderapp.Database.Database;
import com.ElHaddadi.orderapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;


public class sign_In extends AppCompatActivity {
EditText editPhone, editPassword;
Button btnSignIn2;
TextView txtSignUp;
CheckBox ckbRemember;
TextView forgetPassword;
MaterialEditText NameEdit, PhoneEdit;
FirebaseDatabase db;
DatabaseReference forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn2 = findViewById(R.id.btnSignIn2);
        txtSignUp = findViewById(R.id.txtSignIn);
        ckbRemember = findViewById( R.id.ckbRemember );
        forgetPassword = findViewById( R.id.forget );
        db =  FirebaseDatabase.getInstance();
        forgot_password = db.getReference("forgot_password");
        Paper.init(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignIn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet( getBaseContext())) {

                    if(ckbRemember.isChecked()){
                        Paper.book().write( Common.USER_KEY, editPhone.getText().toString() );
                        Paper.book().write( Common.PWD_KEY, editPassword.getText().toString() );
                    }

                    final ProgressDialog mDialog = new ProgressDialog( sign_In.this );
                    mDialog.setMessage( "Please Wait..." );
                    mDialog.show();

                    table_user.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mDialog.dismiss();
                            if (dataSnapshot.child( editPhone.getText().toString() ).exists()) {
                                User user = dataSnapshot.child( editPhone.getText().toString() ).getValue( User.class );
                                user.setPhone( editPhone.getText().toString() );
                                if (user.getPassword().equals( editPassword.getText().toString() )) {
                                    {
                                        Toast.makeText( sign_In.this, "Sign in successfully!", Toast.LENGTH_SHORT ).show();
                                        Intent homeintent = new Intent( sign_In.this, Home.class );
                                        Common.currentUser = user;
                                        startActivity( homeintent );
                                        finish();
                                    }

                                } else {
                                    Toast.makeText( sign_In.this, "Wrong password!", Toast.LENGTH_SHORT ).show();
                                }
                            } else {
                                Toast.makeText( sign_In.this, "User not exist", Toast.LENGTH_SHORT ).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                }else{
                    Toast.makeText( sign_In.this, "Please Check your connection", Toast.LENGTH_SHORT ).show();
                    return;
                }
            }
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_In.this, sign_Up.class);
                startActivity(intent);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(sign_In.this);
        alertDialog.setTitle("Forgot Password ?");
        alertDialog.setMessage("Enter you name and phone number: ");
        LayoutInflater inflater = this.getLayoutInflater();
        View password_forget_layout = inflater.inflate(R.layout.password_forgot_layout, null);
        NameEdit = password_forget_layout.findViewById(R.id.NameEdit);
        PhoneEdit = password_forget_layout.findViewById(R.id.PhoneEdit);

        alertDialog.setView(password_forget_layout);
        alertDialog.setIcon(R.drawable.ic_build_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User fUser = new User(
                        NameEdit.getText().toString(),
                        PhoneEdit.getText().toString()
                );
                forgot_password.child(String.valueOf(System.currentTimeMillis())).setValue(fUser);
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(sign_In.this, "Thank You!, you will get a message", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
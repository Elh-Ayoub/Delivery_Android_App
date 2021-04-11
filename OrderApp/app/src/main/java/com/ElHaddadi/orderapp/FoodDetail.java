package com.ElHaddadi.orderapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ElHaddadi.orderapp.Common.Common;
import com.ElHaddadi.orderapp.Database.Database;
import com.ElHaddadi.orderapp.Model.Food;
import com.ElHaddadi.orderapp.Model.Order;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FoodDetail extends AppCompatActivity {
TextView food_name, food_price, food_description;
ImageView img_food;
CollapsingToolbarLayout collapsingToolbarLayout;
FloatingActionButton btn_cart;
ElegantNumberButton numberButton;
String FoodId = "";
FirebaseDatabase database;
DatabaseReference foods;
Food currentFood;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");
        numberButton = findViewById(R.id.number_button);
        btn_cart = findViewById(R.id.btn_cart);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        img_food = findViewById(R.id.img_food);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_description = findViewById(R.id.food_description);
        collapsingToolbarLayout.setCollapsedTitleGravity(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedAppBar);

        if (getIntent() != null)
            FoodId = getIntent().getStringExtra("FoodId");
        if (!FoodId.isEmpty()) {
            if(Common.isConnectedToInternet( getBaseContext() )) {
                getDetailFood( FoodId );
            }else{
                Toast.makeText( FoodDetail.this, "Please Check your connection", Toast.LENGTH_SHORT ).show();
                return;
            }
        }
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new Database(getBaseContext()).addToCart(new Order(
                       FoodId, currentFood.getName(),numberButton.getNumber(),currentFood.getPrice(),currentFood.getDiscount()
               ));
                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDetailFood(final String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(img_food);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
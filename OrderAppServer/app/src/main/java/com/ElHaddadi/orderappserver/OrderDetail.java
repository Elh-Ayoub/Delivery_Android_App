package com.ElHaddadi.orderappserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.ElHaddadi.orderappserver.Common.Common;
import com.ElHaddadi.orderappserver.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {
TextView order_id, order_phone,order_address,order_total,order_comment, order_payment_method;
String order_id_value = "";
RecyclerView lstFoods;
RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        order_comment = findViewById(R.id.order_comment);
        order_payment_method = findViewById(R.id.order_payment_method);
        lstFoods = findViewById(R.id.lstFood);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setHasFixedSize(true);
        lstFoods.setLayoutManager(layoutManager);
        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_address.setText(Common.currentRequest.getAddress());
        order_total.setText(Common.currentRequest.getTotal());
        order_comment.setText(Common.currentRequest.getComment());
        order_payment_method.setText(Common.currentRequest.getPaymentState());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);
    }
}
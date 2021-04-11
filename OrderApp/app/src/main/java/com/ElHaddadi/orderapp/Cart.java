package com.ElHaddadi.orderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ElHaddadi.orderapp.Common.Common;
import com.ElHaddadi.orderapp.Common.Config;
import com.ElHaddadi.orderapp.Database.Database;
import com.ElHaddadi.orderapp.Model.Order;
import com.ElHaddadi.orderapp.Model.Request;
import com.ElHaddadi.orderapp.ViewHolder.CartAdapter;
import com.esotericsoftware.kryo.NotNull;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class Cart extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotalPrice;
    Place shippingAddress;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION).clientId(Config.PAYPAL_CLIENT_ID);
    String address, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size()>0 ) {
                    showAlertDialog();
                }else{
                    Toast.makeText( Cart.this, "Your cart is empty!!!", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        loadListFood();

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter you address: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);
        final MaterialEditText editAddress = order_address_comment.findViewById(R.id.editAddress);
       // final PlaceAutocompleteFragment editAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        final MaterialEditText editComment = order_address_comment.findViewById(R.id.editComment);
        final MaterialSpinner spinner = order_address_comment.findViewById(R.id.paymentSpinner);
        spinner.setItems("Cash on delivery","Online Payment");

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                address = editAddress.getText().toString();
                comment  =editComment.getText().toString();

          if(spinner.getText().toString().equals("Cash on delivery")){

              Request request = new Request(
                      Common.currentUser.getPhone(),
                      Common.currentUser.getName(),
                      address,
                      txtTotalPrice.getText().toString(), "0",
                      comment,
                      "Cash on delivery",
                      cart);

              requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
              new Database(getBaseContext()).cleanCart();
              Toast.makeText(Cart.this, "Thank You!, Order Place", Toast.LENGTH_SHORT).show();
              finish();
            }else if (spinner.getText().toString().equals("Online Payment")){

            String formatAmount = txtTotalPrice.getText().toString().replace("$", "");
            float amountGrivna = Float.parseFloat(formatAmount);
            float amountEuro = (float) (amountGrivna/30.44);
            String formatAmountFinal = Float.toString(amountEuro);

                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmountFinal), "EUR", "Moroccan food", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(), "0",
                                comment,
                                jsonObject.getJSONObject("response").getString("state"),
                                cart);

                        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, "Thank You!, Order Place", Toast.LENGTH_SHORT).show();
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for(Order order:cart) {
            try {
                total = total+ (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
            }catch(NumberFormatException ex){
                Toast.makeText(this, ""+order.getPrice(), Toast.LENGTH_SHORT).show();
            }
            }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals( Common.DELETE )){
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        cart.remove( position );
        new Database( this ).cleanCart();
        for(Order item: cart)
            new Database( this ).addToCart( item );
        loadListFood();
    }

}
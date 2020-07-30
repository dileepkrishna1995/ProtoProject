package com.dileep.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.dileep.ecommerce.Buyers.CartActivity;

public class OrderConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(OrderConfirmActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5550);

    }
}
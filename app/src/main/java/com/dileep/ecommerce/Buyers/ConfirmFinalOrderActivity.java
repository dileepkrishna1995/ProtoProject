package com.dileep.ecommerce.Buyers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dileep.ecommerce.OrderConfirmActivity;
import com.dileep.ecommerce.Prevalent.Prevalent;
import com.dileep.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditTxt, phoneEditTxt, addressEditTxt, cityEditTxt;
    private Button confirmOrderBtn;
    private ImageView confirmFinalOrderBackBtn;

    //view subtotal, totalamount
    private TextView subTotal, deliveryFee, totalToPay;
    private RelativeLayout cartDetailsLayout;

    private String totalAmount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
//        Toast.makeText(this, "Total Amount = " + totalAmount, Toast.LENGTH_SHORT).show();

        confirmFinalOrderBackBtn = (ImageView) findViewById(R.id.confirm_final_order_back_btn);
        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditTxt = (EditText) findViewById(R.id.shipment_name);
        phoneEditTxt = (EditText) findViewById(R.id.shipment_phone_number);
        addressEditTxt = (EditText) findViewById(R.id.shipment_address);
        cityEditTxt = (EditText) findViewById(R.id.shipment_city);

        //subtotal, totalamount
        subTotal = (TextView) findViewById(R.id.sub_total_number);
        deliveryFee = (TextView) findViewById(R.id.deliver_fee_number);
        totalToPay = (TextView) findViewById(R.id.total_amount_to_pay_number);
        cartDetailsLayout = (RelativeLayout) findViewById(R.id.cart_details_relative1);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });

        confirmFinalOrderBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmFinalOrderActivity.this, CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditTxt.getText().toString())) {
            Toast.makeText(this, "Please Provide the name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditTxt.getText().toString())) {
            Toast.makeText(this, "Please Provide the phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditTxt.getText().toString())) {
            Toast.makeText(this, "Please Provide the address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEditTxt.getText().toString())) {
            Toast.makeText(this, "Please Provide the city", Toast.LENGTH_SHORT).show();
        } else {
            ConfirmOrder();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Integer.valueOf(totalAmount) < 200)
        {
            subTotal.setText("₹ " + totalAmount + ".00");
            deliveryFee.setText("₹ 20.00");
            int totalToPaylast = Integer.valueOf(totalAmount) + 20 ;
            totalToPay.setText("₹ " + totalToPaylast + ".00");
        } else if(Integer.valueOf(totalAmount) >= 200)
        {
            subTotal.setText("₹ " + totalAmount + ".00");
            deliveryFee.setText("Free");
            totalToPay.setText("₹ " + totalAmount + ".00");
        }


    }

    private void ConfirmOrder() {

        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        //save current date
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        //save current time
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());


        //adds the order details into the orders table in the firebase data base
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());


        final HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", nameEditTxt.getText().toString());
        orderMap.put("phone", phoneEditTxt.getText().toString());
        orderMap.put("address", addressEditTxt.getText().toString());
        orderMap.put("city", cityEditTxt.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("state", "not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //removes from the cart list from the user View

                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ConfirmFinalOrderActivity.this, "your order has been placed sucessfuly", Toast.LENGTH_SHORT).show();


                                //notification for above oreo version mobile
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel("OrderConfirmed", "OrderConfirmed", NotificationManager.IMPORTANCE_DEFAULT);
                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                    manager.createNotificationChannel(channel);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(ConfirmFinalOrderActivity.this, "OrderConfirmed")
                                        .setContentTitle("We've Received your order!")
                                        .setSmallIcon(R.drawable.ic_message)
                                        .setAutoCancel(true)
                                        .setContentText("Thank you for your order");

                                //notification sound
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                builder.setSound(alarmSound);

                                //notification
                                NotificationManagerCompat manager = NotificationManagerCompat.from(ConfirmFinalOrderActivity.this);
                                manager.notify(999, builder.build());


                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, OrderConfirmActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });

                }

            }
        });
    }

}
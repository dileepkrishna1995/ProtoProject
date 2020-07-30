package com.dileep.ecommerce.Buyers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.dileep.ecommerce.HomeActivity;
import com.dileep.ecommerce.Model.Cart;
import com.dileep.ecommerce.Prevalent.Prevalent;
import com.dileep.ecommerce.R;
import com.dileep.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartActivity<RecycleView> extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView cartBackBtn, cartEmptyImage;
    private LottieAnimationView msg1Image;

    private Button NextProcessBtn, CancelPreviousBtn;
    private TextView txtTotalAmount, txtMsg1, clearAll;
    private int overTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        msg1Image = (LottieAnimationView) findViewById(R.id.msg1_image);
        cartEmptyImage = (ImageView) findViewById(R.id.cart_image_empty);
        cartBackBtn = (ImageView) findViewById(R.id.cart_back_btn);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        CancelPreviousBtn = (Button) findViewById(R.id.cancel_previous_btn);
        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);
        clearAll = (TextView) findViewById(R.id.clear_all);


        cartBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (overTotalPrice != 0) {
                    //deleting cart items from the admin view and user view
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).removeValue();

                    //displaying the cart_empty image
                    CancelPreviousBtn.setVisibility(View.GONE);
                    NextProcessBtn.setVisibility(View.GONE);
                    cartEmptyImage.setVisibility(View.VISIBLE);
                }
            }
        });

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(overTotalPrice != 0) {
                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    startActivity(intent);
                }
                else
                {
                    CancelPreviousBtn.setVisibility(View.GONE);
                    NextProcessBtn.setVisibility(View.GONE);
                    cartEmptyImage.setVisibility(View.VISIBLE);
                }
            }
        });

        CancelPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelintent = new Intent(CartActivity.this, HomeActivity.class);
                cancelintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                cancelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(cancelintent);
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                holder.txtProductQuantity.setText("Quantity: " + model.getQuantity() + " ×");
                holder.txtProductPrice.setText("Price: ₹ "  + model.getPrice() + ".00");
                holder.txtProductName.setText("Product " + model.getPname());
                Picasso.get().load(model.getImage()).into(holder.imageView);


                int oneTypeProductPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());

                overTotalPrice = overTotalPrice + oneTypeProductPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);

                                }
                                if(i == 1)
                                {
                                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue();
                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(CartActivity.this, "Item Removed Successfully.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        builder.show();
                    }
                });
            }


            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    private void CheckOrderState()
    {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {

                        txtTotalAmount.setText("Order");
                        recyclerView.setVisibility(View.GONE);

                        msg1Image.setVisibility(View.VISIBLE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, your final order has been shipped sucessfully, you will get your product at your door step");
                        NextProcessBtn.setVisibility(View.GONE);
                        CancelPreviousBtn.setVisibility(View.GONE);
                        clearAll.setVisibility(View.GONE);

//                        Toast.makeText(CartActivity.this, "you can purchase the more products, once you received the previous order", Toast.LENGTH_SHORT).show();

                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Order");
                        recyclerView.setVisibility(View.GONE);

                        msg1Image.setVisibility(View.VISIBLE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                        CancelPreviousBtn.setVisibility(View.GONE);
                        clearAll.setVisibility(View.GONE);

//                        Toast.makeText(CartActivity.this, "you can purchase the more products, once you received the previous order", Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
        startActivity(intent);
    }

}

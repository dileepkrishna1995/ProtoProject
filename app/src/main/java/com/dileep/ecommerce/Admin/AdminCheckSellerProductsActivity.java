package com.dileep.ecommerce.Admin;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dileep.ecommerce.MapViewActivity;
import com.dileep.ecommerce.Model.Seller;
import com.dileep.ecommerce.R;
import com.dileep.ecommerce.ViewHolder.SellerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class AdminCheckSellerProductsActivity extends AppCompatActivity {

    private ImageView adminCheckSellerbackBtn;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference sellerProductsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_seller_products);

        adminCheckSellerbackBtn = (ImageView) findViewById(R.id.admin_check_seller_products_back_btn);
        sellerProductsRef = FirebaseDatabase.getInstance().getReference().child("Seller Products");

        recyclerView = findViewById(R.id.seller_home_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adminCheckSellerbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCheckSellerProductsActivity.this, AdminHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Seller> options = new FirebaseRecyclerOptions.Builder<Seller>()
                .setQuery(sellerProductsRef.orderByChild("status").equalTo("Not Verified"), Seller.class).build();

        FirebaseRecyclerAdapter<Seller, SellerViewHolder> adapter = new FirebaseRecyclerAdapter<Seller, SellerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellerViewHolder holder, int position, @NonNull final Seller model)
            {
                holder.txtSellerName.setText(model.getName());
                holder.txtPhoneNumber.setText(model.getPhone());
                holder.txtSellerCity.setText(model.getCity());
                holder.txtSellerProduct.setText("Product : " + model.getProducttype());
                holder.txtSellerPrice.setText("Price = " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.imageView);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        final String productID = model.getPid();

                        CharSequence options[] = new CharSequence[]
                                {
                                        "yes",
                                        "No",
                                        "Locate"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckSellerProductsActivity.this);
                        builder.setTitle("Do You Want To Delete Product Or Locate");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position)
                            {
                                if(position == 0)
                                {
                                    deleteProduct(productID);
                                }
                                if(position == 1)
                                {

                                }
                                if(position == 2)
                                {
                                    Intent intent = new Intent(AdminCheckSellerProductsActivity.this, MapViewActivity.class);
                                    intent.putExtra("latitude", model.getLatitude());
                                    intent.putExtra("longitude", model.getLongitude());
                                    intent.putExtra("sellerName", model.getName());
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view, parent, false);
                SellerViewHolder holder = new SellerViewHolder(view);
                return holder;

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void deleteProduct(String productID)
    {

/*
        sellerProductsRef.child(productID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(AdminCheckSellerProductsActivity.this, "This Item has been Deleted, and it is not availiable from now", Toast.LENGTH_SHORT).show();
                    }
                });
*/

    }
}

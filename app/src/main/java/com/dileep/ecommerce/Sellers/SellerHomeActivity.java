package com.dileep.ecommerce.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dileep.ecommerce.Buyers.MainActivity;
import com.dileep.ecommerce.Model.Seller;
import com.dileep.ecommerce.R;
import com.dileep.ecommerce.ViewHolder.SellerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SellerHomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    BottomNavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);


        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Seller Products");

        recyclerView = findViewById(R.id.seller_home_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
        switch (menuItem.getItemId())
        {
            case R.id.navigation_home:
            Intent mainIntent = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
            break;

            case R.id.navigation_settings:
                Intent settingsIntent = new Intent(SellerHomeActivity.this, SellerDetailsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(settingsIntent);
                break;

            case R.id.navigation_logout:
                final FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

        }
        return true;
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Seller> options = new FirebaseRecyclerOptions.Builder<Seller>()
                .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Seller.class).build();

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
                                        "No"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do You Want To Delete Product");
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

        unverifiedProductsRef.child(productID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(SellerHomeActivity.this, "This Item has been Deleted, and it is not availiable from now", Toast.LENGTH_SHORT).show();


            }
        });

    }
}

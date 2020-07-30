package com.dileep.ecommerce.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dileep.ecommerce.R;


public class AdminProductCategoryActivity extends AppCompatActivity {

    private Button vegetables, fruits, foodgrains;


    //boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_category);


        vegetables = (Button) findViewById(R.id.vegetables_imageview);
        fruits = (Button) findViewById(R.id.fruits_imageview);
        foodgrains = (Button) findViewById(R.id.foodgrains_imageview);

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminProductCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "vegetables");
                startActivity(intent);

            }
        });

        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminProductCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "fruits");
                startActivity(intent);

            }
        });


        foodgrains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminProductCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "foodgrains");
                startActivity(intent);

            }
        });

    }
}
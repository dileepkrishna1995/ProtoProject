package com.dileep.ecommerce.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dileep.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String AdmnCategoryName, AdmnDescription, AdmnPrice, AdmnPname, saveCurrentDate, saveCurrentTime;
    private Button AdmnAddNewProductButton;
    private ImageView AdmnInputProductImage, AdminAddNewBackBtn;
    private EditText AdmnInputProductName, AdmnInputProductDescription, AdmnInputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference AdmnProductImageRef;
    private DatabaseReference AdmnProductsRef;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        AdmnCategoryName = getIntent().getExtras().get("category").toString();
        AdmnProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        AdmnProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AdminAddNewBackBtn = (ImageView) findViewById(R.id.admin_new_product_back_btn);

        AdmnAddNewProductButton = (Button) findViewById(R.id.admn_add_new_product);
        AdmnInputProductName = (EditText) findViewById(R.id.admn_product_name);
        AdmnInputProductDescription = (EditText) findViewById(R.id.admn_product_description);
        AdmnInputProductPrice = (EditText) findViewById(R.id.admn_product_price);
        AdmnInputProductImage = (ImageView) findViewById(R.id.admn_select_product_image);
        loadingBar = new ProgressDialog(this);

        AdminAddNewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminAddNewProductActivity.this, AdminHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        AdmnInputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        AdmnAddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

    }


    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/'");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null)
        {
            ImageUri = data.getData();
            AdmnInputProductImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData()
    {
        AdmnDescription = AdmnInputProductDescription.getText().toString();
        AdmnPrice = AdmnInputProductPrice.getText().toString();
        AdmnPname = AdmnInputProductName.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(AdmnDescription))
        {
            Toast.makeText(this, "Please write crop description...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(AdmnPrice))
        {
            Toast.makeText(this, "Please  write crop Price...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(AdmnPname))
        {
            Toast.makeText(this, "Please write crop Name...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreImageInformation();
        }

    }

    private void StoreImageInformation()
    {
        loadingBar.setTitle("Add New Crop");
        loadingBar.setMessage("Please wait, while we are adding new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM DD, YYYY");
        saveCurrentDate = currentDate.format(calendar.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:SS a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = AdmnProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewProductActivity.this, "Image Uploaded Succesfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "Got Product image Url sucessfully...", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }

                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase()
    {
        HashMap<String, Object> admnproductMap = new HashMap<>();
        admnproductMap.put("pid", productRandomKey);
        admnproductMap.put("date", saveCurrentDate);
        admnproductMap.put("time", saveCurrentTime);
        admnproductMap.put("description", AdmnDescription);
        admnproductMap.put("image", downloadImageUrl);
        admnproductMap.put("category", AdmnCategoryName);
        admnproductMap.put("price", AdmnPrice);
        admnproductMap.put("pname", AdmnPname);
        admnproductMap.put("productState", "Not Approved");


        AdmnProductsRef.child(productRandomKey).updateChildren(admnproductMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(AdminAddNewProductActivity.this, AdminHomeActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is added Successfully...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}

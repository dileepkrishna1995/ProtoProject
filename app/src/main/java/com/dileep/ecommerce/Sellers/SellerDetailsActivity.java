package com.dileep.ecommerce.Sellers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dileep.ecommerce.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerDetailsActivity extends AppCompatActivity {

    private String SellerName, SellerCity, ProductType, ProductPrice, saveCurrentDate, saveCurrentTime;
    private Button SellerDetailsUpload, SellerProductImageBtn, SellerGpsBtn;
    private EditText SellerNameET, SellerPhoneET, SellerCityET, ProductTypeET, ProductPriceET;

    private ImageView sellerDetailBackBtn;

    private DatabaseReference ProductsRef, sellerRef;
    private StorageReference ProductImageRef;
    private String productRandomKey, downloadImageUrl;
    private double sellerLatitude, sellerLongitude;


    private static final int GalleryPick = 1;
    private Uri ImageUri;


    private FirebaseAuth mAuth;
    private String currentUserId;


    private Button saveBtn;
    private ProgressDialog progressDialog;

    private static final String TAG = "SellerDetailsActivity";
    int LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_details);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Seller Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Seller Products");
        //sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers Products");

        sellerDetailBackBtn = findViewById(R.id.seller_detail_back_btn);

        SellerNameET = findViewById(R.id.seller_Detail_name_input);
        SellerPhoneET = findViewById(R.id.seller_Detail_phone_number_input);
        SellerCityET = findViewById(R.id.seller_Detail_city_input);
        ProductTypeET = findViewById(R.id.seller_Detail_product_type_input);
        ProductPriceET = findViewById(R.id.seller_Detail_product_price_input);
//        SellerGpsBtn = findViewById(R.id.seller_details_gps_upload);

        SellerProductImageBtn = findViewById(R.id.seller_details_image_upload);

        saveBtn = findViewById(R.id.seller_details_update);

        progressDialog = new ProgressDialog(this);

        SellerProductImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

/*

        validateUser();
*/



//        retriveUserInfo();


/*
        SellerGpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDetailsActivity.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
*/


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });
        sellerDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDetailsActivity.this, SellerHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();


        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    //Toast.makeText(SellerDetailsActivity.this, "Location" + location.getLatitude() + location.getLongitude(), Toast.LENGTH_SHORT).show();


                    sellerLatitude = Double.valueOf(location.getLatitude());
                    sellerLongitude = Double.valueOf(location.getLongitude());

                    //we have a location
             /*       Log.d(TAG, "OnSucess: " + location.toString());
                    Log.d(TAG, "OnSucess: " + location.getLatitude());
                    Log.d(TAG, "OnSucess: " + location.getLongitude());
             */   }
                else
                {
                    Log.d(TAG, "ONSucess: Location was null");
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"OnFailure : " + e.getLocalizedMessage());

            }
        });

    }

    private void askLocationPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Permission Granted
            } else {
                //Permission not granted
            }
        }

    }

    /*
    private void validateUser()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.exists())
                {
                    Intent settingIntent = new Intent(SellerDetailsActivity.this, SellerDetailsActivity.class);
                    startActivity(settingIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/


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
        }
    }



    private void saveUserData() {
        final String getUserName = SellerNameET.getText().toString();
        final String getPhoneNumber = SellerPhoneET.getText().toString();
        final String getCityName = SellerCityET.getText().toString();
        final String getProductType = ProductTypeET.getText().toString();
        final String getProductPrice = ProductPriceET.getText().toString();


        if (getUserName.equals("")) {
            Toast.makeText(this, "user name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (getPhoneNumber.equals("")) {
            Toast.makeText(this, "Phone Number is mandatory", Toast.LENGTH_SHORT).show();
        } else if (getCityName.equals("")) {
            Toast.makeText(this, "Village Name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (getProductType.equals("")) {
            Toast.makeText(this, "Crop Type is mandatory", Toast.LENGTH_SHORT).show();
        } else if (getProductPrice.equals("")) {
            Toast.makeText(this, "Price is mandatory", Toast.LENGTH_SHORT).show();
        } else if (ImageUri == null) {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        } else {
            StoreImageInformation();
        }
    }

    private void StoreImageInformation()
    {
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please wait,..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM DD, YYYY");
        saveCurrentDate = currentDate.format(calendar.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:SS a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(SellerDetailsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(SellerDetailsActivity.this, "Image Uploaded Succesfully...", Toast.LENGTH_SHORT).show();

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

                            Toast.makeText(SellerDetailsActivity.this, "Got Product image Url sucessfully...", Toast.LENGTH_SHORT).show();
                            SaveDateOnline();
                        }
                    }
                });
            }
        });
    }



    private void SaveDateOnline()
    {
        final String getUserName = SellerNameET.getText().toString();
        final String getPhoneNumber = SellerPhoneET.getText().toString();
        final String getCityName = SellerCityET.getText().toString();
        final String getProductType = ProductTypeET.getText().toString();
        final String getProductPrice = ProductPriceET.getText().toString();

        final HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("sid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        profileMap.put("pid", productRandomKey);
        profileMap.put("name", getUserName);
        profileMap.put("phone", getPhoneNumber);
        profileMap.put("city", getCityName);
        profileMap.put("producttype", getProductType);
        profileMap.put("price", getProductPrice);
        profileMap.put("image", downloadImageUrl);
        profileMap.put("date", saveCurrentDate);
        profileMap.put("time", saveCurrentTime);
        profileMap.put("status", "Not Verified");
        profileMap.put("latitude", sellerLatitude);
        profileMap.put("longitude", sellerLongitude);



        ProductsRef.child(productRandomKey).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Intent intent = new Intent(SellerDetailsActivity.this, SellerHomeActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(SellerDetailsActivity.this, "Product Details Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

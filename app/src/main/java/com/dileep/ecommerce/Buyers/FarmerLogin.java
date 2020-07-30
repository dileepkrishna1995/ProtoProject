package com.dileep.ecommerce.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dileep.ecommerce.Admin.AdminHomeActivity;
import com.dileep.ecommerce.HomeActivity;
import com.dileep.ecommerce.Model.Users;
import com.dileep.ecommerce.Prevalent.Prevalent;
import com.dileep.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class FarmerLogin extends AppCompatActivity {
    private Button farmerLoginButton;
    private EditText InputNumber, InputPassword;
    private ProgressDialog loadingBar;
    private TextView CustomerButton;
    private TextView AdminLink, NotAdminLink, ForgetPasswordLink;



    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);


        CustomerButton = (TextView) findViewById(R.id.is_customer_btn);
        farmerLoginButton = (Button) findViewById(R.id.farmer_login_btn);
        InputNumber = (EditText) findViewById(R.id.farmer_phone_no_input);
        InputPassword = (EditText) findViewById(R.id.farmer_password_input);

        AdminLink = (TextView) findViewById(R.id.farmer_admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.farmer_not_admin_panel_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.farmer_forgot_txtview);


        loadingBar = new ProgressDialog(this);


        chkBoxRememberMe = (CheckBox) findViewById(R.id.farmer_remember_checkbox);
        Paper.init(this);

        farmerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        CustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent customerintent = new Intent(FarmerLogin.this, RegisterActivity.class);
                customerintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(customerintent);
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farmerLoginButton.setText("Admin Login");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                chkBoxRememberMe.setVisibility(view.INVISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farmerLoginButton.setText("Customer Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                chkBoxRememberMe.setVisibility(view.VISIBLE);
                parentDbName = "Users";
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(FarmerLogin.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);

            }
        });

    }


    private void loginUser() {
        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Phone number is Empty", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "password is Empty", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserpasswordKey, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(FarmerLogin.this, "Welcome Admin, You are Loged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(FarmerLogin.this, AdminHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                            {
//                                Toast.makeText(FarmerLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(FarmerLogin.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);

                            }
                        }
                        else{
                            Toast.makeText(FarmerLogin.this, "password does not match with the phone number", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                }
                else{
                    Toast.makeText(FarmerLogin.this, "Account with this " + phone + " do not exits", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

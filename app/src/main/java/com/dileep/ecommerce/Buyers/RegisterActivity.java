package com.dileep.ecommerce.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dileep.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private EditText InputName, Phoneno, InputPassword, InputRepeatPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button) findViewById(R.id.register_signin_btn);
        InputName = (EditText) findViewById(R.id.register_name_input);
        Phoneno = (EditText) findViewById(R.id.register_phone_no_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputRepeatPassword = (EditText) findViewById(R.id.register_password_repeat_input);
        loadingBar = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }
    private void CreateAccount()
    {
        String MobilePattern = "[0-9]{10}";
        String name = InputName.getText().toString();
        String phone = Phoneno.getText().toString();
        String password = InputPassword.getText().toString();
        String repeatpassword = InputRepeatPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Phone is Empty", Toast.LENGTH_SHORT).show();
        }
        else if(phone.length() != 10)
        {
            Toast.makeText(this, "Enter a correct mobile number", Toast.LENGTH_SHORT).show();
        }
        else if(!Phoneno.getText().toString().matches(MobilePattern))
        {
            Toast.makeText(this, "Enter a correct mobile number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "password is Empty", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(repeatpassword))
        {
            Toast.makeText(this, "Repeat Password is Empty", Toast.LENGTH_SHORT).show();
        }
        else if(!TextUtils.equals(password, repeatpassword))
        {
            Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() <= 6)
        {
            Toast.makeText(this, "password should contain at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Validatephonenumber(name, phone, password);
        }
    }

    private void Validatephonenumber(final String name, final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "congratulations, your account has been created", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(RegisterActivity.this, FarmerLogin.class);
                                startActivity(intent);
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Error: Please Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "this " + phone + " Number already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Login with the mobile number", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, FarmerLogin.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

package com.example.win8.nokia.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.win8.nokia.Api_interfaces.Login_Api;
import com.example.win8.nokia.Pojo.LoginPojo;
import com.example.win8.nokia.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    /* Declaring Required Variables*/
    EditText userName, passWord;
    Button login_Button;
    String IMEI_Number_holder;
    TelephonyManager telephonyManager;
    Login_Api login_api;
    LoginPojo loginPojo, data;
    // TextView iemi_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        /* Initializing required Variables*/
        //iemi_number = findViewById(R.id.tv_textview);
        userName = findViewById(R.id.et_userName);
        passWord = findViewById(R.id.et_password);
        login_Button = findViewById(R.id.bt_login);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        IMEI_Number_holder = telephonyManager.getDeviceId();
        // iemi_number.setText(IMEI_Number_holder);
        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit_Calling();
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
            }

        });
    }

    /* checking Login credentials through this method*/
    private void Retrofit_Calling() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Login_Api.Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        login_api = retrofit.create(Login_Api.class);
        loginPojo = new LoginPojo(userName.getText().toString(), passWord.getText().toString(), IMEI_Number_holder);

        Call<LoginPojo> call = login_api.insertdata(loginPojo);
        call.enqueue(new Callback<LoginPojo>() {
            @Override
            public void onResponse(Call<LoginPojo> call, Response<LoginPojo> response) {
                if(response.isSuccessful()) {

                    data = response.body();
                    Toast.makeText(LoginActivity.this, "details" + data, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "UserName:" + data.getUsername(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "Password:" + data.getPassword(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "IMEI number:" + data.getDevice_IMEI(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginPojo> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

}


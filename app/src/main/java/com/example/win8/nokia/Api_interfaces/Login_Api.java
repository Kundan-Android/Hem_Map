package com.example.win8.nokia.Api_interfaces;

import com.example.win8.nokia.Pojo.LoginPojo;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by win8 on 9/4/2018.
 */

public interface Login_Api {
    String Base_Url ="http://787e03ed.ngrok.io/";

    @POST("driver_login")
    retrofit2.Call<LoginPojo> insertdata(@Body LoginPojo loginPojo);
}

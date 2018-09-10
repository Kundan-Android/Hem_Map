package com.example.win8.nokia.Api_interfaces;

import com.example.win8.nokia.Pojo.CurrentLocation_pojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Hemanth Kumar on 9/2/2018.
 */

public interface Pickup_Api {
    String Base_Url = "http://hmkcode.appspot.com/";

    @POST("jsonservlet/")
    Call<CurrentLocation_pojo> insertdata(@Body CurrentLocation_pojo example);

}

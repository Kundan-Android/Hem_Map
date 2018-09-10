package com.example.win8.nokia.Pojo;

/**
 * Created by win8 on 9/4/2018.
 */

public class LoginPojo {
    String username,password,device_IMEI;

    public LoginPojo(String username, String password, String device_IMEI) {
        this.username = username;
        this.password = password;
        this.device_IMEI = device_IMEI;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice_IMEI() {
        return device_IMEI;
    }

    public void setDevice_IMEI(String device_IMEI) {
        this.device_IMEI = device_IMEI;
    }
}

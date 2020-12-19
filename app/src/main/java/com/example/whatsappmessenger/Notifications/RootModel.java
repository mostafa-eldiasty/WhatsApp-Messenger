package com.example.whatsappmessenger.Notifications;

import com.google.gson.annotations.SerializedName;

public class RootModel {

    @SerializedName("to")
    private String token;

    public RootModel(String token) {
        this.token = token;
    }
}
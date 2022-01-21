package com.example.whatsappmessenger.api;

import com.example.whatsappmessenger.api.models.LoginModel;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("login")
    Call<LoginModel> verify(@Body JsonObject verifcationDetails);
}

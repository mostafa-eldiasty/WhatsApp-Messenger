package com.example.whatsappmessenger.Notifications;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers({"Authorization: key=AAAAlvo_5VQ:APA91bHi-S6Qa5eDG8SxAHGEcebXDpQIULCJ4gsBNKvL5MmTY2A8DK4My35ixQH0BKawRSO5LQAItu09vP6AMjqu67EVTOCTts9Jjs_myFPqoUzvfyTGQ52IfGT-rQE79AygEItjNTLk"
            , "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);
}
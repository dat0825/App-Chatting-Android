package com.example.datvtd.chatting.Fragment;

import com.example.datvtd.chatting.Notifications.MyResponse;
import com.example.datvtd.chatting.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAHCtZhyk:APA91bFT9di46P01WUzp7weaGCGw_oU25FXGoHXjuIHrHsuqnB3EBanxTTKYIpfxVHG9vrxG1Xh1RluPAdZt8giu8HN24di4ZYeEUjc2wVwEmWFHPThD8mZLui2NM_qb8A6Fse3U8tjO"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

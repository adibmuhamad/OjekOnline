package com.online.ojek.ojekonline.remote;

import com.online.ojek.ojekonline.Model.FCMResponse;
import com.online.ojek.ojekonline.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by adib on 05/05/18.
 */

public interface IFCMService {
    @Headers({
            "Content-Type: application/jason",
            "Authorization:key=AAAA2QLtqn4:APA91bFj3L2i73Lrn_PVYeuI6X89YQjO921wp7V6vvqkIOd2yfcUWpD95EgRJqQNjkVeNayYU5fktsKUmfd36YzOTH2RWgxAQe4BsX-NXW7skO0d871sr9hXIh6_h_XiUOOmeRmDHb9Q"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}

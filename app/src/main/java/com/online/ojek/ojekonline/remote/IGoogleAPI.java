package com.online.ojek.ojekonline.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by adib on 16/12/17.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);

}

package com.online.ojek.ojekonline.Common;

import com.online.ojek.ojekonline.remote.IGoogleAPI;
import com.online.ojek.ojekonline.remote.RetrofitClient;

/**
 * Created by adib on 16/12/17.
 */

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}

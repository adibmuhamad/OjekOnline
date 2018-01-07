package com.online.ojek.ojekonline.Common;

import com.online.ojek.ojekonline.remote.IGoogleAPI;
import com.online.ojek.ojekonline.remote.RetrofitClient;

/**
 * Created by adib on 16/12/17.
 */

public class Common {

    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String pickup_request_tbl = "PickupRequest";

    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}

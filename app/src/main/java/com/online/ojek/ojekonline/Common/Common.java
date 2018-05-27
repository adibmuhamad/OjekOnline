package com.online.ojek.ojekonline.Common;

import android.location.Location;

import com.online.ojek.ojekonline.Model.Driver;
import com.online.ojek.ojekonline.remote.FCMClient;
import com.online.ojek.ojekonline.remote.GoogleMapAPI;
import com.online.ojek.ojekonline.remote.IFCMService;
import com.online.ojek.ojekonline.remote.IGoogleAPI;
import com.online.ojek.ojekonline.remote.RetrofitClient;

/**
 * Created by adib on 16/12/17.
 */

public class Common {

    public static boolean isDriverFound = false;
    public static String driverId="";

    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl = "Tokens";
    public static final String rating_tbl = "RatingDetails";
    public static final String user_field = "usr";
    public static final String password_field = "pwd";
    public static final String rider_user_field = "usr";
    public static final String rider_password_field = "pwd";
    public static final int PICK_IMAGE_REQUEST = 9999;

    public static Driver currentUser;

    public static double base_fare = 1000;
    public static double time_rate = 200;
    public static double distance_rate = 500;

    public static double getPrice(double km,int min){
        return (base_fare+(time_rate*min)+(distance_rate*km));
    }

    public static Location mLastLocation = null;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";
    public static final String googleAPIUrl = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public static IGoogleAPI getGoogleService(){
        return GoogleMapAPI.getClient(googleAPIUrl).create(IGoogleAPI.class);
    }
}

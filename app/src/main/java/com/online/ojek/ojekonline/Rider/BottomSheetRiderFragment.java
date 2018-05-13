package com.online.ojek.ojekonline.Rider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextRecognizer;
import com.online.ojek.ojekonline.Common.Common;
import com.online.ojek.ojekonline.R;
import com.online.ojek.ojekonline.remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adib on 06/01/18.
 */

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {
    String mLocation,mDestination;
    IGoogleAPI mService;
    TextView txtCalculation;

    public static BottomSheetRiderFragment newInstance(String location, String destination){
        BottomSheetRiderFragment f = new BottomSheetRiderFragment();
        Bundle args = new Bundle();
        args.putString("location",location);
        args.putString("destination",destination);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation =  getArguments().getString("location");
        mDestination =  getArguments().getString("destination");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_rider, container,false);
        TextView txtLocation = (TextView)view.findViewById(R.id.txtLocation);
        TextView txtDestination = (TextView)view.findViewById(R.id.txtDestination);
        txtCalculation = (TextView)view.findViewById(R.id.txtCalculation);
        mService = Common.getGoogleService();
        getPrice(mLocation,mDestination);

        txtLocation.setText(mLocation);
        txtDestination.setText(mDestination);

        return view;
    }

    private void getPrice(String mLocation, String mDestination) {
        String requestUrl = null;
        String loc = mLocation.replaceAll("\\s+","");
        String des = mDestination.replaceAll("\\s+","");
        try{
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"
                    +"transit_routing_preference=less_driving&"
                    +"origin="+loc+"&"
                    +"destination="+des+"&"
                    +"key="+getResources().getString(R.string.google_browser_key);
            Log.e("LINK",requestUrl);
            mService.getPath(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distance = legsObject.getJSONObject("distance");
                        String distance_text = distance.getString("text");
                        Double distance_value = Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+",""));

                        JSONObject time = legsObject.getJSONObject("duration");
                        String time_text = time.getString("text");
                        Integer time_value = Integer.parseInt(time_text.replaceAll("\\D+",""));

                        String final_calculation = String.format(Locale.getDefault(),"%s + %s = Rp %.2f",distance_text,time_text,
                                                    Common.getPrice(distance_value,time_value));

                        txtCalculation.setText(final_calculation);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR",t.getMessage());
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

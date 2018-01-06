package com.online.ojek.ojekonline.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.online.ojek.ojekonline.R;

/**
 * Created by adib on 06/01/18.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomInfoWindow(Context context){
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPickUpTitle = (TextView)myView.findViewById(R.id.txtPickUpInfo);
        txtPickUpTitle.setText(marker.getTitle());

        TextView txtPickUpSnippet = (TextView)myView.findViewById(R.id.txtPickUpSnippet);
        txtPickUpSnippet.setText(marker.getSnippet());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

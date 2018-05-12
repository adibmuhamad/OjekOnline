package com.online.ojek.ojekonline.Rider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.text.TextRecognizer;
import com.online.ojek.ojekonline.R;

/**
 * Created by adib on 06/01/18.
 */

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {
    String mLocation,mDestination;

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
        TextView txtCalculation = (TextView)view.findViewById(R.id.txtCalculation);

        txtLocation.setText(mLocation);
        txtDestination.setText(mDestination);

        return view;
    }
}

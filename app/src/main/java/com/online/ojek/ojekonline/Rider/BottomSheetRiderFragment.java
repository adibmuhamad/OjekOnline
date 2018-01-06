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
    String mTag;
    public static BottomSheetRiderFragment newInstance(String tag){
        BottomSheetRiderFragment f = new BottomSheetRiderFragment();
        Bundle args = new Bundle();
        args.putString("TAG",tag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag =  getArguments().getString("TAG");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_rider, container,false);
        //TextView = ....
        return view;
    }
}

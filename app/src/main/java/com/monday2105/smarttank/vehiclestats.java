package com.monday2105.smarttank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;


public class vehiclestats extends Fragment {

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        TableLayout vehicleTable = (TableLayout) getActivity().findViewById(R.id.dispvehicle);
        return inflater.inflate(R.layout.vehiclestats, container, false);
    }
}
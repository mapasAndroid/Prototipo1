package com.example.cristhian.prototipo2;

/**
 * Created by MAIS on 17/05/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Restaurante extends Fragment{


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lay_restau,container,false);
        return rootView;
    }
}

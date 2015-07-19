package com.example.cristhian.prototipo2;

/**
 * Created by MAIS on 17/05/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class Restaurante extends Fragment implements View.OnClickListener {
    ImageButton boton10, boton9, boton8, boton7, boton6, boton5, boton4, boton3, boton2, boton1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lay_restau, container, false);
        boton10 = (ImageButton) rootView.findViewById(R.id.i10);
        boton10.setOnClickListener(this);
        boton9 = (ImageButton) rootView.findViewById(R.id.i9);
        boton9.setOnClickListener(this);
        boton8 = (ImageButton) rootView.findViewById(R.id.i8);
        boton8.setOnClickListener(this);
        boton7 = (ImageButton) rootView.findViewById(R.id.i7);
        boton7.setOnClickListener(this);
        boton6 = (ImageButton) rootView.findViewById(R.id.i6);
        boton6.setOnClickListener(this);
        boton5 = (ImageButton) rootView.findViewById(R.id.i5);
        boton5.setOnClickListener(this);
        boton4 = (ImageButton) rootView.findViewById(R.id.i4);
        boton4.setOnClickListener(this);
        boton3 = (ImageButton) rootView.findViewById(R.id.i3);
        boton3.setOnClickListener(this);
        boton2 = (ImageButton) rootView.findViewById(R.id.i2);
        boton2.setOnClickListener(this);
        boton1 = (ImageButton) rootView.findViewById(R.id.i1);
        boton1.setOnClickListener(this);
        return rootView;

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), v.getId() + "HOLAA", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }
}
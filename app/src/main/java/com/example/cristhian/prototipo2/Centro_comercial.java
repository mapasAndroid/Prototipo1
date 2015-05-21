package com.example.cristhian.prototipo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by MAIS on 17/05/2015.
 */


public class Centro_comercial extends Fragment implements View.OnClickListener {


    ImageButton boton10;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lay_centro,container,false);

        boton10 = (ImageButton)rootView.findViewById(R.id.i10);
        boton10.setOnClickListener(this);
        return rootView;

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), v.getId()+"", Toast.LENGTH_LONG).show();
    }
}
package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MAIS on 13/07/15.
 */
public class Banco extends Fragment {

    String paraderos;
    BaseDeDatos baseDeDatos;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lay_banco, container, false);

        this.baseDeDatos = new BaseDeDatos(rootView.getContext());
        this.baseDeDatos.abrir();
        this.paraderos = this.baseDeDatos.getParaderos("B");
        this.baseDeDatos.cerrar();

        //malla_layout_banco
        //GridLayout malla = (GridLayout)rootView.findViewById(R.id.malla_layout_banco);
        //ImageButton boton = new ImageButton(rootView.getContext());
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(150, 150);
        //boton.setLayoutParams(params);
        //boton.setBackground(new ColorDrawable(Color.parseColor("#3F51B5")));
        //malla.addView(boton);

        return rootView;

    }
}

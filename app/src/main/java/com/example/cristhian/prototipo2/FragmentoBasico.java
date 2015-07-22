package com.example.cristhian.prototipo2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class FragmentoBasico extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View rootView;
    BaseDeDatos baseDeDatos;
    private AdapterCardView myAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;


    public static FragmentoBasico newInstance(String param1, String param2) {
        return null;
    }

    public FragmentoBasico() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public FragmentoBasico(String tipoV) {
        Bundle args = new Bundle();
        args.putString("tipoV", tipoV);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //tipoDeVista = getArguments().getString(tipoVista);
            //mParam2 = getArguments().getString(argumentoAdicional);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fragmento_basico, container, false);

        //set rootView como mi atributo
        this.rootView = rootView;

        //recycler view para la lista dinamica
        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //solicitar los paraderos de tipo banco y los inserta en la lista dinamica
        this.setParaderosEnLista();

        //elemento swiperefresh para actualizar
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        return rootView;

    }

    public void setParaderosEnLista() {

        this.baseDeDatos = new BaseDeDatos(this.rootView.getContext());
        this.baseDeDatos.abrir();
        String tv = "";
        if (getArguments() != null) {
            tv = getArguments().getString("tipoV");

        }
        String[] paraderos = this.baseDeDatos.getParaderos(tv);
        this.baseDeDatos.cerrar();
        String r = "";
        for (String para : paraderos) {
            r += para + ":::";
        }
        //elementos de cada elemento que la lsita dinamica va a tener
        ItemParaderos itemsData[] = new ItemParaderos[paraderos.length];
        for (int i = 0; i < paraderos.length; i++) {
            itemsData[i] = new ItemParaderos(paraderos[i]);
        }
        //le pone el adapter personalizado a el recycle view
        this.myAdapter = new AdapterCardView(itemsData);
        this.recyclerView.setAdapter(this.myAdapter);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * metodo que refresca la lista de paraderos
     */
    @Override
    public void onRefresh() {
        new Copia().copiarDatos(
                this.rootView.getContext(),
                ((Lugares) getActivity()).getNombreUsuario()
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setParaderosEnLista();
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FragmentoBasico.this.getActivity().getBaseContext(), "Actualizado", Toast.LENGTH_SHORT).show();
            }
        }, 7000);
    }


}

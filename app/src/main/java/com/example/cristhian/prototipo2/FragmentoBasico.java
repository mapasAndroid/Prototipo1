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
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //elemento swiperefresh para actualizar
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) this.rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        this.mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);

        //solicitar los paraderos de tipo banco y los inserta en la lista dinamica
        boolean hayParaderos = this.setParaderosEnLista();
        if(! hayParaderos){
            String tv = "";
            if (getArguments() != null) {
                tv = getArguments().getString("tipoV");
            }
            if(tv.equals("LR")){
                this.rootView = inflater.inflate(R.layout.fragment_fragmento_vacio_recientes, container, false);
            }else{
                this.rootView = inflater.inflate(R.layout.fragment_fragmento_vacio_basico, container, false);
            }

        }


        return this.rootView;

    }

    public boolean setParaderosEnLista() {

        String tv = "";
        if (getArguments() != null) {
            tv = getArguments().getString("tipoV");

        }

        this.baseDeDatos = new BaseDeDatos(this.rootView.getContext());
        this.baseDeDatos.abrir();
        String[] paraderos = this.baseDeDatos.getParaderos(tv);
        this.baseDeDatos.cerrar();

        if (paraderos != null) {
            //elementos de cada elemento que la lista dinamica va a tener
            ItemParaderos itemsData[] = new ItemParaderos[paraderos.length];
            for (int i = 0; i < paraderos.length; i++) {
                itemsData[i] = new ItemParaderos(paraderos[i]);
            }
            //le pone el adapter personalizado a el recycle view
            this.myAdapter = new AdapterCardView(itemsData);
            this.recyclerView.setAdapter(this.myAdapter);
            this.recyclerView.setItemAnimator(new DefaultItemAnimator());
            return true;
        } else {
            return false;
        }

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

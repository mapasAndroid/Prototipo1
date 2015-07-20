package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by MAIS on 13/07/15.
 */
public class Banco extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    BaseDeDatos baseDeDatos;
    private AdapterCardView myAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private View rootView;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //infla en contenido en el fragmento lay_banco
        View rootView = inflater.inflate(R.layout.lay_banco, container, false);

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

    private void setParaderosEnLista() {

        this.baseDeDatos = new BaseDeDatos(this.rootView.getContext());
        this.baseDeDatos.abrir();
        String [] paraderos = this.baseDeDatos.getParaderos("B");
        this.baseDeDatos.cerrar();

        //elementos de cada elemento que la lsita dinamica va a tener
        ItemParaderos itemsData[] = new ItemParaderos[paraderos.length];
        for(int i = 0; i< paraderos.length ; i++) {
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
                Toast.makeText(Banco.this.getActivity().getBaseContext(), "Actualizado", Toast.LENGTH_LONG).show();
            }
        }, 7000);
    }
}

                    /*
                    =========================================
                    CLASE PARA MANEJAR EL ADAPTER DE LA LISTA
                    =========================================
                     */

class AdapterCardView extends RecyclerView.Adapter<AdapterCardView.ViewHolder> {
    private ItemParaderos[] itemsData;

    public AdapterCardView(ItemParaderos[] itemsData) {
        this.itemsData = itemsData;
    }

    // crear nuevas vistas (invocado por el layout manager)
    @Override
    public AdapterCardView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // crear una nueva vista
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_card_view, parent, false);

        // crear ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // reemplaza el contenido de la vista (invocado por el layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //coloca el nombre en cada elemento de la lista
        viewHolder.txtViewTitle.setText(itemsData[position].getNombreParadero());
        viewHolder.txtViewLatLong.setText(itemsData[position].getLatLong());
        if(itemsData[position].esReciente()){
            viewHolder.imageViewFav.setBackgroundResource(R.drawable.ic_action_star_10_yellow);
        }

    }

    //calse embebida para referenciar a cada elemento de recycler
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView txtViewLatLong;
        public ImageView imageViewFav;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.nombreParadero);
            txtViewLatLong = (TextView) itemLayoutView.findViewById(R.id.latLonParadero);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}

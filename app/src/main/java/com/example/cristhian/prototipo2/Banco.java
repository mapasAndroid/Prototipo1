package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by MAIS on 13/07/15.
 */
public class Banco extends Fragment {

    String paraderos[];
    BaseDeDatos baseDeDatos;
    private ListView listView;
    private AdapterCardView myAdapter;

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

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));


        ItemData itemsData[] = { new ItemData("Help",5),
                new ItemData("Delete",5),
                new ItemData("Cloud",5),
                new ItemData("Favorite",5),
                new ItemData("Like",5),
                new ItemData("Rating",5)};

        this.myAdapter = new AdapterCardView(itemsData);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;

    }

    private String imprimirVector(String[] paraderos) {
        String res = "";
        for (String paradero : paraderos) {
            res += paradero + ":::";
        }
        return res;
    }

    public String[] getParaderos() {
        return this.paraderos;
    }
}

class AdapterCardView extends RecyclerView.Adapter<AdapterCardView.ViewHolder> {
    private ItemData[] itemsData;

    public AdapterCardView(ItemData[] itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterCardView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_card_view, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.txtViewTitle.setText(itemsData[position].getTitle());
        //viewHolder.imgViewIcon.setImageResource(itemsData[position].getImageUrl());


    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.person_name);
            //imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}

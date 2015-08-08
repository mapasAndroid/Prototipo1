package com.example.cristhian.prototipo2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by CRISTHIAN and MAITE
 */
public class AdapterCardView extends RecyclerView.Adapter<AdapterCardView.ViewHolder> {

    private ItemParaderos[] itemsData;


    public AdapterCardView(ItemParaderos[] itemsData) {
        this.itemsData = itemsData;
    }

    // crear nuevas vistas (invocado por el layout manager)
    @Override
    public AdapterCardView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {


        // crear una nueva vista
        View itemLayoutView = itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragmento_fila_card_view, parent, false);

        // crear ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // reemplaza el contenido de la vista (invocado por el layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //coloca el nombre en cada elemento de la lista
        viewHolder.txtViewTitle.setText(itemsData[position].getNombreParadero());
        viewHolder.txtViewLatLong.setText(itemsData[position].getDireccion());
        viewHolder.texto_oculto.setText(itemsData[position].getIdParadero());
        if(itemsData[position].esReciente()){
            viewHolder.imageViewFav.setImageResource(R.drawable.ic_action_star_10_yellow);
        }else{
            viewHolder.imageViewFav.setImageResource(R.drawable.ic_star_black_24dp);
        }

    }

    //calse embebida para referenciar a cada elemento de recycler
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView txtViewLatLong;
        public ImageView imageViewFav;
        public TextView texto_oculto;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.nombreParadero);
            txtViewLatLong = (TextView) itemLayoutView.findViewById(R.id.latLonParadero);
            texto_oculto = (TextView) itemLayoutView.findViewById(R.id.id_oculto);
            imageViewFav = (ImageView) itemLayoutView.findViewById(R.id.boton_favoritos);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}

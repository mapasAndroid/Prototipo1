package com.example.cristhian.prototipo2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentoBasico extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View rootView;
    BaseDeDatos baseDeDatos;
    private AdapterCardView myAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    FragmentActivity myContext;
    ViewGroup container;
    LayoutInflater inflater;


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

        this.inflater = inflater;
        this.container = container;
        final View rootView = inflater.inflate(R.layout.fragment_fragmento_basico, container, false);

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

        if (!hayParaderos) {
            String tv = "";
            if (getArguments() != null) {
                tv = getArguments().getString("tipoV");
            }

            if (tv.equals("LR")) {
                this.rootView = inflater.inflate(R.layout.fragment_fragmento_vacio_recientes, container, false);
                ((TextView) this.rootView.findViewById(R.id.texto_vacio_recientes))
                        .setTypeface(Typeface.createFromAsset(
                                        getActivity().getAssets(), "fonts/RobotoCondensed-Light.ttf")
                        );
            } else {
                this.rootView = inflater.inflate(R.layout.fragment_fragmento_vacio_basico, container, false);
                ((TextView) this.rootView.findViewById(R.id.texto_vacio_basico))
                        .setTypeface(Typeface.createFromAsset(
                                        getActivity().getAssets(), "fonts/RobotoCondensed-Light.ttf")
                        );
            }


        }

        colocarEventoClick();
        colocarEventoBorrado();

        return this.rootView;

    }


    /**
     * metodo que hace el evento del click de cada terjeta en la vista
     */
    private void colocarEventoClick() {
        this.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.rootView.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        LinearLayout linearLayout = (LinearLayout) view;

                        CardView cardView = (CardView) linearLayout.getChildAt(0);

                        LinearLayout hijoCardView = (LinearLayout) cardView.getChildAt(0);

                        LinearLayout hijodeLinear = (LinearLayout) hijoCardView.getChildAt(0);

                        TextView texto = (TextView) hijodeLinear.getChildAt(2);

                        String id = texto.getText().toString();

                        String datosParadero = ((Lugares) getActivity()).agregarARecientes(id);

                        recyclerView.getAdapter().notifyDataSetChanged();

                        setParaderosEnLista();

                        Intent intent = new Intent(getActivity(), Mapa.class);

                        //datosParadero es un String con todos los datos del paradero separados por &
                        //
                        intent.putExtra("datosParadero", datosParadero);
                        //datosUsuario es un VECTOR con los datos, usuario, nombre, correo, password
                        intent.putExtra("datosUsuario", ((Lugares) getActivity()).getDatosUsuario());

                        //ubicacion actual dada por lugares

                        String ubicacionActual = ((Lugares) (getActivity())).getUbicacionActual();
                        Log.i("pruebas", "ubica_ac = " + ubicacionActual);
                        if (ubicacionActual.isEmpty()) {
                            intent.putExtra("ubicacionActual", "none");
                        } else {
                            intent.putExtra("ubicacionActual", ubicacionActual);
                        }

                        startActivity(intent);

                    }
                })
        );
    }

    /**
     * metodo que permite ejecutar el borrado de los lugares recientes de las tarjetas
     */
    private void colocarEventoBorrado() {
        String tv = "";
        if (getArguments() != null) {
            tv = getArguments().getString("tipoV");

        }
        if (tv.equals("LR")) {
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    int position = viewHolder.getAdapterPosition();

                    viewHolder.getLayoutPosition();

                    LinearLayout linearLayout = (LinearLayout) recyclerView.getChildAt(position);

                    CardView cardView = (CardView) linearLayout.getChildAt(0);

                    LinearLayout hijoCardView = (LinearLayout) cardView.getChildAt(0);

                    LinearLayout hijodeLinear = (LinearLayout) hijoCardView.getChildAt(0);

                    TextView texto = (TextView) hijodeLinear.getChildAt(2);

                    String id = texto.getText().toString();

                    baseDeDatos.abrir();
                    baseDeDatos.eliminarReciente(id);
                    baseDeDatos.cerrar();

                    if (recyclerView.getAdapter().getItemCount() == 1) {

                        recyclerView = null;
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                        recyclerView.setAdapter(null);

                    } else {
                        recyclerView.getAdapter().notifyItemRemoved(position);
                    }

                    boolean x = setParaderosEnLista();

                    if (!x) {
                        rootView = inflater.inflate(R.layout.fragment_fragmento_vacio_recientes, container, false);
                    }

                    Toast.makeText(rootView.getContext(), "Favorito eliminado...", Toast.LENGTH_SHORT).show();


                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

        }
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
            this.myAdapter = new AdapterCardView(itemsData, rootView.getContext());
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
                Toast.makeText(FragmentoBasico.this.getActivity().getBaseContext(), "Informacion actualizada...", Toast.LENGTH_SHORT).show();
            }
        }, 7000);
    }

    @Override
    public void onAttach(Activity activity) {
        this.myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}

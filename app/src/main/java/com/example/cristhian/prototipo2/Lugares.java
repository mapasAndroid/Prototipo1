package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;


public class Lugares extends ActionBarActivity {

    private String[] alista;
    private DrawerLayout drawerLayout;
    private ListView lista;
    private RecyclerView mRecycler;
    RecyclerView.LayoutManager mLayoutManager;
    private EditText editable;
    private ActionBarDrawerToggle drawerToggle;
    AsistenteMensajes asistenteMensajes = new AsistenteMensajes();

    private CharSequence tituloSec;

    private CharSequence tituloApp;

    private String recientes;

    private String nombreUsuario;

    private String correoUsuario;

    private MyAdapter myAdapter;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitios);

        //pintar el action bar de color indigo
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));


        //verificar si hay conexion a internet para notificarle al usuario
        if (!verificaConexion(this.getBaseContext())) {
            asistenteMensajes.imprimir(this.getFragmentManager(),
                    "No tienes conexion a internet, Stopbus trabajara con los datos locales. " +
                            "Conectate a internet lo mas rapido posible.", 3);
        }

        //muestra el mensaje de bienvenida, siempre y cuando hayan extras en el intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mostrarMensaje(extras.get("usuario").toString(), extras.get("desde").toString());
            this.nombreUsuario = extras.get("usuario").toString();
            this.correoUsuario = extras.get("correo").toString();
        }

        //mostrar los lugares recientes en el principal
        //llenarListaRecientes();

        drawerLayout = (DrawerLayout) findViewById(R.id.contenedor_principal);

        int imagenes[] = {R.drawable.ic_action_clock, R.drawable.ic_action_bike,
                R.drawable.ic_action_book, R.drawable.ic_action_cart,
                R.drawable.ic_action_home, R.drawable.ic_action_creditcard,
                R.drawable.ic_action_dialer, R.drawable.ic_action_restaurant
        };

        String titulos[] = this.getResources().getStringArray(R.array.menu_izquierdo);


        //poner el adapter
        this.mRecycler = (RecyclerView) findViewById(R.id.menuizquierdo);
        this.mRecycler.setHasFixedSize(true);

        this.myAdapter = new MyAdapter(titulos, imagenes, this.getNombreUsuario(),
                this.getCorreoUsuario(), R.drawable.ic_action_user_white);


        this.mRecycler.setAdapter(this.myAdapter);

        mLayoutManager = new LinearLayoutManager(this);

        this.mRecycler.setLayoutManager(mLayoutManager);

        mRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String title[] = {""};
                        Fragment a = getFragmentByPos(position, title);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.contenedor_frame, a);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        setTitle(title[0]);
                        drawerLayout.closeDrawers();

                    }
                })
        );

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.abierto, R.string.cerrado) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ActivityCompat.invalidateOptionsMenu(Lugares.this);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActivityCompat.invalidateOptionsMenu(Lugares.this);
            }

        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    public Fragment getFragmentByPos(int pos, String title []){
        String instancia = "";
        switch (pos){
            case 1:
                instancia = "LR";
                title[0] = "Lugares Recientes";
                break;
            case 2:
                instancia = "P";
                title[0] = "Parques";
                break;
            case 3:
                instancia = "E";
                title[0] = "Educacion";
                break;
            case 4:
                instancia = "C";
                title[0] = "Compras";
                break;
            case 5:
                instancia = "H";
                title[0] = "Hoteles";
                break;
            case 6:
                instancia = "B";
                title[0] = "Bancos";
                break;
            case 7:
                instancia = "CA";
                title[0] = "Cajeros";
                break;
            case 8:
                instancia = "R";
                title[0] = "Restaurantes";
                break;
        }
        return new FragmentoBasico(instancia);
    }

    private void mostrarMensaje(String usuario, String desde) {
        String mensaje = "";
        switch (desde) {
            case "inicio":
                mensaje = "Stopbus te estaba esperando " + usuario;
                break;
            case "splash":
                mensaje = "Hola de nuevo " + usuario;
                break;
            case "registro":
                mensaje = "Stopbus te saluda " + usuario + "!!";
                break;
        }

        Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    private void llenarListaRecientes() {

        BaseDeDatos baseDeDatos = new BaseDeDatos(Lugares.this.getBaseContext());
        baseDeDatos.abrir();
        this.recientes = baseDeDatos.getRecientes();
        baseDeDatos.cerrar();

        //lista = (ListView) findViewById(R.id.lugaresRecientes);

        if (!recientes.isEmpty()) {
            String[] para = this.recientes.split(",");
            alista = new String[para.length];

            for (int i = 0; i < alista.length; i++) {
                alista[i] = para[i].split("&")[1];
            }
        } else {
            alista = new String[]{"No hay lugares recientes"};
        }

        final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alista);
        lista.setAdapter(aa);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();


        if (id == R.id.action_logout) {
            Toast.makeText(this.getBaseContext(), "Cerrando Sesion...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    borrarBd();
                    Intent inicio = new Intent(Lugares.this, Inicio.class);
                    startActivity(inicio);
                    Lugares.this.finish();
                }
            }, 1500);

        }
        if (id == R.id.action_refresh) {
            Toast.makeText(this.getBaseContext(), "Actualizando...", Toast.LENGTH_LONG).show();
            Copia copiaDatos = new Copia();
            copiaDatos.copiarDatos(getBaseContext(), this.nombreUsuario);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    llenarListaRecientes();
                }
            }, 5000);

        }
        return super.onOptionsItemSelected(item);
    }

    private void borrarBd() {
        File database = getApplicationContext().getDatabasePath("stopbus.db");
        Log.i("Database", database.getFreeSpace() + "");

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.i("Database", "no encontrada");
        } else {
            Log.i("Database", "encontrada");
            database.delete();
            Log.i("Database", "borrada");
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }

    public void maxtrans(View view) {
        Toast.makeText(this, "supuestamente lo agrego a favoritos, no lo hago, pero intento :P", Toast.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
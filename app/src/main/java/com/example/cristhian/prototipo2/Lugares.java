package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class Lugares extends ActionBarActivity{

    private String[] opciones, alista;
    private DrawerLayout drawerLayout;
    private ListView listView, lista;
    private EditText editable;
    private ActionBarDrawerToggle drawerToggle;
    AsistenteMensajes asistenteMensajes = new AsistenteMensajes();

    private CharSequence tituloSec;
    private CharSequence tituloApp;

    private String paraderos;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitios);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Toast.makeText(this.getBaseContext(), extras.get("usuarioMensaje").toString(), Toast.LENGTH_LONG).show();
        }

        if (!verificaConexion(this.getBaseContext())) {
            asistenteMensajes.imprimir(this.getFragmentManager(), "No tienes conexion a internet, Stopbus trabajara con los datos locales. Conectate a internet lo mas rapido posible.", 3);
        }

        BaseDeDatos baseDeDatos = new BaseDeDatos(Lugares.this.getBaseContext());
        baseDeDatos.abrir();
        this.paraderos = baseDeDatos.getParaderos();
        baseDeDatos.cerrar();

        llenarLista();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new Parque();
                        break;
                    case 1:
                        fragment = new Educacion();
                        break;
                    case 2:
                        fragment = new Centro_comercial();
                        break;
                    case 3:
                        fragment = new Hotel();
                        break;
                    case 4:
                        fragment = new Banco();
                        break;
                    case 5:
                        fragment = new Cajero();
                        break;
                    case 6:
                        fragment = new Restaurante();
                        break;
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.contenedor_frame, fragment)
                        .commit();

                listView.setItemChecked(position, true);
                tituloSec = opciones[position];
                getSupportActionBar().setTitle(tituloSec);
                drawerLayout.closeDrawer(listView);


            }
        });

        tituloSec = getTitle();
        tituloApp = getTitle();

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

    private void llenarLista() {

        lista = (ListView) findViewById(R.id.lugaresRecientes);

        if (!paraderos.isEmpty()) {
            String[] para = this.paraderos.split(",");
            alista = new String[para.length];

            for (int i = 0; i < alista.length; i++) {
                alista[i] = para[i].split("&")[1];
            }
        } else {
            alista = new String[]{"No hay paraderos"};
        }


        //alista = new String[]{"Parque Colon", "Davivienda", "River Plaza", "BBVA Av 6"};

        final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alista);
        lista.setAdapter(aa);

        opciones = new String[]{"Parque", "Educacion", "Centro comercial", "Hotel", "Banco", "Cajero", "Restaurante"};
        drawerLayout = (DrawerLayout) findViewById(R.id.contenedor_principal);
        listView = (ListView) findViewById(R.id.menuizquierdo);


        listView.setAdapter(new ArrayAdapter<String>(getSupportActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1, opciones));

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


        if (id == R.id.action_settings) {
            Toast.makeText(this.getBaseContext(), "settings", Toast.LENGTH_LONG).show();
        }
        if(id == R.id.action_refresh){
            Toast.makeText(this.getBaseContext(), "refresh", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
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

}

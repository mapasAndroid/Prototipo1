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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by CRISTHIAN and MAITE
 */
public class Lugares extends ActionBarActivity {

    //====================================
    //          ATRIBUTOS
    //====================================

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


    //====================================
    //          GETTER Y SETTER
    //====================================


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }


    //====================================
    //          METODOS
    //====================================


    /**
     * metodo que se ejecuta de primero al instanciar la actividad
     *
     * @param savedInstanceState
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitios);

        //pintar el action bar de color azul indigo
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));


        //verificar si hay conexion a internet para notificarle al usuario
        //que va a trabajar con datos posiblemente antiguos
        if (!verificaConexion(this.getBaseContext())) {
            asistenteMensajes.imprimir(this.getFragmentManager(),
                    "No tienes conexion a internet, Stopbus trabajara con los datos locales. " +
                            "Conectate a internet lo mas rapido posible.", 3, null);
        }

        //muestra el mensaje de bienvenida, siempre y cuando hayan extras en el intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mostrarMensaje(extras.get("usuario").toString(), extras.get("desde").toString());
            this.nombreUsuario = extras.get("usuario").toString();
            this.correoUsuario = extras.get("correo").toString();
        }

        //extraer el drawer layout de la vista
        drawerLayout = (DrawerLayout) findViewById(R.id.contenedor_principal);

        //guardar las imagenes que iran en el menu del drawer layout
        int imagenes[] = {R.drawable.ic_action_clock, R.drawable.ic_action_bike,
                R.drawable.ic_action_book, R.drawable.ic_action_cart,
                R.drawable.ic_action_home, R.drawable.ic_action_creditcard,
                R.drawable.ic_action_dialer, R.drawable.ic_action_restaurant
        };

        //guardar el texto que va a tener cada item del menu del drawer layout
        String titulos[] = this.getResources().getStringArray(R.array.menu_izquierdo);

        //poner el adapter
        this.mRecycler = (RecyclerView) findViewById(R.id.menuizquierdo);
        this.mRecycler.setHasFixedSize(true);

        //instanciar un nuevo adaprter y agregarlo al recycler view
        this.myAdapter = new MyAdapter(titulos, imagenes, this.getNombreUsuario(),
                this.getCorreoUsuario(), R.drawable.ic_action_user_white);
        this.mRecycler.setAdapter(this.myAdapter);

        //agregar el layout manager al recycler
        mLayoutManager = new LinearLayoutManager(this);
        this.mRecycler.setLayoutManager(mLayoutManager);

        //manejar el evento de click en cada elemento del menu del drawer
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

        //poner estado abierto y cerrado al drawer layout
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
        //escuchar la interfaz drawer
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //colocar el fragmento "recientes" como inicio de la aplicacion
        String title[] = {""};
        Fragment a = getFragmentByPos(1, title);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor_frame, a);
        transaction.addToBackStack(null);
        transaction.commit();
        setTitle(title[0]);

    }

    /**
     * metodo que isntancia un fragmento dependiendo de la posicion recibida y
     * adapta el titulo para el fragmento padre
     *
     * @param pos   posicion del item en el menu drawer
     * @param title vector con una sola posicion donde se guardara el titulo
     * @return el fragmento adecuado
     */
    public Fragment getFragmentByPos(int pos, String title[]) {
        String instancia = "";
        switch (pos) {
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

    /**
     * metodo que muestra un mensaje d ebienvenida al usuario dependiendo de la
     * actividad de donde venga
     *
     * @param usuario el nombre del usuario a mostrar
     * @param desde   la actividad de donde viene
     */
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * metodo que maneja los botones de la barra de accion
     *
     * @param item
     * @return
     */
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

                }
            }, 5000);

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * metodo que elimina el aarchivo de la base de datos a la hora de cerrar sesion
     */
    private void borrarBd() {
        File database = getApplicationContext().getDatabasePath("stopbus.db");

        if (database.exists()) {
            database.delete();
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

    /**
     * metodo que verifica si existe una conexion a internet
     *
     * @param ctx el contexto donde es llamado el metodo
     * @return true si hay, false si no
     */
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

    /**
     * metodo que permite salir de la aplicacion cuando el usuario presiona el boton ATRAS
     * y no generar error con la pila de sucesos
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public String agregarARecientes(String id){
        String datos;
        BaseDeDatos baseDeDatos = new BaseDeDatos(this.getBaseContext());
        baseDeDatos.abrir();
        datos = baseDeDatos.getParaderoPorId(id);
        baseDeDatos.agregarReciente(datos);
        baseDeDatos.cerrar();
        return datos;
    }
}
package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Mapa extends ActionBarActivity {

    private GoogleMap mMap;
    private ActionBarDrawerToggle drawerToggle;
    private Lugares cx = new Lugares();


    private String[] datosParadero;

    //usuario, nombre, correo , password
    private String[] datosUsuario;

    private BaseDeDatos baseDeDatos;
    private final double MARGEN_DE_ERROR = 753.9047315860873;
    private final double MARGEN_DE_ERROR_LUGAR = 700.9047315860873;
    public static final LatLng CAMARA = new LatLng(7.885067, -72.500351);

    //Atributo que gestiona las conexiones a datos web
    private WebHelper webHelper = new WebHelper();

    protected ProgressDialog progres;

    //Atributo que pinta los mensajes en la pantalla
    private AsistenteMensajes asistente = new AsistenteMensajes();

    //datos globales para usar desde el Async Task
    LatLng ubicacionActual;
    LatLng ubicacionParadero;
    String id_ruta_string;

    ArrayList<Marker> busesMarkers = new ArrayList<>();
    ArrayList<String> busesDatos = new ArrayList<>();

    //en la pos 0 placa, 1 conductor, 2 nombre_ruta,
    // 3 tiempo estimado de demora, 4 posicion bus
    String[] datosAmostrar;

    //markes actual, paradero, bus APB
    Marker[] markersActual_paradero = {null, null};

    /*
    ====================================
                ONCREATE
    ====================================
     */


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapas);

        progres = new ProgressDialog(this);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

        this.baseDeDatos = new BaseDeDatos(getBaseContext());



        /*
        ====================================
                   LOCALIZACION
        ====================================
         */
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

       /*
        ====================================
                FIN DE LOCALIZACION
        ====================================
         */

        Bundle datosFragmento = getIntent().getExtras();
        if (datosFragmento != null) {
            datosParadero = datosFragmento.get("datosParadero").toString().split("&");
            datosUsuario = datosFragmento.getStringArray("datosUsuario");

            String temp = datosFragmento.getString("ubicacionActual");

            if (temp.equals("none")) {

                if(this.ubicacionActual == null){
                    finish();
                    return;
                }
            } else {
                Double v[] = {Double.parseDouble(temp.split("&")[0]), Double.parseDouble(temp.split("&")[1])};
                this.ubicacionActual = new LatLng(
                        v[0],
                        v[1]
                );
            }

        }

        setUpMapIfNeeded();

        this.ubicacionParadero = new LatLng(
                Double.parseDouble(this.datosParadero[3]),
                Double.parseDouble(this.datosParadero[4])
        );

        //agrego los markers en las posiciones a tratar, actual y la del paradero
        markersActual_paradero[0] = agregarMarker(this.ubicacionActual, R.drawable.person_marker, "Aqui estas", "estas cerca a tu paradero");

        markersActual_paradero[1] = agregarMarker(this.ubicacionParadero,
                R.drawable.parada_marker,
                this.datosParadero[1],
                this.datosParadero[2]
        );

        //muestro el snippet del marker actual para guiar al usuario
        markersActual_paradero[0].showInfoWindow();


        this.id_ruta_string = getRutaApropiada();

        if (this.id_ruta_string.equalsIgnoreCase("no_cercanas_actual")) {
            Toast.makeText(getBaseContext(), "Lo sentimos, pero no hay buses cerca a ti", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (this.id_ruta_string.equalsIgnoreCase("no_cercanas_paradero")) {
            Toast.makeText(getBaseContext(), "Lo sentimos, pero no hay buses que se acerquen a tu parada", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        //busco los waypoints de la ruta segun el id de la ruta
        this.baseDeDatos.abrir();
        ArrayList<LatLng> ruta = baseDeDatos.getWaypointsByRuta(this.id_ruta_string);
        this.baseDeDatos.cerrar();

        //pinto la ruta en color azul
        pintarRuta(ruta, new ColorDrawable(Color.parseColor("#3F51B5")).getColor());

        //animar la barra inferior de los comandos
        FrameLayout frame = (FrameLayout) findViewById(R.id.fab_container);
        Animation myAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        frame.startAnimation(myAnimation);

        BusuqedaDeBus buscarBus = new BusuqedaDeBus();
        buscarBus.execute(this.id_ruta_string);


    }//fin del metodo oncreate


    /**
     * metodo que pinta la ruta en el mapa
     *
     * @param puntos arraylist de todos los puntos a pintar
     * @param color  entero con el color a pintar la linea
     */
    public void pintarRuta(ArrayList<LatLng> puntos, int color) {
        PolylineOptions opciones = new PolylineOptions();
        opciones.addAll(puntos);
        opciones.color(color);
        opciones.width(4);
        this.mMap.addPolyline(opciones);
    }

    /**
     * metodo que busca la ruta mas apropiada para la posicion
     *
     * @return
     */
    private String getRutaApropiada() {

        this.baseDeDatos.abrir();
        String waypoints[] = this.baseDeDatos.getTodosWaypoints();
        this.baseDeDatos.cerrar();
        String b = calcularRuta(waypoints, this.ubicacionActual, this.ubicacionParadero);
        return b;
    }

    /**
     * calcula la ruta ideal
     *
     * @param waypoints
     * @param posicionActual
     * @param posicionParadero
     * @return
     */
    private String calcularRuta(String[] waypoints, LatLng posicionActual, LatLng posicionParadero) {

        //0 id ruta, 1 consecutivo, 2 lat, 3 long
        HashMap<String, String[]> cercanasPosActual = new HashMap<>();
        HashMap<String, String[]> cercanasParadero = new HashMap<>();

        for (int i = 0; i < waypoints.length; i++) {

            String[] waypointActual = waypoints[i].split("&");

            double dif1 = diferencia(
                    new LatLng(Double.parseDouble(waypointActual[2]), Double.parseDouble(waypointActual[3])),
                    posicionActual
            );
            if (dif1 <= this.MARGEN_DE_ERROR) {
                cercanasPosActual.put(waypointActual[0], waypointActual);
            }

            double dif2 = diferencia(
                    new LatLng(Double.parseDouble(waypointActual[2]), Double.parseDouble(waypointActual[3])),
                    posicionParadero
            );
            if (dif2 <= this.MARGEN_DE_ERROR_LUGAR) {
                cercanasParadero.put(waypointActual[0], waypointActual);
            }
        }//termina el ciclo

        if (cercanasParadero.isEmpty() || cercanasPosActual.isEmpty()) {
            return "no_cercanas_actual";
        }

        for (Object key : cercanasPosActual.keySet()) {
            if (cercanasParadero.containsKey(key)) {
                return key.toString();
            }
        }

        return "no_cercanas_paradero";
    }


    /**
     * metodo que calcula la diferencia entre dos coordenadas
     *
     * @param x
     * @param y
     * @return
     */
    private double diferencia(LatLng x, LatLng y) {
        double r = 6371000;
        double c = Math.PI / 180;
        return (2 * r * Math.asin(Math.sqrt(Math.pow(Math.sin(c * (y.latitude - x.latitude) / 2), 2) +
                Math.cos(c * x.latitude) * Math.cos(c * y.latitude) * Math.pow(Math.sin(c * (y.longitude - x.longitude) / 2), 2))));
    }

    /**
     * agregar un marker en el mapa
     *
     * @param posicion
     * @param imagen
     * @param titulo
     * @param snippet
     * @return
     */
    private Marker agregarMarker(LatLng posicion, int imagen, String titulo, String snippet) {

        MarkerOptions marker = new MarkerOptions().position(posicion)
                .title(titulo)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(imagen));

        if (mMap != null) {

            return mMap.addMarker(marker);
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            Toast.makeText(this.getBaseContext(), "Cerrando Sesion...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    borrarBd();
                    Intent inicio = new Intent(Mapa.this, Inicio.class);
                    startActivity(inicio);
                    Mapa.this.finish();
                    cx.cerrarlugar.finish();

                }
            }, 1000);

        }

        if (id == R.id.action_acerca_de) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(webHelper.getUrlAcercaDe()));
            startActivity(intent);
        }

        if (id == R.id.action_ayuda) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(webHelper.getUrlContacto()));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * borra el archivo de base de datos
     */
    private void borrarBd() {
        File database = getApplicationContext().getDatabasePath("stopbus.db");

        if (database.exists()) {
            database.delete();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CAMARA));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    /**
     * cuando se presiona el icono de ubicacion
     *
     * @param view
     */
    public void onPressPrimerIcono(View view) {

        // tiene 7 posiciones
        //en la pos 0 ubicacion actual , 1 buses en formato JSON

        if (datosAmostrar == null || VacioDatosMostrar()) {
            return;
        }

        Dialog dialogo = new Dialog(this);
        dialogo.setContentView(R.layout.fragme_mensaje_basico);
        TextView texto = (TextView) dialogo.findViewById(R.id.text_mensaje_basico);
        texto.setText(datosAmostrar[0]);
        dialogo.setTitle("Ubicación");
        dialogo.show();

    }

    /**
     * verifica que no haya nulos en los datos a mostrar
     *
     * @return
     */
    private boolean VacioDatosMostrar() {
        for (String dato : this.datosAmostrar) {
            if (dato.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * cuando se presiona el icono del bus
     *
     * @param view
     */
    public void onPressSegundoIcono(View view) {

        //en la pos 0 placa, 1 conductor, 2 nombre_ruta,
        // 3 tiempo estimado de demora, 4 posicion bus, 5 direccion bus, 6 direccion actual

        if (datosAmostrar == null || VacioDatosMostrar()) {
            return;
        }

        Dialog dialogo = new Dialog(this);
        dialogo.setContentView(R.layout.fragment_fragmento_mensaje);

        ListView lv = (ListView) dialogo.findViewById(R.id.lista_buses);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                busesDatos);

        lv.setAdapter(arrayAdapter);

        dialogo.setTitle("Buses");
        dialogo.show();
    }

    public void onPressTercerIcono(View view) {
        finish();
    }

    /**
     * metodo que pinta los buses basandose en la respuesta de los buses en formato JSON
     */
    public void pintarTodosBuses() {

        try {

            JSONObject rutas = new JSONObject(datosAmostrar[1]);
            for (int i = 0; i < rutas.length(); i++) {

                String placa = rutas.getJSONObject(i + "").getString("placa");
                String conductor = rutas.getJSONObject(i + "").getString("conductor");
                String pos_actual = rutas.getJSONObject(i + "").getString("pos_actual");
                agregarMarkerBus(placa, conductor, pos_actual);
                busesDatos.add("Placa: " + placa + "\nConductor: " + conductor);
            }

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }

    }

    /**
     * agrega un marker de un bus y guarda su referencia en una lista de de markers
     *
     * @param placa
     * @param conductor
     * @param pos_actual
     */
    private void agregarMarkerBus(String placa, String conductor, String pos_actual) {
        LatLng posicion = new LatLng(
                Double.parseDouble(pos_actual.split("&")[0]),
                Double.parseDouble(pos_actual.split("&")[1])
        );
        busesMarkers.add(agregarMarker(posicion, R.drawable.bus_marker, "Placa:" + placa, "Conductor: " + conductor));
    }


    //clase que maneja la busqueda del bus indicado en la web, "asycn task"

    public class BusuqedaDeBus extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progres.setMessage("Estamos buscando los buses... Danos unos segundos");
            progres.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progres.setIndeterminate(true);
            progres.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String respuesta) {

            //oculta la barra de progreso
            progres.hide();

            if (respuesta.isEmpty()) {
                //asistente.imprimir(getFragmentManager(), "no pudimos encontrar un bus cercano", 1);
                Toast.makeText(getBaseContext(), "Lo sentimos no encontramos un buses en la ruta", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (respuesta.equals("nonet")) {
                asistente.imprimir(getFragmentManager(), "no estras conectado a internet", 4);
                finish();
                return;
            }

            datosAmostrar = respuesta.split("/");

            //datos mostrar
            //pos 0 la direeccin de la posicion actual,
            markersActual_paradero[0].setSnippet(datosAmostrar[0]);
            markersActual_paradero[0].showInfoWindow();
            pintarTodosBuses();

        }

        protected String doInBackground(String... params) {
            return copiarDatosweb(params[0]);
        }

        public String copiarDatosweb(String id_ruta) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(webHelper.getUrl() + webHelper.getUrlBuscarBus());
            HttpResponse response = null;
            String resultado;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("usuario", webHelper.getUsuario()));
                params.add(new BasicNameValuePair("contrasenia", webHelper.getPass()));

                params.add(new BasicNameValuePair("id_ruta", id_ruta));
                params.add(new BasicNameValuePair("usuario_usuario", datosUsuario[0]));
                params.add(new BasicNameValuePair("nombre_usuario", datosUsuario[1]));
                params.add(new BasicNameValuePair("ubicacion_actual", ubicacionActual.latitude + "&" + ubicacionActual.longitude));
                params.add(new BasicNameValuePair("ubicacion_paradero", ubicacionParadero.latitude + "&" + ubicacionParadero.longitude));

                httpPost.setEntity(new UrlEncodedFormEntity(params));
                response = httpClient.execute(httpPost, localContext);
                HttpEntity httpEntity = response.getEntity();
                resultado = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                resultado = "nonet";
            }
            return resultado;

        }

    }//fin de async tasks

}//fin de la clase

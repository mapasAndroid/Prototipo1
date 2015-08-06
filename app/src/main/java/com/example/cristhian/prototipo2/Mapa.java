package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Mapa extends ActionBarActivity {

    private GoogleMap mMap;
    private ActionBarDrawerToggle drawerToggle;
    private Lugares cx = new Lugares();

    private String datosParadero;
    private String[] datosUsuario;
    private BaseDeDatos baseDeDatos;
    private final double MARGEN_DE_ERROR = 753.9047315860873;
    private final double MARGEN_DE_ERROR_LUGAR = 700.9047315860873;
    public static final LatLng CAMARA = new LatLng(7.885067, -72.500351);

    //Atributo que encripta las contrasenias en sha1
    private Encriptador encriptador = new Encriptador();
    //Atributo que gestiona las conexiones a datos web
    private WebHelper webHelper = new WebHelper();

    //datos globales para usar desde el Async Task
    LatLng ubicacionActual;
    LatLng ubicacionParadero;
    String rutaString;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapas);


        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

        this.baseDeDatos = new BaseDeDatos(getBaseContext());

        Bundle datosFragmento = getIntent().getExtras();
        if (datosFragmento != null) {
            datosParadero = datosFragmento.get("datosParadero").toString();
            datosUsuario = datosFragmento.getStringArray("datosUsuario");
        }
        setUpMapIfNeeded();


        //=========== LOCALIZACION ============
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Toast.makeText(getBaseContext(), location.toString(), Toast.LENGTH_LONG).show();
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

        //============ FIN LOCALIZACION ============

        this.ubicacionActual = new LatLng(
                7.8928452,-72.5025499

        );


        this.ubicacionParadero = new LatLng(
                Double.parseDouble(this.datosParadero.split("&")[3]),
                Double.parseDouble(this.datosParadero.split("&")[4])
        );

        //agrego los markers en las posiciones a tratar, actual y la del paradero
        agregarMarker(this.ubicacionActual, BitmapDescriptorFactory.HUE_BLUE, "Aqui estas", "actual");

        agregarMarker(
                this.ubicacionParadero,
                BitmapDescriptorFactory.HUE_ORANGE,
                this.datosParadero.split("&")[1],
                this.datosParadero.split("&")[2]
        );

        this.rutaString = getRutaApropiada();

        if (!this.rutaString.isEmpty()) {

            this.baseDeDatos.abrir();
            ArrayList<LatLng> ruta = baseDeDatos.getWaypointsByRuta(this.rutaString);
            this.baseDeDatos.cerrar();

            pintarRuta(ruta, new ColorDrawable(Color.parseColor("#3F51B5")).getColor());

        } else {
            finish();
        }



    }

    public void pintarRuta(ArrayList<LatLng> puntos, int color) {
        PolylineOptions opciones = new PolylineOptions();
        opciones.addAll(puntos);
        opciones.color(color);
        opciones.width(4);
        this.mMap.addPolyline(opciones);
    }

    private String getRutaApropiada() {

        this.baseDeDatos.abrir();
        String waypoints[] = this.baseDeDatos.getTodosWaypoints();
        this.baseDeDatos.cerrar();
        String b = calcularRuta(waypoints, new LatLng(7.8928452,-72.5025499), this.ubicacionParadero);
        return b;
    }

    private String calcularRuta(String[] waypoints, LatLng posicionActual, LatLng posicionParadero) {

        ArrayList<String[]> cercanasPosActual = new ArrayList<>();

        for (int i = 0; i < waypoints.length; i++) {

            String[] waypointActual = waypoints[i].split("&");

            double dif = diferencia(
                    new LatLng(Double.parseDouble(waypointActual[2]), Double.parseDouble(waypointActual[3])),
                    posicionActual
            );


            if (dif <= this.MARGEN_DE_ERROR) {
                cercanasPosActual.add(waypointActual);
            }
        }

        if (cercanasPosActual.isEmpty()) {

            return "";
        }

        Iterator cercanos = cercanasPosActual.iterator();

        while (cercanos.hasNext()) {
            String datosWaypoint[] = (String[]) cercanos.next();

            double dif = diferencia(
                    new LatLng(Double.parseDouble(datosWaypoint[2]), Double.parseDouble(datosWaypoint[3])),
                    posicionParadero

            );

            if (dif <= this.MARGEN_DE_ERROR_LUGAR) {
                return datosWaypoint[0];
            }
        }
        return "";
    }

    private double diferencia(LatLng x, LatLng y) {
        double r = 6371000;
        double c = Math.PI / 180;
        return (2 * r * Math.asin(Math.sqrt(Math.pow(Math.sin(c * (y.latitude - x.latitude) / 2), 2) +
                Math.cos(c * x.latitude) * Math.cos(c * y.latitude) * Math.pow(Math.sin(c * (y.longitude - x.longitude) / 2), 2))));
    }

    private void agregarMarker(LatLng posicion, float color, String titulo, String snippet) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(posicion)
                            .title(titulo)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(color))
            );
        }
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

    public class BusuqedaDeBus extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String respuesta) {
            if(respuesta.isEmpty()){
                //asistente.imprimir(getFragmentManager(), "no pudimos encontrar un bus cercano", 1);
                return;
            }

            if(respuesta.equals("nonet")){
                //asistente.imprimir(getFragmentManager(), "no estras conectado a internet", 1);
                return;
            }

            String datosMostrar [] = respuesta.split("&");
            //en la pos 0 placa, 1 conductor, 2 nombre_ruta,
            // 3 tiempo estimado de demora, 4 distancia aproximada




        }

        protected String doInBackground(String... params) {
            return copiarDatosweb(params[0]);
        }

        public String copiarDatosweb(String id_ruta) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(webHelper.getUrl() + webHelper.getUrlBuscarBus());
            Log.i("cm01", webHelper.getUrl() + webHelper.getUrlBuscarBus());
            HttpResponse response = null;
            String resultado = "";
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("usuario", webHelper.getUsuario()));
                params.add(new BasicNameValuePair("contrasenia", webHelper.getPass()));
                params.add(new BasicNameValuePair("id_ruta", id_ruta));
                params.add(new BasicNameValuePair("ubicacion_actual", ubicacionActual.latitude + "&" + ubicacionActual.longitude));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                response = httpClient.execute(httpPost, localContext);
                HttpEntity httpEntity = response.getEntity();
                resultado = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                resultado = "nonet";
            }
            return resultado;

        }

    }//fin de async task

}//fin de la clase

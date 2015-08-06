package com.example.cristhian.prototipo2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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


        //agrego los markers en las posiciones a tratar, actual y la del paradero
        agregarMarker(this.ubicacionActual, BitmapDescriptorFactory.HUE_BLUE, "actual", "actual");



        this.ubicacionParadero = new LatLng(
                Double.parseDouble(this.datosParadero.split("&")[3]),
                Double.parseDouble(this.datosParadero.split("&")[4])
        );


        agregarMarker(
                this.ubicacionParadero,
                BitmapDescriptorFactory.HUE_ORANGE,
                this.datosParadero.split("&")[1],
                this.datosParadero.split("&")[2]
        );

        String id_ruta = getRutaApropiada();




        this.rutaString = id_ruta;
        Log.i("cm01", "ruta que sirve::: " + id_ruta);
        if (!id_ruta.isEmpty()) {
            //this.recorridoRuta = this.baseDeDatos.getWaypointsByRuta(id_ruta);
            //BusuqedaDeBus busuqedaDeBus = new BusuqedaDeBus();
            //busuqedaDeBus.execute(id_ruta);
        } else {
            //imprima mensaje
            Log.i("cm01", "no hay rutas cercanas ");
        }

        this.baseDeDatos.abrir();
        ArrayList<LatLng> ruta = baseDeDatos.getWaypointsByRuta(id_ruta);
        this.baseDeDatos.cerrar();

        pintarRuta(ruta, Color.BLUE);

    }

    private String toStringRuta(String[] ruta1) {
        String res = "";
        for (String dato : ruta1) {
            res += dato.toString() + "*";
        }
        return res;
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

            return "vacio";
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
            intent.setData(Uri.parse("http://pruebasmais.zz.mu/stopbus/acerca_de.php"));
            startActivity(intent);
        }

        if (id == R.id.action_ayuda) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://pruebasmais.zz.mu/stopbus/contacto.php"));
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

        mMap.moveCamera(CameraUpdateFactory.newLatLng(CAMARA));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    public class BusuqedaDeBus extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String posBus) {
            Log.i("cm01", "ubicacionBus::: " + posBus);
            Log.i("cm01", "ubicacionActual::: " + ubicacionActual);
            Log.i("cm01", "ubicacionParadero::: " + ubicacionParadero);
            Log.i("cm01", "id de la ruta apropiada:: " + rutaString);


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
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                response = httpClient.execute(httpPost, localContext);
                HttpEntity httpEntity = response.getEntity();
                resultado = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                Log.i("cm01", e.toString());
            }
            return resultado;

        }

    }//fin de async task

}//fin de la clase

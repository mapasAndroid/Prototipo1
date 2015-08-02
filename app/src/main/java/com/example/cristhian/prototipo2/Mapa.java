package com.example.cristhian.prototipo2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
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
import com.google.android.gms.maps.model.LatLng;

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
    private Lugares cx= new Lugares();

    private String datosParadero;
    private String[] datosUsuario;
    private BaseDeDatos baseDeDatos;
    private final double MARGEN_DE_ERROR = 753.9047315860873;
    private final double MARGEN_DE_ERROR_LUGAR = 700.9047315860873;
    public static final LatLng CAMARA = new LatLng(7.885067,-72.500351);

    //Atributo que encripta las contrasenias en sha1
    private Encriptador encriptador = new Encriptador();
    //Atributo que gestiona las conexiones a datos web
    private WebHelper webHelper = new WebHelper();

    //datos para usar desde el Async Task
    LatLng ubicacionActual;
    LatLng ubicacionParadero;
    LatLng[] recorridoRuta;
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

        // cambiar posiciones para pruebas
        this.ubicacionActual =new LatLng(7.893039,-72.502297);// getUbicacionActual();


        //7.894214,-72.499529

        this.ubicacionParadero = new LatLng(
                Double.parseDouble(this.datosParadero.split("&")[3]),
                Double.parseDouble(this.datosParadero.split("&")[4])
        );


        String id_ruta = getRutaApropiada();

        this.rutaString = id_ruta;
        Log.i("cm01", "la ruta adecuada para ir a "+this.datosParadero.split("&")[1]+" es la "+id_ruta);
        if (!id_ruta.isEmpty()) {
            //this.recorridoRuta = this.baseDeDatos.getWaypointsByRuta(id_ruta);
            //BusuqedaDeBus busuqedaDeBus = new BusuqedaDeBus();
            //busuqedaDeBus.execute(id_ruta);
        } else {
            //imprima mensaje
            Log.i("cm01", "no hay rutas cercanas ");
        }


    }

    private String getRutaApropiada() {

        this.baseDeDatos.abrir();
        String v[] = this.baseDeDatos.getTodosWaypoints();
        this.baseDeDatos.cerrar();
        String b = calcularRuta(v, this.ubicacionActual, this.ubicacionParadero);
        return b;
    }

    private String calcularRuta(String[] waypoints, LatLng posicionActual, LatLng posicionParadero) {

        ArrayList<String[]> cercanasPosACtual = new ArrayList<>();

        for (int i = 0; i < waypoints.length; i++) {

            String[] datosWaypoint = waypoints[i].split("&");

            double dif = diferencia(new LatLng(Double.parseDouble(datosWaypoint[1]), Double.parseDouble(datosWaypoint[2])), posicionActual);
            if (dif <= this.MARGEN_DE_ERROR) {
                cercanasPosACtual.add(datosWaypoint);
            }
        }

        if (cercanasPosACtual.isEmpty()) {

            return "";
        }

        Iterator cercanos = cercanasPosACtual.iterator();
        while (cercanos.hasNext()) {
            String datosWaypoint[] = (String[]) cercanos.next();

            double dif = diferencia(
                    new LatLng(Double.parseDouble(datosWaypoint[1]), Double.parseDouble(datosWaypoint[2])),
                    posicionParadero);
            if (dif <= this.MARGEN_DE_ERROR_LUGAR) {
                Log.i("cm01", datosWaypoint[0]);
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

    private LatLng getUbicacionActual() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);

        return new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
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
            Log.i("cm01", webHelper.getUrl() + webHelper.getUrlBuscarBus() );
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


    }


}

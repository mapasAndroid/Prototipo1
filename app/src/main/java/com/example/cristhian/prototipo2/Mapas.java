package com.example.cristhian.prototipo2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Mapas extends ActionBarActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapas);
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        setMapaMarker();
    }

    public HashMap<String, Marker> mapaMarker = new HashMap<String, Marker>();


    public void agregueMarker(double lat, double lng, String titulo) {
        Marker x = this.mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(titulo)
                        .visible(false)
        );
        this.mapaMarker.put(titulo, x);
    }

    public void setMapaMarker() {

        //marcadores estudio
        agregueMarker(7.884461, -72.5004519, "A1");//unisimon calle 13
        agregueMarker(7.88229, -72.500593, "A2"); //ageso av 3
        agregueMarker(7.882154, -72.501313, "A3"); //sagrado corazon av 4
        agregueMarker(7.884857, -72.502388, "A4"); //carmary av 4
        agregueMarker(7.882402, -72.501917, "A5"); //gran colombiano av 4
        agregueMarker(7.885591, -72.499565, "A6"); //biblioteca calle 12
        agregueMarker(7.88577, -72.49855, "A7"); //nacional de comercio calle 12
        agregueMarker(7.885558, -72.498103, "A8"); //BBVA av 0
        agregueMarker(7.885217, -72.498055, "A9"); //bancolombia av 0
        agregueMarker(7.884637, -72.50011, "A10"); //capilla del carmen calle 13
        agregueMarker(7.884461, -72.5004519, "A1");//unisimon calle 13

        //centros comerciales, bancos y cajeros
        agregueMarker(7.88849,-72.505266, "B1");//alejandria av 6
        agregueMarker(7.887318,-72.505035, "B2"); //plaza de los andes av 6
        agregueMarker(7.886921,-72.50496, "B3"); //BBVA cajero av 6
        agregueMarker(7.886921,-72.50496, "B4"); //davivienda banco av 6
        agregueMarker(7.8859536,-72.5049926, "B5"); //santander banco av 6
        agregueMarker(7.88514,-72.50342, "B6"); //cajero ancolombia av 5
        agregueMarker(7.88509,-72.503855, "B7"); //exito av 5
        agregueMarker(7.8852947,-72.5038695, "B8"); //banco popular av 5
        agregueMarker(7.8859988,-72.50353625, "B9"); //av villas calle 11 banco
        agregueMarker(7.886122,-72.5029179, "B10"); //cajero ath calle 11
        agregueMarker(7.8855549,-72.503075, "B11"); //lecs centro comercial av 4
        agregueMarker(7.8879759,-72.5039625, "B12"); //rayotex calle 9
        agregueMarker(7.8879246,-72.5037372, "B13"); //river plaza centro comercial calle 9
        agregueMarker(7.8880609,-72.5033699, "B14"); //bbva calle 9 cajero
        agregueMarker(7.88849,-72.505266, "B1");//alejandria av 6

        //parques
        agregueMarker(7.885067,-72.500351, "C1");//parque colon av 2
        agregueMarker(7.887344,-72.501511, "C2"); //hotel acora calle 10
        agregueMarker(7.887659,-72.501875, "C3"); //hotel efrau av 3
        agregueMarker(7.8876415,-72.5020174, "C4"); //hotel la bastilla av 3
        agregueMarker(7.8887201,-72.50241, "C5"); //adpostal calle 8
        agregueMarker(7.888245,-72.503285, "C6"); //bbva calle 9
        agregueMarker(7.8879762,-72.5039627, "C7"); //rayotex calle 9
        agregueMarker(7.88514,-72.50342, "C8"); //bancolombia cajero av 5
        agregueMarker(7.8863475,-72.504373, "C9"); //parque santander calle 10
        agregueMarker(7.88743,-72.50526, "C10"); //plaza de los andes av 6
        agregueMarker(7.88797,-72.50548, "C11"); //alejandria av 6
        agregueMarker(7.8882618,-72.5069983, "C12"); //atlantis plaza calle 8
        agregueMarker(7.8879904,-72.5072965, "C13"); //parque mercedes calle 8
        agregueMarker(7.891818,-72.505613, "C14"); //parque lineal calle 5
        agregueMarker(7.8919608,-72.5043739, "C15"); //representaciones orozco calle 5
        agregueMarker(7.8934246,-72.5032076, "C16"); //estacion de servicio los amigos calle 4
        agregueMarker(7.8934246,-72.5032076, "C17"); //u pamplona diagonal
        agregueMarker(7.8923437,-72.5017718, "C18"); //efecty av 2
        agregueMarker(7.885067,-72.500351, "C1");//parque colon av 2


        //hoteles y restaurantes
        agregueMarker(7.8841587,-72.4986827, "D1");//pachoricuras av 1
        agregueMarker(7.8849064,-72.499263, "D2"); //biblioteca av 1
        agregueMarker(7.886646,-72.5009548, "D3"); //hotel acora calle 10
        agregueMarker(7.8875916,-72.501719, "D3"); //hotel efrau av 3
        agregueMarker(7.8867775,-72.5019828, "D4"); //la bastilla av 3
        agregueMarker(7.8876952,-72.5036797, "D5"); //la mazorca av 4
        agregueMarker(7.8873688,-72.503484, "D6"); //sonricenter av 4
        agregueMarker(7.8881263,-72.5023979, "D7"); //pasteleria la mejor calle 9
        agregueMarker(7.8878918,-72.5044093, "D8"); //amaruc calle 10
        agregueMarker(7.8870899,-72.5035129, "D9"); //bancolombia av 5
        agregueMarker(7.8878918,-72.5044093, "D10"); //pasteleria ara de oro calle 9
        agregueMarker(7.8873253,-72.5047462, "D11"); //plaza de los andes av 6
        agregueMarker(7.8861056,-72.5045606, "D12"); //banco santander av 6
        agregueMarker(7.8833239,-72.50037893, "D14"); //copy copy calle 14
        agregueMarker(7.8835552,-72.4993124, "D15"); //serventel baloto calle 14
        agregueMarker(7.8841587,-72.4986827, "D1");//pachoricuras av 1


    }

    public void muestre(String tipo) {
        Iterator x = this.mapaMarker.entrySet().iterator();
        while (x.hasNext()) {
            Map.Entry temporal = (Map.Entry) x.next();

            Marker marcador = (Marker) temporal.getValue();
            if (marcador.getTitle().startsWith(tipo)) {
                marcador.setVisible(true);
            } else {
                marcador.setVisible(false);
            }
        }
    }

    public void getA(View view) {muestre("A");}

    public void getB(View view) {
        muestre("B");
    }

    public void getC(View view) {
        muestre("C");
    }

    public void getD(View view) {
        muestre("D");
    }
}

package com.example.cristhian.prototipo2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.cristhian.prototipo2.StopBusContract.*;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CRISTHIAN and MAITE
 */
public class BaseDeDatos {


    private DBHelper nDBHelper;

    private Context contexto;

    SQLiteDatabase nBaseDatos;


    public BaseDeDatos(Context contexto) {
        this.contexto = contexto;
    }

    /**
     * metodo que abre una coneccion con la base de datos
     * @return un objeto de tipo base de datos
     */
    public BaseDeDatos abrir() {
        this.nDBHelper = new DBHelper(this.contexto);
        this.nBaseDatos = this.nDBHelper.getWritableDatabase();
        return this;
    }

    /**
     * metodo que cierra la coneccion con la base de datos
     */
    public void cerrar() {
        this.nDBHelper.close();
    }


    /**
     * metodo que permite copiar los datos a la base de datos local, por medio de un JSON
     *
     * @param s es la cadena con el contenido de las tabals en formato JSON
     */
    public void duplicarEnLocal(String s) {

        insertarTablaPasajero(s);
        insertarTablaRuta(s);
        insertarTablaParadero(s);
        insertarTablaParaderoxRuta(s);
        insertarTablaBus(s);
        insertarTablaWaypoints(s);

    }


    /*
    ========================================================
    PARSEANDO LOS DATOS JSON PARA PODER INSERTARLOS EN LA BD
    =========================================================
     */

    public void insertarTablaWaypoints(String s) {
        try {

            JSONObject objetoPapa = new JSONObject(s);
            JSONObject paraderosxrutas = objetoPapa.getJSONObject("7");
            for (int i = 0; i < paraderosxrutas.length(); i++) {
                String id_ruta = paraderosxrutas.getJSONObject(i + "").getString("id_ruta");
                String latitud = paraderosxrutas.getJSONObject(i + "").getString("latitud");
                String longitud = paraderosxrutas.getJSONObject(i + "").getString("longitud");
                this.insertarWaypoint(id_ruta, latitud, longitud);
            }

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }
    }

    public void insertarTablaPasajero(String response) {
        try {

            JSONObject objetoPapa = new JSONObject(response);
            JSONObject lista = objetoPapa.getJSONObject("1");
            JSONObject detallesUsuario = lista.getJSONObject("0");

            this.insertarUsuario(
                    detallesUsuario.getString("usuario"),
                    detallesUsuario.getString("nombre"),
                    detallesUsuario.getString("correo"),
                    detallesUsuario.getString("password")
            );

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }
    }

    private void insertarTablaRuta(String s) {
        try {

            JSONObject objetoPapa = new JSONObject(s);
            JSONObject rutas = objetoPapa.getJSONObject("2");
            for (int i = 0; i < rutas.length(); i++) {
                String id_ruta = rutas.getJSONObject(i + "").getString("id_ruta");
                String nombre = rutas.getJSONObject(i + "").getString("nombre");
                this.insertarRuta(id_ruta, nombre);
            }

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }
    }

    private void insertarTablaParadero(String s) {

        try {

            JSONObject objetoPapa = new JSONObject(s);
            JSONObject paraderos = objetoPapa.getJSONObject("3");
            for (int i = 0; i < paraderos.length(); i++) {
                String id_paradero = paraderos.getJSONObject(i + "").getString("id_paradero");
                String nombre = paraderos.getJSONObject(i + "").getString("nombre");
                String direccion = paraderos.getJSONObject(i + "").getString("direccion");
                String latitud = paraderos.getJSONObject(i + "").getString("latitud");
                String longitud = paraderos.getJSONObject(i + "").getString("longitud");
                this.insertarParadero(id_paradero, nombre, direccion, latitud, longitud);
            }

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }


    }

    private void insertarTablaParaderoxRuta(String s) {

        try {

            JSONObject objetoPapa = new JSONObject(s);
            JSONObject paraderosxrutas = objetoPapa.getJSONObject("4");
            for (int i = 0; i < paraderosxrutas.length(); i++) {
                String id_paradero = paraderosxrutas.getJSONObject(i + "").getString("id_paradero");
                String id_ruta = paraderosxrutas.getJSONObject(i + "").getString("id_ruta");
                this.insertarParaderoxRuta(id_paradero, id_ruta);
            }

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }
    }

    private void insertarTablaBus(String s) {
        try {

            JSONObject objetoPapa = new JSONObject(s);
            JSONObject buses = objetoPapa.getJSONObject("5");
            for (int i = 0; i < buses.length(); i++) {
                String placa = buses.getJSONObject(i + "").getString("placa");
                String conductor = buses.getJSONObject(i + "").getString("conductor");
                String id_ruta = buses.getJSONObject(i + "").getString("id_ruta");
                String nit = buses.getJSONObject(i + "").getString("nit");

                this.insertarBus(placa, conductor, id_ruta, nit);
            }

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }
    }



    /*
    =================================
    INSERCIONES EN LA BASE DE DATOS
    =================================
     */



    //debe ser xq ya estan, borra la app y volvamos a ejecutarlas amor voy amor
    private void insertarWaypoint(String id_ruta, String latitid, String longitud) {
        ContentValues values = new ContentValues();

        values.put("id_ruta", id_ruta);
        values.put("latitud", latitid);
        values.put("longitud", longitud);

    }


    /**
     * metodo que registra una ruta en la BD local
     *
     * @param id_ruta el id de la ruta
     * @param nombre  el nombre de la ruta
     */
    private void insertarRuta(String id_ruta, String nombre) {
        ContentValues values = new ContentValues();

        values.put("id_ruta", id_ruta);
        values.put("nombre", nombre);

        this.nBaseDatos.insert("ruta", null, values);
    }

    private void insertarUsuario(String usuario, String nombre, String correo, String password) {
        ContentValues values = new ContentValues();

        values.put("usuario", usuario);
        values.put("nombre", nombre);
        values.put("correo", correo);
        values.put("password", password);

        this.nBaseDatos.insert("pasajero", null, values);
    }

    private void insertarParadero(String id_paradero, String nombre, String direccion,
                                  String latitud, String longitud) {
        ContentValues values = new ContentValues();

        values.put("id", id_paradero);
        values.put("nombre", nombre);
        values.put("direccion", direccion);
        values.put("latitud", latitud);
        values.put("longitud", longitud);

        this.nBaseDatos.insert("paradero", null, values);
    }

    private void insertarParaderoxRuta(String id_paradero, String id_ruta) {
        ContentValues values = new ContentValues();

        values.put("id_paradero", id_paradero);
        values.put("id_ruta", id_ruta);
        this.nBaseDatos.insert("paraderoxruta", null, values);

    }


    private void insertarBus(String placa, String conductor, String id_ruta, String nit) {

        ContentValues values = new ContentValues();

        values.put("placa", placa);
        values.put("conductor", conductor);
        values.put("id_ruta", id_ruta);
        values.put("nit", nit);
        this.nBaseDatos.insert("bus", null, values);

    }

    private void insertarReciente(String id, String nombre, String direccion, String latitud, String longitud) {

        ContentValues values = new ContentValues();

        values.put("id", id);
        values.put("nombre", nombre);
        values.put("direccion", direccion);
        values.put("latitud", latitud);
        values.put("longitud", longitud);

        this.nBaseDatos.insert("recientes", null, values);
    }

    public void agregarReciente(String datos) {
        //separo por el simbolo &
        String datosReciente[] = datos.split("&");
        this.insertarReciente(datosReciente[0],
                datosReciente[1], datosReciente[2], datosReciente[3], datosReciente[4]);
    }


    /*
   =================================
          REMOVER RECIENTE POR ID
   =================================
    */

    public void eliminarReciente(String id) {

        this.nBaseDatos.delete("recientes", "id=\'" + id + "\'", null);
    }


    /*
   =================================
               GETTERS
   =================================
    */

    public String[] getDatosUsuario() {

        String columnas[] = new String[]{
                "usuario",
                "nombre",
                "correo",
                "password"
        };

        Cursor c = this.nBaseDatos.query("pasajero", columnas, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }

        int usuario = c.getColumnIndexOrThrow("usuario");
        int nombre = c.getColumnIndexOrThrow("nombre");
        int correo = c.getColumnIndexOrThrow("correo");
        int password = c.getColumnIndexOrThrow("password");

        c.moveToFirst();

        String[] x = new String[]{c.getString(usuario),
                c.getString(nombre),
                c.getString(correo),
                c.getString(password)};

        return x;
    }


    public String getReciente(String id) {
        String columnas[] = new String[]{
                "id",
                "nombre",
                "latitud",
                "longitud"
        };

        Cursor c = this.nBaseDatos.query("recientes", columnas, "id=\'" + id + "\'", null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }

        int id_ = c.getColumnIndexOrThrow("id");
        int nombre = c.getColumnIndexOrThrow("nombre");
        int latitud = c.getColumnIndexOrThrow("latitud");
        int longitud = c.getColumnIndexOrThrow("longitud");
        c.moveToFirst();

        //Separo el id del nombre ocn un simbolo &
        return c.getString(id_) + "&" +
                c.getString(nombre) + "&" +
                c.getString(latitud) + "&" +
                c.getString(longitud);
    }

    /**
     * retorna los paraderos de toda la base de datos
     *
     * @param tipo el tipo de paraderos que se necesita buscar
     * @return un vector con los paraderos separados por coma, o null
     * en caso contrario
     */
    public String[] getParaderos(String tipo) {

        String columnas[] = new String[]{
                "id",
                "nombre",
                "direccion",
                "latitud",
                "longitud"
        };

        if (tipo.equals("LR")) {
            return getTodosRecientes();
        }

        Cursor c = this.nBaseDatos.query("paradero", columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }
        ArrayList<String> res = new ArrayList<String>();

        int id = c.getColumnIndexOrThrow("id");
        int nombre = c.getColumnIndexOrThrow("nombre");
        int direccion = c.getColumnIndexOrThrow("direccion");
        int latitud = c.getColumnIndexOrThrow("latitud");
        int longitud = c.getColumnIndexOrThrow("longitud");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if (c.getString(id).startsWith(tipo + "$")) {
                String esReciente = (this.getReciente(c.getString(id)));
                if (!esReciente.isEmpty()) {
                    esReciente = "si";
                } else {
                    esReciente = "no";
                }
                res.add(c.getString(id) + "&" + c.getString(nombre) + "&"
                        + c.getString(direccion) + "&"
                        + c.getString(latitud) + "&" + c.getString(longitud) + "&"
                        + esReciente);
            }
        }


        if (res.isEmpty()) {
            return null;
        }

        String[] respuesta = new String[res.size()];
        for (int i = 0; i < res.size(); i++) {
            respuesta[i] = res.get(i);
        }

        return respuesta;

    }

    public String[] getTodosRecientes() {
        String columnas[] = new String[]{
                "id",
                "nombre",
                "direccion",
                "latitud",
                "longitud"
        };

        Cursor c = this.nBaseDatos.query("recientes", columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }
        ArrayList<String> res = new ArrayList<String>();

        int id = c.getColumnIndexOrThrow("id");
        int nombre = c.getColumnIndexOrThrow("nombre");
        int direccion = c.getColumnIndexOrThrow("direccion");
        int latitud = c.getColumnIndexOrThrow("latitud");
        int longitud = c.getColumnIndexOrThrow("longitud");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            res.add(c.getString(id) + "&" + c.getString(nombre) + "&"
                    + c.getString(direccion) + "&"
                    + c.getString(latitud) + "&" + c.getString(longitud) + "&si");
        }

        if (res.isEmpty()) {
            return null;
        }

        String[] respuesta = new String[res.size()];
        for (int i = 0; i < res.size(); i++) {
            respuesta[i] = res.get(i);
        }

        return respuesta;

    }

    public String getParaderoPorId(String id) {

        String columnas[] = new String[]{
                "id",
                "nombre",
                "direccion",
                "latitud",
                "longitud"
        };

        Cursor c = this.nBaseDatos.query("paradero", columnas, "id=\'" + id + "\'", null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }

        int id_ = c.getColumnIndexOrThrow("id");
        int nombre = c.getColumnIndexOrThrow("nombre");
        int direccion = c.getColumnIndexOrThrow("direccion");
        int latitud = c.getColumnIndexOrThrow("latitud");
        int longitud = c.getColumnIndexOrThrow("longitud");

        c.moveToFirst();

        //Separo el id del nombre ocn un simbolo &
        return c.getString(id_) + "&" +
                c.getString(nombre) + "&" +
                c.getString(direccion) + "&" +
                c.getString(latitud) + "&" +
                c.getString(longitud);
    }

    public LatLng[] getWaypointsByRuta(String id_ruta) {
        String columnas[] = new String[]{
                Waypoints.WAY_LATITUD,
                Waypoints.WAY_LONGITUD
        };

        Cursor c = this.nBaseDatos.query(Waypoints.NOMBRE_TABLA, columnas, Waypoints.WAY_ID_RUTA + "=\'" + id_ruta + "\'", null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }

        int latitud = c.getColumnIndexOrThrow(Waypoints.WAY_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Waypoints.WAY_LONGITUD);

        LatLng res [] = new LatLng[c.getCount()];
        int i = 0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(), i++) {
            res[i] = new LatLng(c.getDouble(latitud), c.getDouble(longitud));
        }

        return res;

    }

    public String[] getTodosWaypoints() {

        String columnas[] = new String[]{
                Waypoints.WAY_ID_RUTA,
                Waypoints.WAY_LATITUD,
                Waypoints.WAY_LONGITUD
        };

        Cursor c = this.nBaseDatos.query(Waypoints.NOMBRE_TABLA, columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }

        int id = c.getColumnIndexOrThrow(Waypoints.WAY_ID_RUTA);
        int latitud = c.getColumnIndexOrThrow(Waypoints.WAY_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Waypoints.WAY_LONGITUD);

        String res [] = new String[c.getCount()];
        int i = 0 ;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(), i++) {
            res[i] = c.getString(id) + "&" + c.getString(latitud) + "&" + c.getString(longitud);
        }

        return res;

    }
}


package com.example.cristhian.prototipo2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cristhian.prototipo2.StopBusContract.Bus;
import com.example.cristhian.prototipo2.StopBusContract.Paradero;
import com.example.cristhian.prototipo2.StopBusContract.ParaderoxRuta;
import com.example.cristhian.prototipo2.StopBusContract.Pasajero;
import com.example.cristhian.prototipo2.StopBusContract.Recientess;
import com.example.cristhian.prototipo2.StopBusContract.Ruta;
import com.example.cristhian.prototipo2.StopBusContract.Waypoints;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

public class BaseDeDatos {


    private DBHelper nDBHelper;

    private Context contexto;

    SQLiteDatabase nBaseDatos;


    public BaseDeDatos(Context contexto) {
        this.contexto = contexto;
    }

    /**
     * metodo que abre una coneccion con la base de datos
     *
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
                String id_ruta = paraderosxrutas.getJSONObject(i + "").getString(Waypoints.WAY_ID_RUTA);
                int consecutivo = paraderosxrutas.getJSONObject(i + "").getInt(Waypoints.WAY_CONSECUTIVO);
                String latitud = paraderosxrutas.getJSONObject(i + "").getString(Waypoints.WAY_LATITUD);
                String longitud = paraderosxrutas.getJSONObject(i + "").getString(Waypoints.WAY_LONGITUD);
                this.insertarWaypoint(id_ruta, consecutivo, latitud, longitud);
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
                    detallesUsuario.getString(Pasajero.P_USUARIO),
                    detallesUsuario.getString(Pasajero.P_NOMBRE),
                    detallesUsuario.getString(Pasajero.P_CORREO),
                    detallesUsuario.getString(Pasajero.P_PASSWORD)
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
                String id_ruta = rutas.getJSONObject(i + "").getString(Ruta.RU_ID_RUTA);
                String nombre = rutas.getJSONObject(i + "").getString(Ruta.RU_NOMBRE);
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
                String id_paradero = paraderos.getJSONObject(i + "").getString(Paradero.PA_ID);
                String nombre = paraderos.getJSONObject(i + "").getString(Paradero.PA_NOMBRE);
                String direccion = paraderos.getJSONObject(i + "").getString(Paradero.PA_DIRECCION);
                String latitud = paraderos.getJSONObject(i + "").getString(Paradero.PA_LATITUD);
                String longitud = paraderos.getJSONObject(i + "").getString(Paradero.PA_LONGITUD);
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
                String id_paradero = paraderosxrutas.getJSONObject(i + "").getString(ParaderoxRuta.PR_ID_PARADERO);
                String id_ruta = paraderosxrutas.getJSONObject(i + "").getString(ParaderoxRuta.PR_ID_RUTA);
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
                String placa = buses.getJSONObject(i + "").getString(Bus.B_PLACA);
                String conductor = buses.getJSONObject(i + "").getString(Bus.B_CONDUCTOR);
                String id_ruta = buses.getJSONObject(i + "").getString(Bus.B_ID_RUTA);
                String nit = buses.getJSONObject(i + "").getString(Bus.B_NIT);

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
    private void insertarWaypoint(String id_ruta, int consecutivo, String latitid, String longitud) {
        ContentValues values = new ContentValues();

        values.put(Waypoints.WAY_ID_RUTA, id_ruta);
        values.put(Waypoints.WAY_CONSECUTIVO, consecutivo);
        values.put(Waypoints.WAY_LATITUD, latitid);
        values.put(Waypoints.WAY_LONGITUD, longitud);

        this.nBaseDatos.insert(Waypoints.NOMBRE_TABLA, null, values);

    }


    /**
     * metodo que registra una ruta en la BD local
     *
     * @param id_ruta el id de la ruta
     * @param nombre  el nombre de la ruta
     */
    private void insertarRuta(String id_ruta, String nombre) {
        ContentValues values = new ContentValues();

        values.put(Ruta.RU_ID_RUTA, id_ruta);
        values.put(Ruta.RU_NOMBRE, nombre);

        this.nBaseDatos.insert(Ruta.NOMBRE_TABLA, null, values);
    }

    private void insertarUsuario(String usuario, String nombre, String correo, String password) {
        ContentValues values = new ContentValues();

        values.put(Pasajero.P_USUARIO, usuario);
        values.put(Pasajero.P_NOMBRE, nombre);
        values.put(Pasajero.P_CORREO, correo);
        values.put(Pasajero.P_PASSWORD, password);

        this.nBaseDatos.insert(Pasajero.NOMBRE_TABLA, null, values);
    }

    private void insertarParadero(String id_paradero, String nombre, String direccion,
                                  String latitud, String longitud) {
        ContentValues values = new ContentValues();

        values.put(Paradero.PA_ID, id_paradero);
        values.put(Paradero.PA_NOMBRE, nombre);
        values.put(Paradero.PA_DIRECCION, direccion);
        values.put(Paradero.PA_LATITUD, latitud);
        values.put(Paradero.PA_LONGITUD, longitud);

        this.nBaseDatos.insert(Paradero.NOMBRE_TABLA, null, values);
    }

    private void insertarParaderoxRuta(String id_paradero, String id_ruta) {
        ContentValues values = new ContentValues();

        values.put(ParaderoxRuta.PR_ID_PARADERO, id_paradero);
        values.put(ParaderoxRuta.PR_ID_RUTA, id_ruta);
        this.nBaseDatos.insert(ParaderoxRuta.NOMBRE_TABLA, null, values);

    }


    private void insertarBus(String placa, String conductor, String id_ruta, String nit) {

        ContentValues values = new ContentValues();

        values.put(Bus.B_PLACA, placa);
        values.put(Bus.B_CONDUCTOR, conductor);
        values.put(Bus.B_ID_RUTA, id_ruta);
        values.put(Bus.B_NIT, nit);
        this.nBaseDatos.insert(Bus.NOMBRE_TABLA, null, values);

    }

    private void insertarReciente(String id, String nombre, String direccion, String latitud, String longitud) {

        ContentValues values = new ContentValues();

        values.put(Recientess.R_ID, id);
        values.put(Recientess.R_NOMBRE, nombre);
        values.put(Recientess.R_DIRECCION, direccion);
        values.put(Recientess.R_LATITUD, latitud);
        values.put(Recientess.R_LONGITUD, longitud);

        this.nBaseDatos.insert(Recientess.NOMBRE_TABLA, null, values);
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

        this.nBaseDatos.delete(Recientess.NOMBRE_TABLA, Recientess.R_ID + "=\'" + id + "\'", null);
    }


    /*
   =================================
               GETTERS
   =================================
    */

    public String[] getDatosUsuario() {

        String columnas[] = new String[]{
                Pasajero.P_USUARIO,
                Pasajero.P_NOMBRE,
                Pasajero.P_CORREO,
                Pasajero.P_PASSWORD
        };

        Cursor c = this.nBaseDatos.query(Pasajero.NOMBRE_TABLA, columnas, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }

        int usuario = c.getColumnIndexOrThrow(Pasajero.P_USUARIO);
        int nombre = c.getColumnIndexOrThrow(Pasajero.P_NOMBRE);
        int correo = c.getColumnIndexOrThrow(Pasajero.P_CORREO);
        int password = c.getColumnIndexOrThrow(Pasajero.P_PASSWORD);

        c.moveToFirst();

        String[] x = new String[]{
                c.getString(usuario),
                c.getString(nombre),
                c.getString(correo),
                c.getString(password)
        };

        return x;
    }


    public String getReciente(String id) {
        String columnas[] = new String[]{
                Recientess.R_ID,
                Recientess.R_NOMBRE,
                Recientess.R_LATITUD,
                Recientess.R_LONGITUD
        };

        Cursor c = this.nBaseDatos.query(Recientess.NOMBRE_TABLA, columnas, Recientess.R_ID + "=\'" + id + "\'", null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }

        int id_ = c.getColumnIndexOrThrow(Recientess.R_ID);
        int nombre = c.getColumnIndexOrThrow(Recientess.R_NOMBRE);
        int latitud = c.getColumnIndexOrThrow(Recientess.R_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Recientess.R_LONGITUD);
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
                Paradero.PA_ID,
                Paradero.PA_NOMBRE,
                Paradero.PA_DIRECCION,
                Paradero.PA_LATITUD,
                Paradero.PA_LONGITUD
        };

        if (tipo.equals("LR")) {
            return getTodosRecientes();
        }

        Cursor c = this.nBaseDatos.query(Paradero.NOMBRE_TABLA, columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }
        ArrayList<String> res = new ArrayList<String>();

        int id = c.getColumnIndexOrThrow(Paradero.PA_ID);
        int nombre = c.getColumnIndexOrThrow(Paradero.PA_NOMBRE);
        int direccion = c.getColumnIndexOrThrow(Paradero.PA_DIRECCION);
        int latitud = c.getColumnIndexOrThrow(Paradero.PA_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Paradero.PA_LONGITUD);

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
                Recientess.R_ID,
                Recientess.R_NOMBRE,
                Recientess.R_DIRECCION,
                Recientess.R_LATITUD,
                Recientess.R_LONGITUD
        };

        Cursor c = this.nBaseDatos.query(Recientess.NOMBRE_TABLA, columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }
        ArrayList<String> res = new ArrayList<String>();

        int id = c.getColumnIndexOrThrow(Recientess.R_ID);
        int nombre = c.getColumnIndexOrThrow(Recientess.R_NOMBRE);
        int direccion = c.getColumnIndexOrThrow(Recientess.R_DIRECCION);
        int latitud = c.getColumnIndexOrThrow(Recientess.R_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Recientess.R_LONGITUD);

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
                Paradero.PA_ID,
                Paradero.PA_NOMBRE,
                Paradero.PA_DIRECCION,
                Paradero.PA_LATITUD,
                Paradero.PA_LONGITUD
        };

        Cursor c = this.nBaseDatos.query(Paradero.NOMBRE_TABLA, columnas, Paradero.PA_ID + "=\'" + id + "\'", null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }

        int id_ = c.getColumnIndexOrThrow(Paradero.PA_ID);
        int nombre = c.getColumnIndexOrThrow(Paradero.PA_NOMBRE);
        int direccion = c.getColumnIndexOrThrow(Paradero.PA_DIRECCION);
        int latitud = c.getColumnIndexOrThrow(Paradero.PA_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Paradero.PA_LONGITUD);

        c.moveToFirst();

        //Separo el id del nombre ocn un simbolo &
        return c.getString(id_) + "&" +
                c.getString(nombre) + "&" +
                c.getString(direccion) + "&" +
                c.getString(latitud) + "&" +
                c.getString(longitud);
    }

    public ArrayList<LatLng> getWaypointsByRuta(String id_ruta) {
        String columnas[] = new String[]{
                Waypoints.WAY_LATITUD,
                Waypoints.WAY_LONGITUD
        };
        String orderBy = Waypoints.WAY_CONSECUTIVO + " ASC";

        Cursor c = this.nBaseDatos.query(
                Waypoints.NOMBRE_TABLA,
                columnas,
                Waypoints.WAY_ID_RUTA + "=\'" + id_ruta + "\'",
                null, null, null, orderBy
        );

        if (c.getCount() == 0) {
            return null;
        }

        int latitud = c.getColumnIndexOrThrow(Waypoints.WAY_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Waypoints.WAY_LONGITUD);

        ArrayList<LatLng> res = new ArrayList<LatLng>();
        int i = 0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(), i++) {
            res.add(new LatLng(c.getDouble(latitud), c.getDouble(longitud)));
        }

        return res;

    }

    public String[] getTodosWaypoints() {

        String columnas[] = new String[]{
                Waypoints.WAY_ID_RUTA,
                Waypoints.WAY_CONSECUTIVO,
                Waypoints.WAY_LATITUD,
                Waypoints.WAY_LONGITUD
        };

        Cursor c = this.nBaseDatos.query(Waypoints.NOMBRE_TABLA, columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }

        int id_ruta = c.getColumnIndexOrThrow(Waypoints.WAY_ID_RUTA);
        int consecutivo = c.getColumnIndexOrThrow(Waypoints.WAY_CONSECUTIVO);
        int latitud = c.getColumnIndexOrThrow(Waypoints.WAY_LATITUD);
        int longitud = c.getColumnIndexOrThrow(Waypoints.WAY_LONGITUD);

        String res[] = new String[c.getCount()];
        int i = 0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(), i++) {
            res[i] = c.getString(id_ruta) + "&" + c.getString(consecutivo)+ "&" + c.getString(latitud) + "&" + c.getString(longitud);
        }

        return res;

    }
}


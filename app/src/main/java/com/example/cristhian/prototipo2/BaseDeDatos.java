package com.example.cristhian.prototipo2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by MAIS on 11/07/2015.
 */
public class BaseDeDatos {


    private DBHelper nDBHelper;
    private Context contexto;
    SQLiteDatabase nBaseDatos;


    public BaseDeDatos(Context contexto) {
        this.contexto = contexto;
    }

    public BaseDeDatos abrir() {
        this.nDBHelper = new DBHelper(this.contexto);
        this.nBaseDatos = this.nDBHelper.getWritableDatabase();
        return this;
    }

    public void cerrar() {
        this.nDBHelper.close();
    }


    public Cursor getDatosUsuario() {

        String columnas[] = new String[]{
                "usuario",
                "nombre",
                "correo",
                "password"
        };

        Cursor c = this.nBaseDatos.query("pasajero", columnas, null, null, null, null, null);
        Log.i("prueba", c.getCount()+"");
        if (c.getCount() == 0) {
            return null;

        }

        return c;

    }

    public void setNoActualizacion(String usuario) {

    }

    /**
     * metodo que permite copiar los datos a la base de datos local, por medio de un JSON
     * @param s es la cadena con el contenido de las tabals en formato JSON
     */
    public void duplicarEnLocal(String s) {
        Log.i("prueba", s);
        insertarTablaPasajero(s);
        insertarTablaRuta(s);
        insertarTablaParadero(s);
        insertarTablaParaderoxRuta(s);
        insertarTablaBus(s);
        insertarTablaPasajeroxBus(s);
    }


    /*
    ========================================================
    PARSEANDO LOS DATOS JSON PARA PODER INSERTARLOS EN LA BD
    =========================================================
     */

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
                String latitud = paraderos.getJSONObject(i + "").getString("latitud");
                String longitud = paraderos.getJSONObject(i + "").getString("longitud");
                this.insertarParadero(id_paradero, nombre, latitud, longitud);
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


    private void insertarTablaPasajeroxBus(String s) {
        try {

            JSONObject objetoPapa = new JSONObject(s);
            JSONObject pasajerosxbuses = objetoPapa.getJSONObject("6");
            for (int i = 0; i < pasajerosxbuses.length(); i++) {

                //va a insertar si solo si, el usuario que esta en el registro de pasajeroxbus es el usuario que tenemos aca, si no, nos
                //va a generar error a la hora de insertar usuarios diferentes al que tenemos
                if(pasajerosxbuses.getJSONObject(i+"").getString("usuario").equals(objetoPapa.getJSONObject("1").getJSONObject("0").getString("usuario"))) {
                    String usuario = pasajerosxbuses.getJSONObject(i + "").getString("usuario");
                    String placa = pasajerosxbuses.getJSONObject(i + "").getString("placa");
                    String fecha = pasajerosxbuses.getJSONObject(i + "").getString("fecha");
                    this.insertarPasajeroxBus(usuario, placa, fecha);
                }

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

    private void insertarParadero(String id_paradero, String nombre, String latitud, String longitud) {
        ContentValues values = new ContentValues();

        values.put("id", id_paradero);
        values.put("nombre", nombre);
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

    private void insertarPasajeroxBus(String usuario, String placa, String fecha) {

        ContentValues values = new ContentValues();

        values.put("usuario", usuario);
        values.put("placa", placa);
        values.put("fecha", fecha);

        this.nBaseDatos.insert("pasajeroxbus", null, values);
    }



     /*
    =================================
                GETTERS
    =================================
     */


    public String getRecientes() {
        String columnas[] = new String[]{
                "id",
                "nombre"
        };

        Cursor c = this.nBaseDatos.query("recientes", columnas, null, null,  null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }
        String res = "";

        int id = c.getColumnIndexOrThrow("id");
        int nombre = c.getColumnIndexOrThrow("nombre");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            res += c.getString(id) + "$" + c.getString(nombre) + ",";
        }

        return res;

    }

    public String getParaderos() {
        String columnas[] = new String[]{
                "id",
                "nombre"
        };

        Cursor c = this.nBaseDatos.query("paradero", columnas, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }
        String res = "";

        int id = c.getColumnIndexOrThrow("id");
        int nombre = c.getColumnIndexOrThrow("nombre");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            res += c.getString(id) + "&" + c.getString(nombre) + ",";
        }

        return res;

    }
}


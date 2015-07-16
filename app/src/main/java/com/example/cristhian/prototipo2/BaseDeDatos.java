package com.example.cristhian.prototipo2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MAIS on 11/07/2015.
 */
public class BaseDeDatos {


    private DBHelper nDBHelper;
    private Context contexto;
    SQLiteDatabase nBaseDatos;
    private String paraderos;

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


    /**
     * estos metodos son falsos xD
     * solo son de prueba
     *
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public void insertar(String a, String b, String c, String d) {
        ContentValues values = new ContentValues();

        values.put("usuario", "mibobrek");
        values.put("nombre", "maite");
        values.put("correo", "correo");
        values.put("password", "1234");
        values.put("actualizacion", "no");

        this.nBaseDatos.insert("pasajero", null, values);
    }


    /**
     * junto con este :P
     *
     * @return
     */
    public void consulta() {
        String columnas[] = new String[]{
                "usuario",
                "nombre"
        };
        Cursor c = this.nBaseDatos.query("pasajero", columnas, null, null, null, null, null, null);
        String resultado = "";

        int iNit = c.getColumnIndex("usuario");
        int iNombre = c.getColumnIndex("nombre");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            resultado += c.getString(iNit) + c.getString(iNombre) + "\n";
        }

        //Log.i("logconsulta", resultado);

    }

    public Cursor getDatosUsuario() {

        String columnas[] = new String[]{
                "usuario",
                "nombre",
                "correo",
                "password",
                "actualizacion"
        };

        Cursor c = this.nBaseDatos.query("pasajero", columnas, null, null, null, null, null);
        if (c.getCount() == 0) {
            return null;
        }

        return c;

    }

    public void setNoActualizacion(String usuario) {

    }

    public void duplicarEnLocal(String s) {
        insertarTablaUsuario(s);
        //insertarTabla
    }

    public ArrayList<String> insertarTablaUsuario(String response){
        ArrayList<String> listado= new ArrayList<String>();
        try {

            JSONObject objetoPapa = new JSONObject(response);
            JSONObject lista = objetoPapa.getJSONObject("1");
            JSONObject detallesUsuario = lista.getJSONObject("0");
            this.insertarUsuario(
                    detallesUsuario.getString("usuario"),
                    detallesUsuario.getString("nombre"),
                    detallesUsuario.getString("correo"),
                    detallesUsuario.getString("password"),
                    detallesUsuario.getString("actualizacion")
            );

        } catch (Exception e) {
            Log.i("cm01", "error insertando:: " + e.toString());
        }

        return listado;
    }

    private void insertarUsuario(String usuario, String nombre, String correo, String password, String actualizacion) {
        ContentValues values = new ContentValues();

        values.put("usuario", usuario);
        values.put("nombre", nombre);
        values.put("correo", correo);
        values.put("password", password);
        values.put("actualizacion", actualizacion);

        this.nBaseDatos.insert("pasajero", null, values);
    }


    public String getRecientes(String user) {
        String columnas[] = new String[]{
                "id",
                "nombre"
        };

        Cursor c = this.nBaseDatos.query("recientes", columnas, "usuario = '" + user + "'", null, null, null, null);

        if (c.getCount() == 0) {
            return "";
        }
        String res = "";

        int id = c.getColumnIndexOrThrow("usuario");
        int nombre = c.getColumnIndexOrThrow("correo");

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

        Cursor c = this.nBaseDatos.query("recientes", columnas, null, null, null, null, null, null);

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


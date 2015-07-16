package com.example.cristhian.prototipo2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by MAIS on 11/07/2015.
 */
public class BaseDeDatos {


    private DBHelper nDBHelper;
    private Context contexto;
    SQLiteDatabase nBaseDatos;

    public BaseDeDatos(Context contexto){
        this.contexto = contexto;
    }

    public BaseDeDatos abrir(){
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
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public void insertar(String a, String b, String c, String d){
        ContentValues values = new ContentValues();

        values.put("nit", a);
        values.put("nombre", b);
        values.put("direccion", c);
        values.put("telefono", d);

        this.nBaseDatos.insert("empresa", null, values);
    }





    /**
     * junto con este :P
     * @return
     */
    public String consulta() {
        String columnas [] = new String[]{"nit", "nombre"};
        Cursor c = this.nBaseDatos.query("empresa", columnas, null, null,null,null, null, null);
        String resultado = "";

        int iNit = c.getColumnIndex("nit");
        int iNombre = c.getColumnIndex("nombre");

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            resultado += c.getString(iNit) + c.getString(iNombre) + "\n";
        }

        return resultado;


    }

    public Cursor getDatosUsuario() {

        String columnas [] = new String[]{
                "usuario",
                "nombre",
                "correo",
                "password",
                "actualizacion"
        };
        Cursor c = this.nBaseDatos.query("pasajero", columnas, null, null,null,null, null, null);
        if(c.getCount() == 0){
            return null;
        }

        return c;

    }
}


package com.example.cristhian.prototipo2;

import android.content.Context;
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



}

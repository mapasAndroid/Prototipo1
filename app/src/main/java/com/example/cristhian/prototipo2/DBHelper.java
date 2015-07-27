package com.example.cristhian.prototipo2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cristhian.prototipo2.StopBusContract.*;

/**
 * Created by CRISTHIAN and MAITE
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "stopbus.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CREATE_EMPRESA = "CREATE TABLE " + Empresa.NOMBRE_TABLA + "(" +
            Empresa.E_NIT + " TEXT PRIMARY KEY, " +
            Empresa.E_NOMBRE + " TEXT NOT NULL, " +
            Empresa.E_DIRECCION + " TEXT NOT NULL, " +
            Empresa.E_TELEFONO + " TEXT NOT NULL );";


    public static final String CREATE_PASAJERO = "CREATE TABLE " + Pasajero.NOMBRE_TABLA + "(" +
            Pasajero.P_USUARIO + " TEXT PRIMARY KEY, " +
            Pasajero.P_NOMBRE + " TEXT NOT NULL, " +
            Pasajero.P_CORREO + " TEXT NOT NULL, " +
            Pasajero.P_PASSWORD + " TEXT NOT NULL); ";

    public static final String CREATE_RECIENTES = "CREATE TABLE " + Recientess.NOMBRE_TABLA + "(" +
            Recientess.R_ID + " TEXT PRIMARY KEY, " +
            Recientess.R_NOMBRE + " TEXT NOT NULL, " +
            Recientess.R_DIRECCION + " TEXT NOT NULL, " +
            Recientess.R_LATITUD + " TEXT NOT NULL, " +
            Recientess.R_LONGITUD + " TEXT NOT NULL);" ;


    public static final String CREATE_RUTAS = "CREATE TABLE " + Ruta.NOMBRE_TABLA + "(" +
            Ruta.RU_ID_RUTA + " TEXT PRIMARY KEY, " +
            Ruta.RU_NOMBRE + " TEXT NOT NULL " + ");";

    public static final String CREATE_PARADERO = "CREATE TABLE " + Paradero.NOMBRE_TABLA + "(" +
            Paradero.PA_ID + " TEXT PRIMARY KEY, " +
            Paradero.PA_NOMBRE + " TEXT NOT NULL, " +
            Paradero.PA_DIRECCION + " TEXT NOT NULL, " +
            Paradero.PA_LATITUD + " TEXT NOT NULL, " +
            Paradero.PA_LONGITUD + " TEXT NOT NULL );";

    public static final String CREATE_PARADEROXRUTA = "CREATE TABLE " + ParaderoxRuta.NOMBRE_TABLA + "(" +
            ParaderoxRuta.PR_ID_PARADERO + " TEXT NOT NULL, " +
            ParaderoxRuta.PR_ID_RUTA + " TEXT NOT NULL, " +
            "PRIMARY KEY(" + ParaderoxRuta.PR_ID_PARADERO + "," + ParaderoxRuta.PR_ID_RUTA + "),"+
            "FOREIGN KEY ("+ParaderoxRuta.PR_ID_PARADERO+") REFERENCES "+ Paradero.NOMBRE_TABLA+"("+Paradero.PA_ID+"),"+
            "FOREIGN KEY ("+ParaderoxRuta.PR_ID_RUTA+") REFERENCES "+ Ruta.NOMBRE_TABLA+"("+Ruta.RU_ID_RUTA+"));";

    public static final String CREATE_BUS = "CREATE TABLE " + Bus.NOMBRE_TABLA + "(" +
            Bus.B_PLACA + " TEXT PRIMARY KEY, " +
            Bus.B_CONDUCTOR + " TEXT NOT NULL, " +
            Bus.B_ID_RUTA + " TEXT NOT NULL, " +
            Bus.B_NIT + " TEXT NOT NULL, " +
            "FOREIGN KEY ("+Bus.B_ID_RUTA+") REFERENCES "+ Ruta.NOMBRE_TABLA+"("+Ruta.RU_ID_RUTA+"),"+
            "FOREIGN KEY ("+Bus.B_NIT+") REFERENCES "+ Empresa.NOMBRE_TABLA+"("+Empresa.E_NIT+"));";

    public static final String CREATE_PASAJEROXBUS = "CREATE TABLE " + PasajeroxBus.NOMBRE_TABLA + "(" +
            PasajeroxBus.PB_USUARIO + " TEXT NOT NULL, " +
            PasajeroxBus.PB_PLACA + " TEXT NOT NULL, " +
            PasajeroxBus.PB_FECHA + " TEXT NOT NULL, " +
            "PRIMARY KEY(" + PasajeroxBus.PB_USUARIO + "," + PasajeroxBus.PB_PLACA + "," + PasajeroxBus.PB_FECHA + "),"+
            "FOREIGN KEY ("+PasajeroxBus.PB_USUARIO + ") REFERENCES "+ Pasajero.NOMBRE_TABLA +"("+Pasajero.P_USUARIO+"),"+
            "FOREIGN KEY ("+PasajeroxBus.PB_PLACA+") REFERENCES "+ Bus.NOMBRE_TABLA+"("+Bus.B_PLACA+"));";



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(CREATE_EMPRESA);
        database.execSQL(CREATE_PASAJERO);
        database.execSQL(CREATE_RECIENTES);
        database.execSQL(CREATE_RUTAS);
        database.execSQL(CREATE_PARADERO);
        database.execSQL(CREATE_PARADEROXRUTA);
        database.execSQL(CREATE_BUS);
        database.execSQL(CREATE_PASAJEROXBUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Empresa.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + Pasajero.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + Recientess.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + Ruta.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + Paradero.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + ParaderoxRuta.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + Bus.NOMBRE_TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + PasajeroxBus.NOMBRE_TABLA);

        onCreate(db);

    }

}
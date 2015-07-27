package com.example.cristhian.prototipo2;

import android.provider.BaseColumns;

/**
 * Created by CRISTHIAN and MAITE
 */
public class StopBusContract {

    public static final class Empresa implements BaseColumns {
        public static final String NOMBRE_TABLA = "empresa";
        public static final String E_NIT = "nit";
        public static final String E_NOMBRE = "nombre";
        public static final String E_DIRECCION = "direccion";
        public static final String E_TELEFONO = "telefono";
    }

    public static final class Pasajero implements BaseColumns {
        public static final String NOMBRE_TABLA = "pasajero";
        public static final String P_USUARIO = "usuario";
        public static final String P_NOMBRE = "nombre";
        public static final String P_CORREO = "correo";
        public static final String P_PASSWORD = "password";

    }

    public static final class Recientess implements BaseColumns {
        public static final String NOMBRE_TABLA = "recientes";
        public static final String R_ID = "id";
        public static final String R_NOMBRE = "nombre";
        public static final String R_DIRECCION = "direccion";
        public static final String R_LATITUD = "latitud";
        public static final String R_LONGITUD = "longitud";

    }

    public static final class Ruta implements BaseColumns {
        public static final String NOMBRE_TABLA = "ruta";
        public static final String RU_ID_RUTA = "id_ruta";
        public static final String RU_NOMBRE = "nombre";


    }

    public static final class Paradero implements BaseColumns {
        public static final String NOMBRE_TABLA = "paradero";
        public static final String PA_ID = "id";
        public static final String PA_NOMBRE = "nombre";
        public static final String PA_DIRECCION = "direccion";
        public static final String PA_LATITUD = "latitud";
        public static final String PA_LONGITUD = "longitud";

    }

    public static final class ParaderoxRuta implements BaseColumns {
        public static final String NOMBRE_TABLA = "paraderoxruta";
        public static final String PR_ID_PARADERO = "id_paradero";
        public static final String PR_ID_RUTA = "id_ruta";


    }

    public static final class Bus implements BaseColumns {
        public static final String NOMBRE_TABLA = "bus";
        public static final String B_PLACA = "placa";
        public static final String B_CONDUCTOR = "conductor";
        public static final String B_ID_RUTA = "id_ruta";
        public static final String B_NIT = "nit";

    }

    public static final class PasajeroxBus implements BaseColumns {
        public static final String NOMBRE_TABLA = "pasajeroxbus";
        public static final String PB_USUARIO = "usuario";
        public static final String PB_PLACA = "placa";
        public static final String PB_FECHA = "fecha";

    }



}

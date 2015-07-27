package com.example.cristhian.prototipo2;

/**
 * Created by CRISTHIAN and MAITE
 */
public class ItemParaderos {
    private String nombreParadero;
    private String idParadero;
    private String direccion;
    private String latitud;
    private String longitud;
    private boolean esReciente;

    public ItemParaderos(String paradero) {
        String v [] = paradero.split("&");
        this.idParadero = v[0];
        this.nombreParadero = v[1];
        this.direccion = v[2];
        this.latitud = v[3];
        this.longitud = v[4];
        this.esReciente = false;
        if(v[5].equals("si")){
            this.esReciente = true;
        }
    }

    public String getNombreParadero() {
        return nombreParadero;
    }

    public String getIdParadero() {
        return idParadero;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getLatLong() {
        return this.getLatitud() +" , " + this.getLongitud();
    }

    public String getDireccion(){
        return this.direccion;
    }

    public boolean esReciente() {
        return esReciente;
    }
}

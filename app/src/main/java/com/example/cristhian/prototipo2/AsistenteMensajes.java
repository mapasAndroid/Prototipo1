package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;

/**
 * Created by CRISTHIAN and MAITE
 */
public class AsistenteMensajes {

    DialogFragment2 mensajeria;

    public AsistenteMensajes(){
        mensajeria = new DialogFragment2();

    }

    /**
     * metodo decorador de el metodo imprimir de el dialogfragment2
     * @param fm fragment manager de donde fue llamado
     * @param mensaje mensaje a imprimir
     * @param bnd bandera de donde fue llamado
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void imprimir(FragmentManager fm , String mensaje, int bnd){
        //bnd 1: para vista principal, 2: para registro.. etc
        mensajeria.setMensaje(mensaje);
        mensajeria.setTipo(bnd);
        mensajeria.show(fm, mensaje);
    }


}

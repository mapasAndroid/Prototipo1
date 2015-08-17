package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;

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
        // 1: para vista principal
        // 2: para registro
        //3: para lugares
        //4: para fragmento basico
        mensajeria.setMensaje(mensaje);
        mensajeria.setTipo(bnd);
        mensajeria.show(fm, mensaje);

    }


}

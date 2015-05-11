package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;

/**
 * Created by cristhian on 5/10/15.
 */
public class AsistenteMensajes {

    DialogFragment2 mensajeria;

    public AsistenteMensajes(){
        mensajeria = new DialogFragment2();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void imprimir(FragmentManager fm , String mensaje, int bnd){
        //bnd 1: para vista principal, 2: para registro.. etc
        mensajeria.setMensaje(mensaje);
        mensajeria.setTipo(bnd);
        mensajeria.show(fm, mensaje);
    }

}

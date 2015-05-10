package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by cristhian on 5/10/15.
 */
public class AsistenteMensajes {

    DialogFragment2 mensajeria;

    public AsistenteMensajes(){
        mensajeria = new DialogFragment2();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void imprimir(FragmentManager fm , String mensaje){
        mensajeria.setMensaje(mensaje);
        mensajeria.show(fm, mensaje);
    }

}

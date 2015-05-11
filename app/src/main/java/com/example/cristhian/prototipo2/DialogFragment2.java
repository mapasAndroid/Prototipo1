package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by cristhian on 5/10/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DialogFragment2 extends DialogFragment{


    String mensaje;
    int tipo;

    public DialogFragment2(){
        this.mensaje = "";
    }

    public void setMensaje(String nuevo){
        this.mensaje = nuevo;
    }

    public void setTipo(int id){
        this.tipo = id;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mensaje)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hagaAlgo();
                    }

                    //filtra de que vista viene, segun el entero
                    private void hagaAlgo() {
                        switch (tipo){
                            case 0:
                                break;
                            case 1:
                                EditText texto = (EditText) getActivity().findViewById(R.id.editTextContrasenia);
                                texto.setText("");
                                break;
                            case 2:
                                break;

                        }
                    }
                });
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });*/
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

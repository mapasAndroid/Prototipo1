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
    public DialogFragment2(){
        this.mensaje = "";
    }

    public void setMensaje(String nuevo){
        this.mensaje = nuevo;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mensaje)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText x = (EditText) getActivity().findViewById(R.id.editTextContrasenia);
                        x.setText("");
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

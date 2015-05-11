package com.example.cristhian.prototipo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by MAIS on 07/05/2015.
 */

public class Registro extends ActionBarActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

    }


    public void Enviar(View view) {

        EditText usuario = (EditText)findViewById(R.id.editTextUsuario1);
        EditText nombre = (EditText)findViewById(R.id.editTextUsuario2);
        EditText correo = (EditText)findViewById(R.id.editTextUsuario3);
        EditText pass = (EditText)findViewById(R.id.editTextUsuario4);

        Intent i = new Intent(Registro.this, Inicio.class);
        startActivity(i);
    }


}

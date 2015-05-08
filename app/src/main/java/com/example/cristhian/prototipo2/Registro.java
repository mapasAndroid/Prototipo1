package com.example.cristhian.prototipo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

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

        Toast toast = Toast.makeText(this, "Usted ha sido registrado.",Toast.LENGTH_LONG);
        toast.show();
        Intent i = new Intent(Registro.this, Inicio.class);
        startActivity(i);
    }
}

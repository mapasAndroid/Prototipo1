package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by MAIS on 07/05/2015.
 */

public class Registro extends ActionBarActivity{


    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_registro);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        //authButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        /*
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
         */

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

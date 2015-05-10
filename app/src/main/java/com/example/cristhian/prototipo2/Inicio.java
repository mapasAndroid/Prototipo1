package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class Inicio extends ActionBarActivity {

    AsistenteMensajes asistente = new AsistenteMensajes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void IniciarSesion(View view) throws NoSuchAlgorithmException {

        //extrae los datos de la vista
        EditText usuario = (EditText)findViewById(R.id.editTextUsuario);
        EditText pass = (EditText)findViewById(R.id.editTextContrasenia);

        //si tiene campos vacios
        if(usuario.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
            asistente.imprimir(getFragmentManager(), "Campos vacios, verifique nuevamente");
            return;
        }
        //valida que exista en la base de datos
        Validador validador = new Validador();
        validador.execute(usuario.getText().toString(), sha1(pass.getText().toString()));


    }

    public void Registrarse(View view) {
        Intent i = new Intent(Inicio.this, Registro.class);
        startActivity(i);
    }

    public class Validador extends AsyncTask<String, String, String>{

        @Override
        protected void onPostExecute(String s) {
            //si la respuesta del servidor es vacia, avise al usuario
            if(s.equals("0")){
                asistente.imprimir(getFragmentManager(), "Usuario o contraseña no coinciden");
            }else{
                //si existe en la base de datos, sigue a la vista principal
                Intent i = new Intent(Inicio.this, Sitios.class);
                //se lleva el nombre de usuario para nombrarlo mas adelante
                i.putExtra("usuario",s);
                startActivity(i);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return enviarPost(params[0], params[1]);
        }

        public String enviarPost(String usuario, String contrasenia) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(
                    "http://pruebasleon.zz.mu/validarUsuario.php");//http://pruebasleon.zz.mu/
            HttpResponse response = null;
            String resultado = "";
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(3);
                params.add(new BasicNameValuePair("usuario", usuario));
                params.add(new BasicNameValuePair("contrasenia", contrasenia));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                response = httpClient.execute(httpPost, localContext);
                HttpEntity httpEntity = response.getEntity();
                resultado = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                Log.d("MyApp", e.toString());
            }
            return resultado;

        }
    }

}

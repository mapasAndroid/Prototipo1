package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Inicio extends ActionBarActivity {

    /**
     * ========== VARIABLES ===========
     */

    //Atributo que pinta los mensajes en la pantalla
    private AsistenteMensajes asistente = new AsistenteMensajes();
    //Atributo que encripta las contraseñas en sha1
    private Encriptador encriptador = new Encriptador();
    //Atributo que gestiona las conexiones a datos web
    private WebHelper webHelper = new WebHelper();
    //Ventana de espera para el usuario
    protected ProgressDialog progres;

    Typeface roboto;

    public static Activity cerrarInicio;


    /**
     * METODO PRINCIPAL ONCREATE
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        this.cerrarInicio = this;
        progres = new ProgressDialog(this);

        //ocultar barra de iconos
        getSupportActionBar().hide();

        //cambiar el tipo de letra a DEFAULT al edit text contraseña
        EditText password = (EditText) findViewById(R.id.editTextContrasenia);
        password.setTypeface(Typeface.DEFAULT);


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


    /**
     * METODO QUE INICIA SESION CONECTANDOSE CON UN ARCHIVO WEB
     *
     * @param view
     * @throws NoSuchAlgorithmException
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void IniciarSesion(View view) throws NoSuchAlgorithmException {


        //extrae los datos de la vista
        String usuario = ((EditText) findViewById(R.id.editTextUsuario)).getText().toString();
        String pass = ((EditText) findViewById(R.id.editTextContrasenia)).getText().toString();

        //si tiene campos vacios notifiquele al usuario
        if (usuario.isEmpty() || pass.isEmpty()) {
            asistente.imprimir(getFragmentManager(), "Campos vacios, verifique nuevamente", 1);
            return;
        }

        Validador validador = new Validador();
        validador.execute(usuario, this.encriptador.getSha1(pass));

    }

    public void Registrarse(View view) {
        startActivity(new Intent(Inicio.this, Registro.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    public class Validador extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //barra de progreso
            progres.setMessage("Iniciando Sesion...");
            progres.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progres.setIndeterminate(true);
            progres.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            //oculta la barra de progreso
            progres.hide();

            //si la respuesta del servidor es 0, avise al usuario
            if (s.equals("0")) {
                asistente.imprimir(getFragmentManager(), "Usuario o contraseña no coinciden", 1);
                return;
            }
            if (s.equals("nonet")) {
                asistente.imprimir(getFragmentManager(), "Error de red, verifica tu conexion", 1);
                return;
            }

            Log.i("cm01", "respuesta servidor inicio::: " + s);

            String datos [] = s.split("&");
            Intent activityLugares = new Intent(Inicio.this, Lugares.class);
            Log.i("cm01", "usuario::: " + datos[0]);
            new Copia().copiarDatos(Inicio.this.getBaseContext(), datos[0]);
            activityLugares.putExtra("usuario", datos[0]);
            activityLugares.putExtra("correo", datos[1]);
            activityLugares.putExtra("desde", "inicio");
            startActivity(activityLugares);
            Inicio.this.finish();


        }

        @Override
        protected String doInBackground(String... datosUsuario) {
            return enviarPost(datosUsuario[0], datosUsuario[1]);
        }

        public String enviarPost(String usuario, String contrasenia) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(
                    webHelper.getUrl() + webHelper.getUrlIniciarSesion());
            HttpResponse response = null;
            String resultado = "";
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                params.add(new BasicNameValuePair("usuario", usuario));
                params.add(new BasicNameValuePair("contrasenia", contrasenia));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                response = httpClient.execute(httpPost, localContext);
                HttpEntity httpEntity = response.getEntity();
                resultado = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                resultado = "nonet";
            }
            return resultado;

        }
    }

}

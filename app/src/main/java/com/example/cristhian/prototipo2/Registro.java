package com.example.cristhian.prototipo2;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CRISTHIAN and MAITE
 */
public class Registro extends ActionBarActivity{

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

    private Inicio inicio = new Inicio();

    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

        this.progres = new ProgressDialog(this);

        this.checkBox = (CheckBox) findViewById(R.id.checkBox_registro);

        String checkBoxText = "Acepto los <a href='http://pruebasmais.zz.mu/stopbus/terminos_y_condiciones.php' > Terminos y condiciones</a>";
        TextView text_view_terminos_condiciones = (TextView) findViewById(R.id.text_view_terminos_condiciones);
        text_view_terminos_condiciones.setText(Html.fromHtml(checkBoxText));
        text_view_terminos_condiciones.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void Enviar(View view) {

        String usuario = ((EditText)findViewById(R.id.editTextUsuario1)).getText().toString();
        String nombre = ((EditText)findViewById(R.id.editTextUsuario2)).getText().toString();
        String correo = ((EditText)findViewById(R.id.editTextUsuario3)).getText().toString();
        String pass = ((EditText)findViewById(R.id.editTextUsuario4)).getText().toString();
        String telef = ((EditText)findViewById(R.id.editTextUsuario5)).getText().toString();

        if(usuario.isEmpty() || nombre.isEmpty()
                || correo.isEmpty() || pass.isEmpty()|| telef.isEmpty()){
            asistente.imprimir(getFragmentManager(), "Campos vacios, verifica nuevamente", 2);
            return;
        }

        if(!this.checkBox.isChecked()){
            asistente.imprimir(getFragmentManager(), "Recuerda que debes aceptar los terminos y condiciones", 2);
            return;
        }


        Registrador registrador = new Registrador();
        registrador.execute(usuario, nombre, correo, encriptador.getSha1(pass),telef);

    }

    public class Registrador extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {

            //oculta la barra de progreso
            progres.hide();


            //si la respuesta del servidor es 0, avise al usuario
            if (s.equals("0")) {
                asistente.imprimir(getFragmentManager(), "Ocurrio algun error en el registro, intentalo nuevamente", 2);
                return;
            }
            if (s.equals("nonet")) {
                asistente.imprimir(getFragmentManager(), "Error de red, verifica tu conexion", 2);
                return;
            }

            if(s.equals("email_duplicado")){
                asistente.imprimir(getFragmentManager(), "Email ya existe", 2);
                return;
            }

            if(s.equals("usuario_duplicado")){
                asistente.imprimir(getFragmentManager(), "Usuario ya existe", 2);
                return;
            }

            String datos [] = s.split("&");
            Intent activityLugares= new Intent(Registro.this, Lugares.class);
            new Copia().copiarDatos(Registro.this.getBaseContext(), datos[0]);
            activityLugares.putExtra("usuario", datos[0]);
            activityLugares.putExtra("correo", datos[1]);
            activityLugares.putExtra("desde", "registro");
            startActivity(activityLugares);
            finish();
            inicio.cerrarInicio.finish();

        }

        @Override
        protected void onPreExecute() {
            //barra de progreso
            progres.setMessage("Procesando...");
            progres.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progres.setIndeterminate(true);
            progres.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return enviarPost(params[0], params[1], params[2], params[3],params[4]);
        }

        public String enviarPost(String usuario, String nombre, String correo, String pass,String telefono) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(
                    webHelper.getUrl() + webHelper.getUrlRegistrarUsuario());
            HttpResponse response = null;
            String resultado = "";
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("usuario", usuario));
                params.add(new BasicNameValuePair("nombre", nombre));
                params.add(new BasicNameValuePair("correo", correo));
                params.add(new BasicNameValuePair("contrasenia", pass));
                params.add(new BasicNameValuePair("telefono", telefono));
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

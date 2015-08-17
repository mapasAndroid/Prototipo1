package com.example.cristhian.prototipo2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class Copia {

    /**
     * ========== VARIABLES ===========
     */

    //Atributo que encripta las contrasenias en sha1
    private Encriptador encriptador = new Encriptador();
    //Atributo que gestiona las conexiones a datos web
    private WebHelper webHelper = new WebHelper();

    Context contexto;


    /**
     * metodo que permite bajar todos los datos de la bd de la web
     */
    public void copiarDatos(Context contexto, String usuario) {
        this.contexto = contexto;
        new Copiado().execute(usuario);
    }

    public class Copiado extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            if (s.isEmpty() || s.equals("nonet")) return;
            BaseDeDatos baseDeDatos = new BaseDeDatos(contexto);
            baseDeDatos.abrir();
            baseDeDatos.duplicarEnLocal(s);
            baseDeDatos.cerrar();
        }

        protected String doInBackground(String... params) {
            return copiarDatosweb(params[0]);
        }

        public String copiarDatosweb(String usuario) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(webHelper.getUrl() + webHelper.getUrlCopiarDatos());
            HttpResponse response = null;
            String resultado = "";
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("usuario", webHelper.getUsuario()));
                params.add(new BasicNameValuePair("contrasenia", webHelper.getPass()));
                params.add(new BasicNameValuePair("nombreUsuario", usuario));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                response = httpClient.execute(httpPost, localContext);
                HttpEntity httpEntity = response.getEntity();
                resultado = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (Exception e) {
                Log.i("cm01", e.toString());
            }
            return resultado;

        }


    }
}

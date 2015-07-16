package com.example.cristhian.prototipo2;

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

/**
 * Created by MAIS on 13/07/15.
 */
public class Copia {

    /**
     * ========== VARIABLES ===========
     */

    //Atributo que encripta las contrasenias en sha1
    private Encriptador encriptador = new Encriptador();
    //Atributo que gestiona las conexiones a datos web
    private WebHelper webHelper = new WebHelper();


    /**
     * metodo que permite bajar todos los datos de la bd de la web
     */
    public void copiarDatos() {
        Copiado copiado = new Copiado();
        copiado.execute(webHelper.getUsuario(), encriptador.getSha1(webHelper.getPass()));
    }

    public class Copiado extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            //aqui debemo sinsertar los datos en la bd local



        }

        protected String doInBackground(String... params) {
            return copiarDatosweb();
        }

        public String copiarDatosweb() {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(webHelper.getUrl() + webHelper.getUrlCopiarDatos());
            HttpResponse response = null;
            String resultado = "";
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("usuario", webHelper.getUsuario()));
                params.add(new BasicNameValuePair("contrasenia", webHelper.getPass()));
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

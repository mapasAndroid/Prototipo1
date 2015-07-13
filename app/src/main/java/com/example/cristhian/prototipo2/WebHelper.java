
package com.example.cristhian.prototipo2;

/**
 * Created by cristhian on 7/12/15.
 */
public class WebHelper {

    private Encriptador encriptador = new Encriptador();

    private final String url = "http://www.pruebasmais.zz.mu/stopbus/";
    private final String usuario = "stopbus";
    private final String pass = this.encriptador.getSha1("stopbus1234");



    private final String urlCopiarDatos = "servicios/copiarDatos.php";
    private final String urlIniciarSesion = "servicios/iniciarSesion.php";
    private final String urlRegistrarUsuario = "servicios/registrarUsuario.php";
    private final String urlInsertarReciente = "servicios/insertarReciente.php";


    public WebHelper(){

    }


    public String getUrl() {
        return url;
    }

    public String getUrlIniciarSesion() {
        return urlIniciarSesion;
    }

    public String getUrlRegistrarUsuario() {
        return urlRegistrarUsuario;
    }
}

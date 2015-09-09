
package com.example.cristhian.prototipo2;

public class WebHelper {

    private Encriptador encriptador = new Encriptador();

    private final String url = "http://sandbox1.ufps.edu.co/~ufps_61/stopbus/";

    private final String usuario = "stopbus";

    private final String pass = this.encriptador.getSha1("stopbus1234");

    private final String urlCopiarDatos = "servicios/copiarDatos.php";

    private final String urlIniciarSesion = "servicios/iniciarSesion.php";

    private final String urlRegistrarUsuario = "servicios/registrarUsuario.php";

    private final String urlBuscarBus = "servicios/buscarBus.php";

    private final String urlAcercaDe = "http://maitebobrek.eshost.com.ar/stopbus/acerca_de.php";

    private String urlContacto = "http://maitebobrek.eshost.com.ar/stopbus/contacto.php";


    public WebHelper(){

    }


    public String getUrl() {return url;}

    public String getUrlIniciarSesion() {return urlIniciarSesion;}

    public String getUsuario() {
        return usuario;
    }

    public String getPass(){return pass; }

    public String getUrlCopiarDatos(){return urlCopiarDatos;}

    public String getUrlRegistrarUsuario() {
        return urlRegistrarUsuario;
    }

    public String getUrlBuscarBus(){
        return urlBuscarBus;
    }

    public String getUrlAcercaDe(){
        return urlAcercaDe;
    }

    public String getUrlContacto() {
        return urlContacto;
    }
}

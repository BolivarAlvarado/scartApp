package Modelo;

/**
 * Clase que representa el resultado de la verificación de una imagen.
 */
public class VerificacionResultado {

    private String urlImagen;  // URL de la imagen verificada
    private String hashNueva;  // Hash de la nueva imagen verificada
    private String hashOriginal;  // Hash de la imagen original (desde la base de datos)
    private boolean esAutentica;  // Resultado de la verificación

    // Constructor vacío requerido por Firebase Realtime Database
    public VerificacionResultado() {
    }

    // Constructor con todos los campos
    public VerificacionResultado(String urlImagen, String hashNueva, String hashOriginal, boolean esAutentica) {
        this.urlImagen = urlImagen;
        this.hashNueva = hashNueva;
        this.hashOriginal = hashOriginal;
        this.esAutentica = esAutentica;
    }

    // Getters y setters
    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getHashNueva() {
        return hashNueva;
    }

    public void setHashNueva(String hashNueva) {
        this.hashNueva = hashNueva;
    }

    public String getHashOriginal() {
        return hashOriginal;
    }

    public void setHashOriginal(String hashOriginal) {
        this.hashOriginal = hashOriginal;
    }

    public boolean isEsAutentica() {
        return esAutentica;
    }

    public void setEsAutentica(boolean esAutentica) {
        this.esAutentica = esAutentica;
    }
}



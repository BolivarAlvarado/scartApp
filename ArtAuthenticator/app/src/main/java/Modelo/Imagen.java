package Modelo;

public class Imagen {
    private String nombre;
    private String rutaImagen;  // Esta URL debería ser la URL pública de Firebase Storage
    private String hashCifrado; // Este es el hash cifrado de la imagen
    private String clavePublica; // Clave pública utilizada para cifrar el hash

    // Constructor vacío requerido por Firebase Realtime Database
    public Imagen() {
    }

    // Constructor con todos los campos
    public Imagen(String nombre, String rutaImagen, String hashCifrado, String clavePublica) {
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.hashCifrado = hashCifrado;
        this.clavePublica = clavePublica;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getHashCifrado() {
        return hashCifrado;
    }

    public void setHashCifrado(String hashCifrado) {
        this.hashCifrado = hashCifrado;
    }

    public String getClavePublica() {
        return clavePublica;
    }

    public void setClavePublica(String clavePublica) {
        this.clavePublica = clavePublica;
    }
}










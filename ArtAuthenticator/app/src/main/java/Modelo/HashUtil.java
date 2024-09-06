package Modelo;

import java.security.MessageDigest;
import java.io.InputStream;
import android.net.Uri;
import android.content.Context;

/**
 * Utilidad para generar un hash SHA-256 a partir de una imagen.
 * Esta clase proporciona métodos para calcular un hash de una imagen
 * basada en su URI utilizando el algoritmo SHA-256.
 */

public class HashUtil{

    /**
     * Genera un hash SHA-256 para una imagen dada su URI.
     *
     * @param context El contexto de la aplicación.
     * @param uriImagen El URI de la imagen para la cual se generará el hash.
     * @return El hash SHA-256 de la imagen como una cadena hexadecimal, o null si ocurre un error.
     */

    public static String generarHash(Context context, Uri uriImagen) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uriImagen);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            inputStream.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


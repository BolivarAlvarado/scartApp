package Modelo;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * Utilidad para manejar operaciones de clave pública y privada utilizando RSA.
 * Proporciona métodos para generar, cargar, encriptar y desencriptar datos usando claves RSA.
 */
public class RsaUtil{

    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * Constructor que inicializa el objeto con una clave pública en formato Base64.
     * @param publicKeyBase64 Clave pública codificada en Base64.
     * @throws Exception Si ocurre algún error al decodificar la clave o generarla.
     */

    public RsaUtil(String publicKeyBase64) throws Exception{
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(keySpec);
    }

    /**
     * Constructor que genera un nuevo par de claves RSA.
     * @throws Exception Si ocurre algún error durante la generación de las claves.
     */
    public RsaUtil() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Tamaño de la clave
        KeyPair pair = keyGen.generateKeyPair(); //Aqui se crea las claves usando RSA
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
    }

    /**
     * Obtiene la clave pública en formato Base64.
     * @return La clave pública codificada en Base64.
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Encripta los datos proporcionados utilizando la clave privada.
     * @param data Los datos que se desean encriptar.
     * @return Los datos encriptados en formato Base64.
     * @throws Exception Si ocurre algún error durante el proceso de encriptación.
     */
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Desencripta los datos proporcionados utilizando la clave pública.
     * @param encryptedData Los datos encriptados en formato Base64.
     * @return Los datos originales desencriptados.
     * @throws Exception Si ocurre algún error durante el proceso de desencriptación.
     */
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

}









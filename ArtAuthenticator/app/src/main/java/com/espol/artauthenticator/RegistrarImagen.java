package com.espol.artauthenticator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Modelo.HashUtil;
import Modelo.Imagen;
import Modelo.RsaUtil;

/**
 * Actividad que permite cargar una imagen, firmarla digitalmente con RSA y
 * almacenarla en Firebase junto con su metadata.
 */
public class RegistrarImagen extends AppCompatActivity {

    private static final int COD_SELECCIONA = 10;

    private Button botonCargar;
    private Button botonFirmarGuardar;
    private ImageView imagen;
    private EditText editTextNombreObra;
    private Uri uriImagenSeleccionada;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;

    /**
     * Método llamado cuando se crea la actividad.
     * Inicializa los elementos de la interfaz y configura los botones.
     *
     * @param savedInstanceState Estado de la instancia anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        imagen = findViewById(R.id.imageId);
        botonCargar = findViewById(R.id.btnCargarImg);
        botonFirmarGuardar = findViewById(R.id.btnFirmarGuardar);
        editTextNombreObra = findViewById(R.id.editTextNombreObra);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("imagenes");

        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        botonFirmarGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firmarYGuardarImagen();
            }
        });
    }

    /**
     * Inicia la actividad para seleccionar una imagen del dispositivo.
     */
    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione la Imagen"), COD_SELECCIONA);
    }

    /**
     * Método llamado cuando se selecciona una imagen.
     *
     * @param requestCode Código de solicitud.
     * @param resultCode  Resultado de la actividad.
     * @param data        Datos devueltos por la actividad.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == COD_SELECCIONA && data != null && data.getData() != null) {
            uriImagenSeleccionada = data.getData();
            Glide.with(this)
                    .load(uriImagenSeleccionada)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_image)
                    .into(imagen);
        }
    }

    /**
     * Firma la imagen seleccionada utilizando RSA y guarda la imagen y su metadata en Firebase.
     */
    private void firmarYGuardarImagen() {
        if (uriImagenSeleccionada != null && !editTextNombreObra.getText().toString().isEmpty()) {
            String nombreObra = editTextNombreObra.getText().toString();
            String hashImagen = HashUtil.generarHash(this, uriImagenSeleccionada);

            if (hashImagen != null) {
                try {
                    RsaUtil rsaUtil = new RsaUtil();
                    String hashCifrado = rsaUtil.encrypt(hashImagen);

                    if (hashCifrado != null) {
                        String fileName = System.currentTimeMillis() + "_" + uriImagenSeleccionada.getLastPathSegment();
                        StorageReference storageRef = firebaseStorage.getReference().child("imagenes/" + fileName);

                        storageRef.putFile(uriImagenSeleccionada)
                                .addOnSuccessListener(taskSnapshot -> {
                                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                        String urlImagen = downloadUrl.toString();
                                        guardarImagenEnDatabase(nombreObra, urlImagen, hashCifrado, rsaUtil.getPublicKeyBase64());
                                    }).addOnFailureListener(e -> {
                                        mostrarError("Error al obtener la URL de la imagen: " + e.getMessage());
                                    });
                                }).addOnFailureListener(e -> {
                                    mostrarError("Error al cargar la imagen: " + e.getMessage());
                                });
                    } else {
                        mostrarError("Error al cifrar el hash de la imagen");
                    }
                } catch (Exception e) {
                    mostrarError("Error al inicializar RSA: " + e.getMessage());
                }
            } else {
                mostrarError("Error al generar el hash de la imagen");
            }
        } else {
            mostrarError("Debe ingresar un nombre de obra y seleccionar una imagen");
        }
    }

    /**
     * Guarda la metadata de la imagen (nombre, URL, hash cifrado y clave pública) en Firebase Database.
     *
     * @param nombreObra    Nombre de la obra.
     * @param urlImagen     URL de la imagen en Firebase Storage.
     * @param hashCifrado   Hash cifrado de la imagen.
     * @param publicKeyBase64 Clave pública utilizada para firmar la imagen.
     */
    private void guardarImagenEnDatabase(String nombreObra, String urlImagen, String hashCifrado, String publicKeyBase64) {
        String id = databaseReference.push().getKey();
        Imagen imagen = new Imagen(nombreObra, urlImagen, hashCifrado, publicKeyBase64);

        databaseReference.child(id).setValue(imagen)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistrarImagen.this, "Obra registrada con éxito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrarImagen.this, MainActivity.class));
                })
                .addOnFailureListener(e -> {
                    mostrarError("Error al guardar la imagen en la base de datos: " + e.getMessage());
                });
    }

    /**
     * Muestra un mensaje de error en pantalla.
     *
     * @param mensaje El mensaje de error.
     */
    private void mostrarError(String mensaje) {
        Toast.makeText(RegistrarImagen.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}















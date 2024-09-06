package com.espol.artauthenticator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Modelo.HashUtil;
import Modelo.RsaUtil;
import Modelo.VerificacionResultado;

public class VerificarImagen extends AppCompatActivity {

    private static final int COD_SELECCIONA = 10;

    private Button botonCargar, botonVerificar;
    private ImageView imagenSeleccionada;
    private EditText editTextPublicKey;
    private Uri uriImagenSeleccionada;

    private DatabaseReference databaseReference;
    private DatabaseReference verificacionRef;

    /**
     * Método que se ejecuta cuando la actividad es creada.
     * Se inicializan las referencias a los componentes de la interfaz, y se configura la base de datos de Firebase.
     * También se configuran los botones para cargar y verificar la imagen.
     *
     * @param savedInstanceState El estado de la actividad guardado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar);

        // Inicializa las referencias a los elementos de la interfaz.
        imagenSeleccionada = findViewById(R.id.imageViewPreview);
        botonCargar = findViewById(R.id.buttonSeleccionarImagen);
        botonVerificar = findViewById(R.id.buttonGuardarImagen);
        editTextPublicKey = findViewById(R.id.editTextPublicKey);

        // Inicializa la referencia a la base de datos de Firebase.
        databaseReference = FirebaseDatabase.getInstance().getReference("imagenes");
        verificacionRef = FirebaseDatabase.getInstance().getReference("verificaciones");

        // Configura el botón para cargar una imagen.
        botonCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        // Configura el botón para verificar la imagen cargada.
        botonVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarImagen();
            }
        });
    }

    /**
     * Método que inicia la selección de una imagen desde el almacenamiento del dispositivo.
     * Abre un Intent para seleccionar un archivo de imagen.
     */
    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione la Imagen"), COD_SELECCIONA);
    }

    /**
     * Método que se ejecuta cuando se regresa de seleccionar una imagen.
     * Carga la imagen seleccionada y la muestra en un ImageView usando Glide.
     *
     * @param requestCode Código de solicitud.
     * @param resultCode  Código de resultado.
     * @param data        Datos del Intent que contienen la URI de la imagen seleccionada.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == COD_SELECCIONA && data != null && data.getData() != null) {
            // Obtiene la URI de la imagen seleccionada.
            uriImagenSeleccionada = data.getData();
            // Carga la imagen en el ImageView usando Glide.
            Glide.with(this)
                    .load(uriImagenSeleccionada)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_image)
                    .into(imagenSeleccionada);
        }
    }

    /**
     * Método que verifica la autenticidad de la imagen seleccionada.
     * Compara el hash generado de la imagen seleccionada con el hash cifrado almacenado en la base de datos.
     */
    private void verificarImagen() {
        // Verifica que se haya seleccionado una imagen y que se haya ingresado una clave pública.
        if (uriImagenSeleccionada != null && !editTextPublicKey.getText().toString().isEmpty()) {
            String publicKey = editTextPublicKey.getText().toString();
            // Genera el hash de la imagen seleccionada.
            String hashImagen = HashUtil.generarHash(this, uriImagenSeleccionada);

            if (hashImagen != null) {
                // Busca en la base de datos las imágenes que coincidan con la clave pública ingresada.
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean esAutentica = false;
                        String hashOriginal = null;

                        // Recorre todas las imágenes almacenadas en la base de datos.
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String storedPublicKey = snapshot.child("clavePublica").getValue(String.class);
                            String hashCifrado = snapshot.child("hashCifrado").getValue(String.class);

                            Log.d("VerificarImagen", "Stored Public Key: " + storedPublicKey);
                            Log.d("VerificarImagen", "Hash Cifrado: " + hashCifrado);

                            // Si la clave pública coincide, se descifra el hash cifrado.
                            if (publicKey.equals(storedPublicKey)) {
                                try {
                                    RsaUtil rsaUtil = new RsaUtil(publicKey);
                                    String hashDescifrado = rsaUtil.decrypt(hashCifrado);

                                    Log.d("Tag", "Hash Imagen: " + hashImagen);
                                    Log.d("Tag", "Hash Descifrado: " + hashDescifrado);

                                    // Compara el hash descifrado con el hash de la imagen seleccionada.
                                    if (hashImagen.equals(hashDescifrado)) {
                                        esAutentica = true;
                                    }
                                    hashOriginal = hashDescifrado;
                                    break;
                                } catch (Exception e) {
                                    mostrarError("Error al descifrar el hash: " + e.getMessage());
                                }
                            }
                        }

                        // Muestra el resultado de la verificación.
                        if (esAutentica) {
                            Toast.makeText(VerificarImagen.this, "La imagen es auténtica", Toast.LENGTH_SHORT).show();
                        } else {
                            mostrarError("La imagen no es auténtica o no coincide");
                        }

                        // Guarda el resultado de la verificación en Firebase.
                        guardarResultadoVerificacion(uriImagenSeleccionada.toString(), hashImagen, hashOriginal, esAutentica);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mostrarError("Error al acceder a la base de datos: " + databaseError.getMessage());
                    }
                });
            } else {
                mostrarError("Error al generar el hash de la imagen");
            }
        } else {
            mostrarError("Debe ingresar una clave pública válida y seleccionar una imagen");
        }
    }

    /**
     * Método que guarda el resultado de la verificación en Firebase.
     *
     * @param urlImagen    URL de la imagen verificada.
     * @param hashNueva    Hash de la imagen seleccionada.
     * @param hashOriginal Hash descifrado almacenado.
     * @param esAutentica  Indica si la imagen es auténtica.
     */
    private void guardarResultadoVerificacion(String urlImagen, String hashNueva, String hashOriginal, boolean esAutentica) {
        // Genera una clave única para almacenar el resultado.
        String id = verificacionRef.push().getKey();
        VerificacionResultado resultado = new VerificacionResultado(urlImagen, hashNueva, hashOriginal, esAutentica);

        // Guarda el resultado en la base de datos.
        verificacionRef.child(id).setValue(resultado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(VerificarImagen.this, "Resultado de la verificación guardado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    mostrarError("Error al guardar el resultado de la verificación en la base de datos: " + e.getMessage());
                });
    }

    /**
     * Método que muestra un mensaje de error utilizando un Toast.
     *
     * @param mensaje El mensaje de error que se mostrará.
     */
    private void mostrarError(String mensaje) {
        Toast.makeText(VerificarImagen.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}








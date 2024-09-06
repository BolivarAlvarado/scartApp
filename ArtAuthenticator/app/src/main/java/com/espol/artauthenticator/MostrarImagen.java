package com.espol.artauthenticator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Modelo.Imagen;
import Modelo.ImagenAdapter;

public class MostrarImagen extends AppCompatActivity {

    private static final String TAG = "MostrarImagen";

    private RecyclerView recyclerView;
    private ImagenAdapter imagenAdapter;
    private BottomNavigationView navigation;
    private DatabaseReference databaseReference;

    /**
     * Este método se ejecuta cuando la actividad es creada.
     * Se inicializan el RecyclerView, el adaptador y se carga la lista de imágenes desde Firebase Realtime Database.
     * También se configura el BottomNavigationView para manejar la navegación.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagen);

        // Inicializa el RecyclerView y lo configura con un LinearLayoutManager.
        recyclerView = findViewById(R.id.recyclerViewImagenes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa el adaptador con una lista vacía.
        imagenAdapter = new ImagenAdapter(new ArrayList<>());
        recyclerView.setAdapter(imagenAdapter);

        // Referencia a la base de datos Realtime Database de Firebase.
        databaseReference = FirebaseDatabase.getInstance().getReference("imagenes/");

        // Llama al método para cargar las imágenes desde Firebase.
        cargarImagenesDesdeRealtimeDatabase();

        // Configura el BottomNavigationView para la navegación entre pantallas.
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navHome) {
                // Navega a la pantalla principal (MainActivity) al seleccionar el ítem "Home".
                Intent intent = new Intent(MostrarImagen.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    /**
     * Este método carga las imágenes almacenadas en Firebase Realtime Database.
     * Usa un ValueEventListener para obtener los datos en tiempo real y actualiza el RecyclerView con las imágenes descargadas.
     */
    private void cargarImagenesDesdeRealtimeDatabase() {
        // Añade un oyente para escuchar cambios en la base de datos de Firebase.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lista temporal para almacenar las imágenes obtenidas.
                List<Imagen> listaImagenes = new ArrayList<>();

                // Itera sobre cada hijo del snapshot de Firebase.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Convierte cada snapshot a un objeto Imagen.
                    Imagen imagen = snapshot.getValue(Imagen.class);
                    if (imagen != null) {
                        // Registra en los logs cada imagen encontrada.
                        Log.d(TAG, "Imagen encontrada: " + imagen.getNombre() + ", " + imagen.getRutaImagen());
                        // Añade la imagen a la lista.
                        listaImagenes.add(imagen);
                    }
                }
                // Actualiza los datos en el adaptador del RecyclerView.
                imagenAdapter.updateData(listaImagenes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Si ocurre un error al cargar los datos, lo registra en los logs.
                Log.e(TAG, "Error al cargar datos desde Realtime Database: " + databaseError.getMessage());
            }
        });
    }
}















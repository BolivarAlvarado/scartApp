package com.espol.artauthenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Actividad principal de la aplicación que gestiona la barra de navegación inferior
 * y proporciona métodos para iniciar otras actividades.
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.bottom_navigation);

        // Configuración de la barra de navegación
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navHome){
                    return true;
                }else{
                    Intent intent = new Intent(MainActivity.this, MostrarImagen.class);
                    startActivity(intent);
                    return true;
                }
            }
        });
    }

    /**
     * Inicia la actividad para registrar una nueva imagen.
     *
     * @param view La vista que activó este método.
     */
    public void IniciarImagen(View view){
        Intent intent = new Intent(MainActivity.this, RegistrarImagen.class);
        startActivity(intent);
    }

    /**
     * Inicia la actividad para mostrar el funcionamiento de la aplicación.
     *
     * @param view La vista que activó este método.
     */
    public void IniciarFuncionamiento(View view){
        Intent intent = new Intent(MainActivity.this, Funcionamiento.class);
        startActivity(intent);
    }

    /**
     * Inicia la actividad para mostrar estadísticas.
     *
     * @param view La vista que activó este método.
     */
    public void IniciarEstadistica(View view){
        Intent intent = new Intent(MainActivity.this, Estadistica.class);
        startActivity(intent);
    }

    /**
     * Inicia la actividad para comparar imágenes.
     *
     * @param view La vista que activó este método.
     */
    public void IniciarComparacion(View view){
        Intent intent = new Intent(MainActivity.this, VerificarImagen.class);
        startActivity(intent);
    }
}


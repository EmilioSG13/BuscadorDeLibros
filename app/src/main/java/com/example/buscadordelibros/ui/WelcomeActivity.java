package com.example.buscadordelibros.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import com.example.buscadordelibros.R;

public class WelcomeActivity extends AppCompatActivity {
    private PrefsManager prefs;
    private TextView tvSaludo, tvUltima;
    private EditText etNombre;
    private SwitchCompat swTema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        InsetsUtil.aplicar(findViewById(R.id.main));
        prefs = new PrefsManager(this);

        tvSaludo = findViewById(R.id.tvSaludo);
        tvUltima = findViewById(R.id.tvUltima);
        etNombre = findViewById(R.id.etNombre);
        swTema = findViewById(R.id.swTema);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCatalogo = findViewById(R.id.btnCatalogo);
        Button btnFavoritos = findViewById(R.id.btnFavoritos);

        etNombre.setText(prefs.getNombre());
        swTema.setChecked(prefs.isTemaOscuro());

        btnGuardar.setOnClickListener(v -> {
            prefs.setNombre(etNombre.getText().toString().trim());
            boolean oscuro = swTema.isChecked();
            boolean cambio = oscuro != prefs.isTemaOscuro();
            prefs.setTemaOscuro(oscuro);
            Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();
            mostrarPreferencias();
            if (cambio) {
                AppCompatDelegate.setDefaultNightMode(oscuro
                        ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        btnCatalogo.setOnClickListener(v -> startActivity(new Intent(this, CatalogActivity.class)));
        btnFavoritos.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarPreferencias();
    }

    private void mostrarPreferencias() {
        String n = prefs.getNombre();
        tvSaludo.setText(n.isEmpty() ? "¡Bienvenido!" : "¡Hola, " + n + "!");
        tvUltima.setText("Última sección visitada: " + prefs.getUltimaSeccion()
                + "\nTema: " + (prefs.isTemaOscuro() ? "oscuro" : "claro"));
    }
}

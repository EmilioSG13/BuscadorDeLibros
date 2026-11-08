package com.example.buscadordelibros.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.buscadordelibros.R;
import com.example.buscadordelibros.adapters.LibroAdapter;
import com.example.buscadordelibros.db.AppDatabase;
import com.example.buscadordelibros.db.LibroDao;
import com.example.buscadordelibros.models.LibroEntity;
import java.util.HashSet;
import java.util.Set;

/** Lee solo de Room: funciona 100% sin Internet. */
public class FavoritesActivity extends AppCompatActivity implements LibroAdapter.Listener {
    private LibroDao dao;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        InsetsUtil.aplicar(findViewById(R.id.main));
        setTitle("Favoritos (Offline)");
        prefs = new PrefsManager(this);
        dao = AppDatabase.getInstance(this).libroDao();

        RecyclerView rv = findViewById(R.id.rvFavoritos);
        TextView tvVacio = findViewById(R.id.tvVacio);
        rv.setLayoutManager(new LinearLayoutManager(this));
        LibroAdapter adapter = new LibroAdapter(this);
        rv.setAdapter(adapter);

        dao.obtenerTodos().observe(this, lista -> {
            Set<String> ids = new HashSet<>();
            for (LibroEntity l : lista) ids.add(l.id);
            adapter.setFavoritos(ids);
            adapter.setDatos(lista);
            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.setUltimaSeccion("Favoritos");
    }

    @Override
    public void onDetalle(LibroEntity libro) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_ID, libro.id);
        i.putExtra("libro_obj", libro);
        startActivity(i);
    }

    @Override
    public void onFavorito(LibroEntity libro) {
        AppDatabase.dbExecutor.execute(() -> {
            dao.eliminar(libro);
            runOnUiThread(() -> Toast.makeText(FavoritesActivity.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show());
        });
    }
}

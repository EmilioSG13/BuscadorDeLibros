package com.example.buscadordelibros.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.buscadordelibros.R;
import com.example.buscadordelibros.adapters.LibroAdapter;
import com.example.buscadordelibros.api.RetrofitClient;
import com.example.buscadordelibros.db.AppDatabase;
import com.example.buscadordelibros.db.LibroDao;
import com.example.buscadordelibros.models.LibroEntity;
import com.example.buscadordelibros.models.VolumesResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogActivity extends AppCompatActivity implements LibroAdapter.Listener {
    private static final String CONSULTA = "subject:computers";

    private LibroAdapter adapter;
    private LibroDao dao;
    private ProgressBar progress;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        InsetsUtil.aplicar(findViewById(R.id.main));
        setTitle("Catálogo de Libros");
        prefs = new PrefsManager(this);
        dao = AppDatabase.getInstance(this).libroDao();

        progress = findViewById(R.id.progress);
        RecyclerView rv = findViewById(R.id.rvLibros);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibroAdapter(this);
        rv.setAdapter(adapter);

        Button btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> cargarCatalogo());

        // Mantiene las estrellas sincronizadas con Room
        dao.obtenerTodos().observe(this, lista -> {
            Set<String> ids = new HashSet<>();
            for (LibroEntity l : lista) ids.add(l.id);
            adapter.setFavoritos(ids);
        });

        cargarCatalogo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.setUltimaSeccion("Catálogo");
    }

    private void cargarCatalogo() {
        progress.setVisibility(View.VISIBLE);
        RetrofitClient.getApi().buscar(CONSULTA, 20).enqueue(new Callback<VolumesResponse>() {
            @Override
            public void onResponse(Call<VolumesResponse> call, Response<VolumesResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<LibroEntity> lista = new ArrayList<>();
                    if (response.body().items != null) {
                        for (VolumesResponse.VolumeItem it : response.body().items) lista.add(it.toEntity());
                    }
                    adapter.setDatos(lista);
                } else {
                    String mensaje = "Error del servidor: " + response.code();
                    if (response.code() == 429) {
                        mensaje = "Demasiadas peticiones (Error 429). Límite de Google Books API alcanzado.";
                    }
                    Toast.makeText(CatalogActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    cargarCatalogoLocal();
                }
            }

            @Override
            public void onFailure(Call<VolumesResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(CatalogActivity.this,
                        "Error de conexión. Cargando libros locales...", Toast.LENGTH_LONG).show();
                cargarCatalogoLocal();
            }
        });
    }

    private void cargarCatalogoLocal() {
        AppDatabase.dbExecutor.execute(() -> {
            List<LibroEntity> locales = dao.obtenerTodosSync();
            runOnUiThread(() -> {
                if (locales != null && !locales.isEmpty()) {
                    adapter.setDatos(locales);
                    Toast.makeText(CatalogActivity.this, "Mostrando libros guardados localmente.", Toast.LENGTH_SHORT).show();
                } else {
                    List<LibroEntity> respaldo = obtenerLibrosDeRespaldo();
                    adapter.setDatos(respaldo);
                    Toast.makeText(CatalogActivity.this, "Límite de API alcanzado. Mostrando catálogo de respaldo.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private List<LibroEntity> obtenerLibrosDeRespaldo() {
        List<LibroEntity> lista = new ArrayList<>();
        
        LibroEntity l1 = new LibroEntity();
        l1.id = "respaldo_1";
        l1.titulo = "Clean Code: A Handbook of Agile Software Craftsmanship";
        l1.autor = "Robert C. Martin";
        l1.editorial = "Prentice Hall";
        l1.paginas = 464;
        l1.disponibilidad = "Disponible";
        l1.descripcion = "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.";
        lista.add(l1);

        LibroEntity l2 = new LibroEntity();
        l2.id = "respaldo_2";
        l2.titulo = "The Pragmatic Programmer";
        l2.autor = "Andrew Hunt, David Thomas";
        l2.editorial = "Addison-Wesley Professional";
        l2.paginas = 352;
        l2.disponibilidad = "Disponible";
        l2.descripcion = "Straight from the programming trenches, The Pragmatic Programmer cuts through the increasing specialization and technical modernities of software development.";
        lista.add(l2);

        LibroEntity l3 = new LibroEntity();
        l3.id = "respaldo_3";
        l3.titulo = "Effective Java";
        l3.autor = "Joshua Bloch";
        l3.editorial = "Addison-Wesley Professional";
        l3.paginas = 416;
        l3.disponibilidad = "Disponible";
        l3.descripcion = "The definitive guide to Java platform best practices.";
        lista.add(l3);

        LibroEntity l4 = new LibroEntity();
        l4.id = "respaldo_4";
        l4.titulo = "Design Patterns: Elements of Reusable Object-Oriented Software";
        l4.autor = "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides";
        l4.editorial = "Addison-Wesley Professional";
        l4.paginas = 395;
        l4.disponibilidad = "Disponible";
        l4.descripcion = "Capturing a wealth of experience about the design of object-oriented software systems.";
        lista.add(l4);

        LibroEntity l5 = new LibroEntity();
        l5.id = "respaldo_5";
        l5.titulo = "Code Complete: A Practical Handbook of Software Construction";
        l5.autor = "Steve McConnell";
        l5.editorial = "Microsoft Press";
        l5.paginas = 960;
        l5.disponibilidad = "Disponible";
        l5.descripcion = "Widely considered one of the best practical guides to programming, Steve McConnell's original code complete has been fully updated.";
        lista.add(l5);

        return lista;
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
            boolean fav = dao.esFavorito(libro.id) > 0;
            if (fav) {
                dao.eliminar(libro);
                runOnUiThread(() -> Toast.makeText(CatalogActivity.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show());
            } else {
                dao.insertar(libro);
                runOnUiThread(() -> Toast.makeText(CatalogActivity.this, "Agregado a favoritos", Toast.LENGTH_SHORT).show());
            }
        });
    }
}

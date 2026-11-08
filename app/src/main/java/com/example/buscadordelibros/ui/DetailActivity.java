package com.example.buscadordelibros.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import com.example.buscadordelibros.R;
import com.example.buscadordelibros.api.RetrofitClient;
import com.example.buscadordelibros.db.AppDatabase;
import com.example.buscadordelibros.db.LibroDao;
import com.example.buscadordelibros.models.LibroEntity;
import com.example.buscadordelibros.models.VolumesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "libro_id";

    private LibroDao dao;
    private LibroEntity libro;
    private boolean esFavorito = false;
    private String id;
    private TextView tvTitulo, tvDatos, tvDescripcion;
    private Button btnFav;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        InsetsUtil.aplicar(findViewById(R.id.main));
        setTitle("Detalle del Libro");
        dao = AppDatabase.getInstance(this).libroDao();
        id = getIntent().getStringExtra(EXTRA_ID);

        tvTitulo = findViewById(R.id.tvTituloDetalle);
        tvDatos = findViewById(R.id.tvDatosDetalle);
        tvDescripcion = findViewById(R.id.tvDescripcionDetalle);
        btnFav = findViewById(R.id.btnFavDetalle);
        btnFav.setOnClickListener(v -> alternarFavorito());

        // Si se pasó el objeto libro directamente, lo mostramos sin hacer petición de red (evita error 429)
        libro = (LibroEntity) getIntent().getSerializableExtra("libro_obj");
        if (libro != null) {
            id = libro.id;
            mostrar();
            AppDatabase.dbExecutor.execute(() -> {
                esFavorito = dao.esFavorito(id) > 0;
                runOnUiThread(this::actualizarBoton);
            });
        } else {
            cargarDetalle();
        }
    }

    private void cargarDetalle() {
        RetrofitClient.getApi().detalle(id).enqueue(new Callback<VolumesResponse.VolumeItem>() {
            @Override
            public void onResponse(Call<VolumesResponse.VolumeItem> call, Response<VolumesResponse.VolumeItem> r) {
                if (r.isSuccessful() && r.body() != null) {
                    libro = r.body().toEntity();
                    mostrar();
                    AppDatabase.dbExecutor.execute(() -> {
                        esFavorito = dao.esFavorito(id) > 0;
                        if (esFavorito) dao.actualizar(libro); // refresca copia local
                        runOnUiThread(DetailActivity.this::actualizarBoton);
                    });
                } else {
                    Toast.makeText(DetailActivity.this, "Error del servidor: " + r.code(), Toast.LENGTH_LONG).show();
                    cargarLocal();
                }
            }

            @Override
            public void onFailure(Call<VolumesResponse.VolumeItem> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_LONG).show();
                cargarLocal();
            }
        });
    }

    /** Respaldo offline: si el libro está en favoritos, se muestra desde Room. */
    private void cargarLocal() {
        AppDatabase.dbExecutor.execute(() -> {
            LibroEntity l = dao.obtenerPorId(id);
            runOnUiThread(() -> {
                if (l != null) {
                    libro = l;
                    esFavorito = true;
                    mostrar();
                    actualizarBoton();
                }
            });
        });
    }

    private void mostrar() {
        tvTitulo.setText(libro.titulo);
        tvDatos.setText("ID: " + libro.id
                + "\nAutor: " + libro.autor
                + "\nEditorial: " + libro.editorial
                + "\nPáginas: " + (libro.paginas > 0 ? libro.paginas : "N/D")
                + "\nDisponibilidad: " + libro.disponibilidad);
        tvDescripcion.setText(HtmlCompat.fromHtml(libro.descripcion, HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void actualizarBoton() {
        btnFav.setText(esFavorito ? "Quitar de favoritos" : "Agregar a favoritos");
    }

    private void alternarFavorito() {
        if (libro == null) return;
        AppDatabase.dbExecutor.execute(() -> {
            if (esFavorito) {
                dao.eliminar(libro);
                esFavorito = false;
                runOnUiThread(() -> {
                    actualizarBoton();
                    Toast.makeText(DetailActivity.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                });
            } else {
                dao.insertar(libro);
                esFavorito = true;
                runOnUiThread(() -> {
                    actualizarBoton();
                    Toast.makeText(DetailActivity.this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}

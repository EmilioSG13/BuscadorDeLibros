package com.example.buscadordelibros.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.buscadordelibros.R;
import com.example.buscadordelibros.models.LibroEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.VH> {

    public interface Listener {
        void onDetalle(LibroEntity libro);
        void onFavorito(LibroEntity libro);
    }

    private List<LibroEntity> datos = new ArrayList<>();
    private Set<String> favoritos = new HashSet<>();
    private final Listener listener;

    public LibroAdapter(Listener listener) { this.listener = listener; }

    public void setDatos(List<LibroEntity> d) {
        datos = d != null ? d : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setFavoritos(Set<String> f) {
        favoritos = f;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        LibroEntity l = datos.get(position);
        h.tvId.setText("ID: " + l.id);
        h.tvTitulo.setText(l.titulo);
        h.tvAutor.setText("Autor: " + l.autor);
        h.tvEditorial.setText("Editorial: " + l.editorial);
        h.tvPaginas.setText("Páginas: " + (l.paginas > 0 ? l.paginas : "N/D"));
        h.tvDisp.setText("Disponibilidad: " + l.disponibilidad);
        boolean fav = favoritos.contains(l.id);
        h.btnFav.setImageResource(fav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        h.itemView.setOnClickListener(v -> listener.onDetalle(l));
        h.btnFav.setOnClickListener(v -> listener.onFavorito(l));
    }

    @Override
    public int getItemCount() { return datos.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvId, tvTitulo, tvAutor, tvEditorial, tvPaginas, tvDisp;
        ImageButton btnFav;
        VH(View v) {
            super(v);
            tvId = v.findViewById(R.id.tvId);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvAutor = v.findViewById(R.id.tvAutor);
            tvEditorial = v.findViewById(R.id.tvEditorial);
            tvPaginas = v.findViewById(R.id.tvPaginas);
            tvDisp = v.findViewById(R.id.tvDisp);
            btnFav = v.findViewById(R.id.btnFav);
        }
    }
}

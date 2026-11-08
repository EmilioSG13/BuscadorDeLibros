package com.example.buscadordelibros.models;

import android.text.TextUtils;
import java.util.List;

/** Mapeo JSON (Gson) de Google Books API. */
public class VolumesResponse {
    public List<VolumeItem> items;

    public static class VolumeItem {
        public String id;
        public VolumeInfo volumeInfo;
        public SaleInfo saleInfo;

        public LibroEntity toEntity() {
            LibroEntity e = new LibroEntity();
            e.id = id != null ? id : "";
            VolumeInfo v = volumeInfo;
            e.titulo = (v != null && v.title != null) ? v.title : "Sin título";
            e.autor = (v != null && v.authors != null && !v.authors.isEmpty())
                    ? TextUtils.join(", ", v.authors) : "Autor desconocido";
            e.editorial = (v != null && v.publisher != null) ? v.publisher : "Sin editorial";
            e.paginas = v != null ? v.pageCount : 0;
            e.descripcion = (v != null && v.description != null) ? v.description : "Sin descripción disponible.";
            String s = saleInfo != null ? saleInfo.saleability : null;
            if ("FOR_SALE".equals(s)) e.disponibilidad = "Disponible";
            else if ("FREE".equals(s)) e.disponibilidad = "Disponible (gratis)";
            else if ("NOT_FOR_SALE".equals(s)) e.disponibilidad = "No disponible";
            else e.disponibilidad = "Desconocida";
            return e;
        }
    }

    public static class VolumeInfo {
        public String title;
        public List<String> authors;
        public String publisher;
        public int pageCount;
        public String description;
    }

    public static class SaleInfo {
        public String saleability;
    }
}

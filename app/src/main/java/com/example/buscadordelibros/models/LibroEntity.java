package com.example.buscadordelibros.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "favoritos")
public class LibroEntity implements Serializable {
    @PrimaryKey
    @NonNull
    public String id = "";
    public String titulo;
    public String autor;
    public String editorial;
    public int paginas;
    public String disponibilidad;
    public String descripcion;
}

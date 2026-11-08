package com.example.buscadordelibros.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.buscadordelibros.models.LibroEntity;
import java.util.List;

@Dao
public interface LibroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(LibroEntity libro);

    @Query("SELECT * FROM favoritos ORDER BY titulo ASC")
    LiveData<List<LibroEntity>> obtenerTodos();

    @Query("SELECT * FROM favoritos ORDER BY titulo ASC")
    List<LibroEntity> obtenerTodosSync();

    @Query("SELECT * FROM favoritos WHERE id = :id LIMIT 1")
    LibroEntity obtenerPorId(String id);

    @Query("SELECT COUNT(*) FROM favoritos WHERE id = :id")
    int esFavorito(String id);

    @Update
    void actualizar(LibroEntity libro);

    @Delete
    void eliminar(LibroEntity libro);
}

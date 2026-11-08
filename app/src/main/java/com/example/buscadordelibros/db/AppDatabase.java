package com.example.buscadordelibros.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.buscadordelibros.models.LibroEntity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LibroEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LibroDao libroDao();

    /** Hilo secundario para escrituras/lecturas síncronas en Room. */
    public static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "libros_db").build();
                }
            }
        }
        return INSTANCE;
    }
}

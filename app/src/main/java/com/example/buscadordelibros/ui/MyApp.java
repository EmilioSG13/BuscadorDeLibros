package com.example.buscadordelibros.ui;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean oscuro = new PrefsManager(this).isTemaOscuro();
        AppCompatDelegate.setDefaultNightMode(oscuro
                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}

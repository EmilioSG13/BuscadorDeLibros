package com.example.buscadordelibros.ui;

import android.content.Context;
import android.content.SharedPreferences;

/** Solo datos no sensibles: nombre, tema y última sección. */
public class PrefsManager {
    private static final String FILE = "preferencias_usuario";
    private static final String K_NOMBRE = "nombre";
    private static final String K_OSCURO = "tema_oscuro";
    private static final String K_SECCION = "ultima_seccion";

    private final SharedPreferences sp;

    public PrefsManager(Context c) {
        sp = c.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public String getNombre() { return sp.getString(K_NOMBRE, ""); }
    public void setNombre(String n) { sp.edit().putString(K_NOMBRE, n).apply(); }
    public boolean isTemaOscuro() { return sp.getBoolean(K_OSCURO, false); }
    public void setTemaOscuro(boolean b) { sp.edit().putBoolean(K_OSCURO, b).apply(); }
    public String getUltimaSeccion() { return sp.getString(K_SECCION, "Ninguna"); }
    public void setUltimaSeccion(String s) { sp.edit().putString(K_SECCION, s).apply(); }
}

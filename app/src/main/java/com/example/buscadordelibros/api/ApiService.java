package com.example.buscadordelibros.api;

import com.example.buscadordelibros.models.VolumesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("volumes")
    Call<VolumesResponse> buscar(@Query("q") String consulta, @Query("maxResults") int max);

    @GET("volumes/{id}")
    Call<VolumesResponse.VolumeItem> detalle(@Path("id") String id);
}

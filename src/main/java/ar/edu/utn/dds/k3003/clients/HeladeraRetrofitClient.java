package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface HeladeraRetrofitClient {
    @POST("cambiarEstadoActivo/{idHeladera}")
    Call<Void> cambiarEstadoActivo(@Path("idHeladera") Integer id);

}

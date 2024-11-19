package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.model.dtos.IncidenteDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IncidenteRetrofitClient {
    @PATCH("incidentes/{id}")
    Call<Void> actualizar(@Path("id") Long id, @Body IncidenteDTO incidenteDTO);
}

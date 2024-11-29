package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.model.dtos.IncidenteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class IncidenteProxy {
    private final String endpoint;
    private final IncidenteRetrofitClient service;

    public IncidenteProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_INCIDENTES", "http://localhost:8081/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(IncidenteRetrofitClient.class);
    }

    public void actualizar(Long id, IncidenteDTO incidenteDTO){
        Response<Void> execute = null;

        try {
            execute = service.actualizar(id, incidenteDTO).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (execute.code() == HttpStatus.NOT_FOUND.getCode()){
            throw new NoSuchElementException("No se encontro al colaborador en INCIDENTES");
        }
        //throw new RuntimeException("Error al conectarse con Incidentes");
    }

}

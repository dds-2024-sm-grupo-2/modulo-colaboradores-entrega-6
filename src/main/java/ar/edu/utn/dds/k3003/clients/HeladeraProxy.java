package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class HeladeraProxy implements FachadaHeladeras{

    private final String endpoint;
    private final HeladeraRetrofitClient service;

    public HeladeraProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_HELADERAS", "http://localhost:8083");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(HeladeraRetrofitClient.class);
    }

    public void cambiarEstadoActivo(Long id) {
        try {
            Integer idInt = Integer.parseInt(id.toString()); //parsear de long a integer
            Response<Void> response = service.cambiarEstadoActivo(idInt).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("No se pudo cambiar estado de la heladera: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cambiar estado en heladera: ", e);
        }
    }

    @Override
    public HeladeraDTO agregar(HeladeraDTO heladeraDTO) {
        return null;
    }

    @Override
    public void depositar(Integer integer, String s) throws NoSuchElementException {

    }

    @Override
    public Integer cantidadViandas(Integer integer) throws NoSuchElementException {
        return 0;
    }

    @Override
    public void retirar(RetiroDTO retiroDTO) throws NoSuchElementException {
    }

    @Override
    public void temperatura(TemperaturaDTO temperaturaDTO) {

    }

    @Override
    public List<TemperaturaDTO> obtenerTemperaturas(Integer integer) {
        return List.of();
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {

    }

}

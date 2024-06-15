package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class LogisticaProxy implements FachadaLogistica {

    private final String endpoint;
    private final LogisticaRetrofitClient service;

    public LogisticaProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_LOGISTICA", "https://two024-tp-entrega-3-kenzogrosvald.onrender.com");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(LogisticaRetrofitClient.class);
    }
    @Override
    public RutaDTO agregar(RutaDTO var1){return null;}
    @Override
    public TrasladoDTO buscarXId(Long var1) throws NoSuchElementException {return null;}
    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO var1) throws TrasladoNoAsignableException{return null;}
    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long var1, Integer mes, Integer anio) throws NoSuchElementException{
        Response<List<TrasladoDTO>> execute = null;
        try {
            execute = service.findByTraslado(var1, anio, mes).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (execute.isSuccessful()){
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()){
            throw new NoSuchElementException("No se encontro al colaborador en LOGISTICA");
        }
        throw new RuntimeException("Error al conectarse con Logistica");
    }
    @Override
    public void setHeladerasProxy(FachadaHeladeras var1){}
    @Override
    public void setViandasProxy(FachadaViandas var1){}
    @Override
    public void trasladoRetirado(Long var1){}
    @Override
    public void trasladoDepositado(Long var1){}

}

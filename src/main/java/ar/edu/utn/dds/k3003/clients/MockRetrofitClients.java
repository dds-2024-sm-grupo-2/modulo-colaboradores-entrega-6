package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.Request;
import okio.Timeout;

import java.time.LocalDateTime;
import java.util.List;

public class MockRetrofitClients implements ViandasRetrofitClient, LogisticaRetrofitClient{

    @Override
    public Call<ViandaDTO> get(String qr) {
        return new Call<ViandaDTO>() {
            @Override
            public Response<ViandaDTO> execute() {
                // Simula una respuesta exitosa
                ViandaDTO mockVianda = new ViandaDTO(
                        qr,
                        LocalDateTime.now(),
                        EstadoViandaEnum.DEPOSITADA,
                        1L,
                        1
                );
                return Response.success(mockVianda);
            }

            @Override
            public void enqueue(Callback<ViandaDTO> callback) {
                ViandaDTO mockVianda = new ViandaDTO(
                        qr,
                        LocalDateTime.now(),
                        EstadoViandaEnum.DEPOSITADA,
                        1L,
                        1
                );
                callback.onResponse(this, Response.success(mockVianda));
            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {}

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<ViandaDTO> clone() {
                return this;
            }

            @Override
            public Request request() {
                return new Request.Builder().url("http://localhost:8081/viandas/" + qr).build();
            }

            @Override
            public Timeout timeout() {
                return Timeout.NONE;
            }
        };
    }

    @Override
    public Call<List<ViandaDTO>> findByViandas(Long id, Integer integ, Integer integ1) {
        return new Call<List<ViandaDTO>>() {
            @Override
            public Response<List<ViandaDTO>> execute() {
                var viandaDTO1 = new ViandaDTO("", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 2L, 1);
                viandaDTO1.setId(14L);
                return Response.success(List.of(viandaDTO1));
            }

            @Override
            public void enqueue(Callback<List<ViandaDTO>> callback){}

            @Override
            public boolean isExecuted() {return false;}

            @Override
            public void cancel(){}

            @Override
            public boolean isCanceled() {return false;}

            @Override
            public Call<List<ViandaDTO>> clone() {return this.clone();}

            @Override
            public Request request() {return new Request.Builder().url("http://localhost:8081/viandas/").build();}

            @Override
            public Timeout timeout() {return Timeout.NONE;}
        };
    }

    public Call<List<TrasladoDTO>> findByTraslado(Long id) {
        return new Call<List<TrasladoDTO>>() {
            @Override
            public Response<List<TrasladoDTO>> execute() {
                var trasladoDTO = new TrasladoDTO("", EstadoTrasladoEnum.ENTREGADO, LocalDateTime.now(), 2, 1);
                trasladoDTO.setId(14L);
                return Response.success(List.of(trasladoDTO));
            }

            @Override
            public void enqueue(Callback<List<TrasladoDTO>> callback){}

            @Override
            public boolean isExecuted() {return false;}

            @Override
            public void cancel(){}

            @Override
            public boolean isCanceled() {return false;}

            @Override
            public Call<List<TrasladoDTO>> clone() {return this.clone();}

            @Override
            public Request request() {return new Request.Builder().url("http://localhost:8081/viandas/").build();}

            @Override
            public Timeout timeout() {return Timeout.NONE;}
        };
    }
}
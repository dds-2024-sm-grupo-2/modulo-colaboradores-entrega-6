package ar.edu.utn.dds.k3003.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class TelegramProxy {

    private final String endpoint;
    private final TelegramRetrofitClient service;

    public TelegramProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_TELEGRAM", "https://modulo-telegram-entrega-6-0o0j.onrender.com");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(TelegramRetrofitClient.class);
    }

    public void notificar(Long chatID, String msg) {
        try {
            Response<Void> response = service.notificar(chatID, msg).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("No se pudo notificar: " + response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al notificar: ", e);
        }
    }
}

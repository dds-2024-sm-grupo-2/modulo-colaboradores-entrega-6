package ar.edu.utn.dds.k3003.clients;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TelegramRetrofitClient {

    @POST("notificar/{chatID}")
    Call<Void> notificar(@Path("chatID") Long chatID, @Body String msg);
}
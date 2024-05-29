package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.LogisticaProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.model.controllers.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp{

    public static void main(String[] args){

        var env = System.getenv();
        var fachada  = new Fachada();
        var objectMapper = createObjectMapper();
        var colabController = new ColaboradorController(fachada);

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setLogisticaProxy(new LogisticaProxy(objectMapper));

        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_LOGISTICA = env.get("URL_LOGISTICA");
        var URL_HELADERAS = env.get("URL_HELADERAS");
        var URL_COLABORADORES = env.get("URL_COLABORADORES");

        int port = Integer.parseInt(env.getOrDefault("PORT", "8080"));
        var app = Javalin.create().start(port);

        app.get("/", ctx -> ctx.result("Hola Mundo"));
        app.post("/colaboradores", colabController::agregar);
        app.get("/colaboradores/{colaboradorID}", colabController::buscar);
        app.get("/colaboradores/{colaboradorID}/puntos", colabController::puntos);
        app.patch("/colaboradores/{colabID}", colabController::cambiarFormas);
        app.put("/formula", colabController::actualizar);
    }
    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}


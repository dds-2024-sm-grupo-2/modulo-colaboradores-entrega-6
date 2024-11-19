package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladeraProxy;
import ar.edu.utn.dds.k3003.clients.LogisticaProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.model.controllers.*;
import ar.edu.utn.dds.k3003.model.worker.MQUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.javalin.Javalin;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;


public class WebApp{
    public static EntityManagerFactory entityManagerFactory;
    public static final String TOKEN = "token";

    public static void main(String[] args) throws IOException, TimeoutException {

        // WEBAPP---------------------------------------------------------------------------

        var env = System.getenv();

        startEntityManagerFactory(env);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var fachada  = new Fachada();
        var objectMapper = createObjectMapper();
        var colabController = new ColaboradorController(fachada,entityManager, objectMapper);

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setLogisticaProxy(new LogisticaProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladeraProxy(objectMapper));

        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_LOGISTICA = env.get("URL_LOGISTICA");
        var URL_HELADERAS = env.get("URL_HELADERAS");
        var URL_COLABORADORES = env.get("URL_COLABORADORES");
        var URL_INCIDENTES = env.get("URL_INCIDENTES");

        int port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        // Metrics--------------------------------------------------------------------

        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        registry.config().commonTags("app", "metrics-sample");

        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);

        final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);

        fachada.setRegistry(registry);


        // Endpoints------------------------------------------------------------------

        var app = Javalin.create(cf -> {cf.registerPlugin(micrometerPlugin); }).start(port);

        app.get("/", ctx -> ctx.result("Modulo Colaboradores - Diseño de Sistemas 2024"));
        app.post("/colaboradores", colabController::agregar);
        app.get("/colaboradores/{colaboradorID}", colabController::buscar);
        app.get("/colaboradores/{colaboradorID}/puntos", colabController::puntos);
        app.patch("/colaboradores/{colabID}", colabController::cambiarFormas);
        app.put("/formula", colabController::actualizar);
        app.post("/fallas", colabController::falla);
        app.post("/dinero/{colabID}", colabController::donacionDinero);
        app.post("/arreglarHeladera/colaborador/{colabID}", colabController::arreglarHeladera);
        app.post("/evento",colabController::evento);
        app.get("/metrics", ctx -> {
            var auth = ctx.header("Authorization");

            if (auth != null && auth.intern() == "Bearer " + TOKEN) {
                ctx.contentType("text/plain; version=0.0.4").result(registry.scrape());
            }else{
                ctx.status(401).json("unauthorized access");
            }
        });
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
    public static void startEntityManagerFactory(Map<String, String> env) {
        // https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.driver"};
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("db", configOverrides);
    }

    public static EntityManagerFactory startEntityManagerFactory1(){
        // https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
        Map<String, String> env =System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();

        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.driver"};
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        return Persistence.createEntityManagerFactory("db", configOverrides);
    }

}

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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

    public static void main(String[] args){

        // WEBAPP---------------------------------------------------------------------------

        var env = System.getenv();

        startEntityManagerFactory(env);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var fachada  = new Fachada();
        var objectMapper = createObjectMapper();
        var colabController = new ColaboradorController(fachada,entityManager);

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setLogisticaProxy(new LogisticaProxy(objectMapper));

        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_LOGISTICA = env.get("URL_LOGISTICA");
        var URL_HELADERAS = env.get("URL_HELADERAS");
        var URL_COLABORADORES = env.get("URL_COLABORADORES");

        int port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        // Metrics--------------------------------------------------------------------

        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        registry.config().commonTags("app", "metrics-sample");

            //Metricas de la JVM

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
        app.delete("/cleandb", colabController::borrar);
        app.post("/fallas", colabController::falla);
        app.post("/dinero/{colabID}", colabController::donacionDinero);
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
}

package ar.edu.utn.dds.k3003.model.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.dtos.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.NoSuchElementException;

public class ColaboradorController {
    private final Fachada fachada;
    private final EntityManager entityManager;

    public ColaboradorController(Fachada fachada, EntityManager entityManager) {
        this.fachada = fachada;
        this.entityManager = entityManager;
    }

    public void agregar(Context ctx){
        var colabDTO = ctx.bodyAsClass(MiColaboradorDTO.class);
        var rtaDTO = this.fachada.agregarJPA(colabDTO, entityManager);
        ctx.status(HttpStatus.CREATED);
        ctx.json(rtaDTO);
    }

    public void buscar(Context ctx){
        try{
            String parametroString = ctx.pathParam("colaboradorID");
            Long colabID = Long.parseLong(parametroString);
            Colaborador colabBuscado = entityManager.find(Colaborador.class,colabID);
            ctx.status(HttpStatus.OK);
            ctx.json(colabBuscado);
        }
        catch (NoSuchElementException ex) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(ex.getLocalizedMessage());
        }
    }

    public void cambiarFormas(Context ctx){
        try {
            String parametroString = ctx.pathParam("colabID");
            Long colabID = Long.parseLong(parametroString);
            var cuerpoJSON = ctx.bodyAsClass(FormasDeColaborarDTO.class);
            var rtaDTO = fachada.modificarJPA(colabID, cuerpoJSON.getFormas(), entityManager);

            ctx.status(HttpStatus.OK);
            ctx.result("Formas cambiadas correctamente");
            ctx.json(rtaDTO);
        }
        catch (NoSuchElementException ex){
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(ex.getLocalizedMessage());
        }
    }

    public void puntos(Context ctx){

        var anio = ctx.queryParamAsClass("anio", Integer.class).get();
        var mes = ctx.queryParamAsClass("mes", Integer.class).get();

        try {
            String parametroString = ctx.pathParam("colaboradorID");
            Long colabID = Long.parseLong(parametroString);
            var puntosDelColab = this.fachada.puntosJPA(colabID, entityManager, anio, mes);
            PuntosDeColaboradorDTO puntosDTOrta = new PuntosDeColaboradorDTO();
            puntosDTOrta.setPuntos(puntosDelColab);
            ctx.status(HttpStatus.FOUND);
            ctx.json(puntosDTOrta);

        }
        catch(NoSuchElementException ex){
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(ex.getLocalizedMessage());
        }
    }

    public void actualizar(Context ctx){
        var puntosBody = ctx.bodyAsClass(PuntosDTO.class);
        this.fachada.actualizarPesosPuntosJPA(puntosBody.getPesosDonados(), puntosBody.getViandasDistribuidas(),
                puntosBody.getViandasDonadas(), puntosBody.getTarjetasRepartidas(),
                puntosBody.getHeladerasActivas(), puntosBody.getArregloPeso());
        ctx.status(HttpStatus.OK);
        ctx.result("Formula actualizada correctamente");
    }

    public void falla(Context ctx){

        var heladera = ctx.bodyAsClass(HeladeraDTO.class);
        fachada.falla(heladera, entityManager);
    }

    public void donacionDinero(Context ctx){
        var idString = ctx.pathParam("colabID");
        Long id = Long.parseLong(idString);
        var dineroClass = ctx.bodyAsClass(DineroDTO.class);

        var resultado = this.fachada.donarDinero(id, dineroClass, entityManager);

        if(resultado){
            ctx.status(HttpStatus.OK);
            ctx.json(dineroClass);
        }
        else{
            ctx.status(HttpStatus.PRECONDITION_FAILED);
            ctx.result("El colaborador no es donador");
        }
    }

    public void suscribir(Context ctx) throws IOException {

        var idString = ctx.pathParam("colabID");
        Long idColab = Long.parseLong(idString);
        var heladeraBody = ctx.bodyAsClass(HeladeraDTO.class);

        this.fachada.suscribir(idColab, heladeraBody);

        ctx.status(HttpStatus.OK);
        ctx.json(heladeraBody);

    }

    public void evento(Context ctx){

        var evento = ctx.bodyAsClass(EventoDTO.class);

        this.fachada.evento(evento, entityManager);

    }

    public void modificarNotificacion(Context ctx){
        var idString = ctx.pathParam("colabID");
        Long idColab = Long.parseLong(idString);
        var nuevosTiposJSON = ctx.bodyAsClass(EventosSuscriptoDTO.class);
        var tipos = nuevosTiposJSON.getFormas();

        //mandarle a heladeras q me cambie los eventos a los q estoy suscripto

    }

}

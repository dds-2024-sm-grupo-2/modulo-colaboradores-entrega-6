package ar.edu.utn.dds.k3003.model.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.dtos.FormasDeColaborarDTO;
import ar.edu.utn.dds.k3003.model.dtos.PuntosDTO;
import ar.edu.utn.dds.k3003.model.dtos.PuntosDeColaboradorDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.NoSuchElementException;

public class ColaboradorController {
    private final Fachada fachada;
    private final EntityManagerFactory entityManagerFactory;

    public ColaboradorController(Fachada fachada, EntityManagerFactory entityManagerFactory) {
        this.fachada = fachada;
        this.entityManagerFactory = entityManagerFactory;
    }

    public void agregar(Context ctx){
        EntityManager em = entityManagerFactory.createEntityManager();

        em.getTransaction().begin();
        var colabDTO = ctx.bodyAsClass(ColaboradorDTO.class);
        var rtaDTO = this.fachada.agregarJPA(colabDTO, em);
        em.getTransaction().commit();
        em.close();

        ctx.status(HttpStatus.CREATED);
        ctx.result("Colaborador agregado correctamente");
        ctx.json(rtaDTO);
    }

    public void buscar(Context ctx){
        try{
            String parametroString = ctx.pathParam("colaboradorID");
            Long colabID = Long.parseLong(parametroString);
            ctx.status(HttpStatus.OK);
            ctx.result("Colaborador encontrado");
            ctx.json(fachada.buscarXId(colabID));
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
            var rtaDTO = fachada.modificar(colabID, cuerpoJSON.getFormas());

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

        try {
            String parametroString = ctx.pathParam("colaboradorID");
            Long colabID = Long.parseLong(parametroString);
            var puntosDelColab = this.fachada.puntos(colabID);
            PuntosDeColaboradorDTO puntosDTOrta = new PuntosDeColaboradorDTO();
            puntosDTOrta.setPuntos(puntosDelColab);
            ctx.status(HttpStatus.FOUND);
            ctx.result("Puntos obtenidos correctamente");
            ctx.json(puntosDTOrta);

        }
        catch(NoSuchElementException ex){
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(ex.getLocalizedMessage());
        }
    }

    public void actualizar(Context ctx){
        var puntosBody = ctx.bodyAsClass(PuntosDTO.class);
        this.fachada.actualizarPesosPuntos(puntosBody.getPesosDonados(), puntosBody.getViandasDistribuidas(),
                puntosBody.getViandasDonadas(), puntosBody.getTarjetasRepartidas(),
                puntosBody.getHeladerasActivas());
        ctx.status(HttpStatus.OK);
        ctx.result("Formula actualizada correctamente");
    }


}

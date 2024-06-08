package ar.edu.utn.dds.k3003.model.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class AgregarColaboradorController implements Handler {

    private final Fachada fachada;
    private EntityManagerFactory entityManagerFactory;

    public AgregarColaboradorController(Fachada fachada, EntityManagerFactory entityManagerFactory) {
        this.fachada = fachada;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void handle(Context ctx) throws Exception{
        EntityManager em =entityManagerFactory.createEntityManager();
        var colabDTO = ctx.bodyAsClass(ColaboradorDTO.class);
        var rtaDTO = this.fachada.agregar(colabDTO);
        Colaborador col = new Colaborador();
        col.setNombre(colabDTO.getNombre());
        col.setFormas(colabDTO.getFormas());

        em.getTransaction().begin();
        em.persist(col);
        em.getTransaction().commit();
        em.close();


        ctx.status(HttpStatus.CREATED);
        ctx.result("Colaborador agregado correctamente");
        ctx.json(rtaDTO);
    }

}

package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.Colaborador;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceIT {

    static EntityManagerFactory entityManagerFactory ;
    EntityManager entityManager ;

    @BeforeAll
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("tp24db");
    }
    @BeforeEach
    public void setup() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
    }
    @Test
    public void testConectar() {}
    @Test
    public void testGuardarYRecuperarCol() throws Exception {
        Colaborador col1 = new Colaborador();
        col1.setNombre("Pepe");
        entityManager.getTransaction().begin();
        entityManager.persist(col1);
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        Colaborador col2 = entityManager.find(Colaborador.class,1L);

        assertEquals(col1.getNombre(), col2.getNombre());
    }

}


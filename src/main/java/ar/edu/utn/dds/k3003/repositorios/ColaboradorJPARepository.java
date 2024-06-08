package ar.edu.utn.dds.k3003.repositorios;

import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.Colaborador;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class ColaboradorJPARepository {

    private EntityManager entityManager ;
    public ColaboradorJPARepository(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    public void save(Colaborador colaborador) {
        this.entityManager.persist(colaborador);
    }

    public Colaborador findById(Long id) {
        // Notar que esto no es la PK, queda poco consistente respecto al resto de las clases
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Colaborador> criteriaQuery = criteriaBuilder.createQuery(Colaborador.class);
        Root<Colaborador> root = criteriaQuery.from(Colaborador.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(
                root.get("id"), id));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public Collection<Colaborador> all(){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Colaborador> criteriaQuery = criteriaBuilder.createQuery(Colaborador.class);
        Root<Colaborador> root = criteriaQuery.from(Colaborador.class);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }



}

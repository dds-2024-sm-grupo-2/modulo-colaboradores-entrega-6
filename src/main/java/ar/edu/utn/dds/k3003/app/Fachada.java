package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;

@Getter
@Setter
public class Fachada implements FachadaColaboradores{

  private final ColaboradorRepository colaboradorRepository;
  private final ColaboradorMapper colaboradorMapper;
  private Double viandasDistribuidasPeso, viandasDonadasPeso;
  private FachadaViandas viandasFachada;
  private FachadaLogistica logisticaFachada;
  private static AtomicLong seqId = new AtomicLong();


  public Fachada(){
    this.colaboradorRepository = new ColaboradorRepository();
    this.colaboradorMapper = new ColaboradorMapper();
  }

  public ColaboradorDTO agregarJPA(ColaboradorDTO colaboradorDTO, EntityManager em) {
    Colaborador colaborador = new Colaborador();
    colaborador.setNombre(colaboradorDTO.getNombre());
    colaborador.setFormas(colaboradorDTO.getFormas());

    em.getTransaction().begin();
    Colaborador colabRta = this.colaboradorRepository.saveJPA(colaborador, em);
    em.getTransaction().commit();
    return colaboradorMapper.map(colabRta);
  }
  public ColaboradorDTO buscarXIdJPA(Long colaboradorId, EntityManager em){
    em.getTransaction().begin();
    Colaborador colab = colaboradorRepository.findByIdJPA(colaboradorId, em);
    em.getTransaction().commit();
    return colaboradorMapper.map(colab);
  }

  public ColaboradorDTO modificarJPA(
          Long colaboradorId, List<FormaDeColaborarEnum> nuevasFormasDeColaborar, EntityManager em){
    em.getTransaction().begin();
    colaboradorRepository.modificarFormasDeJPA(colaboradorId, nuevasFormasDeColaborar, em);
    em.getTransaction().commit();
    return this.buscarXIdJPA(colaboradorId, em);
  }

  public Double puntosJPA(Long colaboradorId, EntityManager em, Integer anio, Integer mes) {
    Double puntosCalculados =
            ((this.viandasDistribuidasPeso
                    * logisticaFachada.trasladosDeColaborador(colaboradorId, mes, anio).size()))
                    + (this.viandasDonadasPeso
                    * viandasFachada.viandasDeColaborador(colaboradorId, mes, anio).size());
    em.getTransaction().begin();
    Colaborador colaborador = em.find(Colaborador.class, colaboradorId);
    colaborador.setPuntos(puntosCalculados);
    em.getTransaction().commit();
    return puntosCalculados;
  }

  @Override
  public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO){
    Colaborador colaborador = new Colaborador();
    colaborador.setNombre(colaboradorDTO.getNombre());
    colaborador.setFormas(colaboradorDTO.getFormas());
    Colaborador colaboradorGuardado = this.colaboradorRepository.save(colaborador);
    colaboradorDTO.setId(colaboradorGuardado.getId());
    return colaboradorMapper.map(colaboradorGuardado);
  }
  @Override
  public ColaboradorDTO buscarXId(Long colaboradorId) {
    Colaborador colab = colaboradorRepository.findById(colaboradorId);
    return colaboradorMapper.map(colab);
  }
  @Override
  public ColaboradorDTO modificar(
          Long colaboradorId, List<FormaDeColaborarEnum> nuevasFormasDeColaborar) {
          colaboradorRepository.modificarFormasDe(colaboradorId, nuevasFormasDeColaborar);
          return this.buscarXId(colaboradorId);
  }

  @Override
  public void actualizarPesosPuntos(
      Double pesosDonados,
      Double viandasDistribuidas,
      Double viandasDonadas,
      Double tarjetasRepartidas,
      Double heladerasActivas) {
    this.setViandasDistribuidasPeso(viandasDistribuidas);
    this.setViandasDonadasPeso(viandasDonadas);
  }

  @Override
  public Double puntos(Long colaboradorId) {
    ColaboradorDTO colaboradorDTO = this.buscarXId(colaboradorId);
    Colaborador colaborador = colaboradorMapper.pam(colaboradorDTO);
    Double puntosCalculados =
        ((this.viandasDistribuidasPeso
                * logisticaFachada.trasladosDeColaborador(colaboradorId, 1, 2024).size()))
            + (this.viandasDonadasPeso
                * viandasFachada.viandasDeColaborador(colaboradorId, 1, 2024).size());
    colaborador.setPuntos(puntosCalculados);
    return puntosCalculados;
  }



  @Override
  public void setLogisticaProxy(FachadaLogistica logistica) {
    this.logisticaFachada = logistica;
  }

  @Override
  public void setViandasProxy(FachadaViandas viandas) {
    this.viandasFachada = viandas;
  }
}

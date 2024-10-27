package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladeraProxy;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.dtos.DineroDTO;
import ar.edu.utn.dds.k3003.model.dtos.EventoDTO;
import ar.edu.utn.dds.k3003.model.dtos.MiColaboradorDTO;
import ar.edu.utn.dds.k3003.model.enums.MisFormasDeColaborar;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.Getter;
import lombok.Setter;
import io.micrometer.core.instrument.Counter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Getter
@Setter
public class Fachada implements FachadaColaboradores{

  private final ColaboradorRepository colaboradorRepository;
  private final ColaboradorMapper colaboradorMapper;
  private Double viandasDistribuidasPeso, viandasDonadasPeso, dineroDonadoPeso, arregloPeso;
  private FachadaViandas viandasFachada;
  private FachadaLogistica logisticaFachada;
  private HeladeraProxy heladerasFachada;
  private static AtomicLong seqId = new AtomicLong();

  private PrometheusMeterRegistry registry;
  private Counter cantidadColaboradores;
  private Counter puntosTotal;

  public Fachada(){
    this.colaboradorRepository = new ColaboradorRepository();
    this.colaboradorMapper = new ColaboradorMapper();
  }

  public MiColaboradorDTO agregarJPA(MiColaboradorDTO colaboradorDTO, EntityManager em) {
    Colaborador colaborador = new Colaborador();
    colaborador.setNombre(colaboradorDTO.getNombre());
    colaborador.setFormas(colaboradorDTO.getFormas());
    colaborador.setPuntos((double)0);
    colaborador.setDineroDonado((double)0);
    colaborador.setHeladerasReparadas((double)0);

    em.getTransaction().begin();
    Colaborador colabRta = this.colaboradorRepository.saveJPA(colaborador, em);
    em.getTransaction().commit();
    cantidadColaboradores.increment();
    return colaboradorMapper.map(colabRta);
  }
  public MiColaboradorDTO buscarXIdJPA(Long colaboradorId, EntityManager em){
    em.getTransaction().begin();
    Colaborador colab = colaboradorRepository.findByIdJPA(colaboradorId, em);
    em.getTransaction().commit();
    return colaboradorMapper.map(colab);
  }

  public MiColaboradorDTO modificarJPA(
          Long colaboradorId, List<MisFormasDeColaborar> nuevasFormasDeColaborar, EntityManager em){
    em.getTransaction().begin();
    colaboradorRepository.modificarFormasDeJPA(colaboradorId, nuevasFormasDeColaborar, em);
    em.getTransaction().commit();
    return this.buscarXIdJPA(colaboradorId, em);
  }

  public Double puntosJPA(Long colaboradorId, EntityManager em, Integer anio, Integer mes) {
    Double puntosCalculados =
            ((this.viandasDistribuidasPeso * logisticaFachada.trasladosDeColaborador(colaboradorId, mes, anio).size()))
                    + (this.viandasDonadasPeso * viandasFachada.viandasDeColaborador(colaboradorId, mes, anio).size());
    em.getTransaction().begin();
    Colaborador colaborador = em.find(Colaborador.class, colaboradorId);
    puntosCalculados = puntosCalculados + (colaborador.getDineroDonado() * dineroDonadoPeso)
            + (colaborador.getHeladerasReparadas() * arregloPeso);
    colaborador.setPuntos(puntosCalculados);
    em.getTransaction().commit();
    puntosTotal.increment(puntosCalculados);
    return puntosCalculados;
  }

  public boolean donarDinero(Long id , DineroDTO dineroDonado, EntityManager em){

    Double dinero = dineroDonado.getDineroDonado();
    boolean resul;

    em.getTransaction().begin();
    Colaborador colab = em.find(Colaborador.class, id);
    if(colab.getFormas().contains(MisFormasDeColaborar.DONADORDEDINERO)){
    Double dineroAnterior = colab.getDineroDonado();
    Double dineroNuevo = dineroAnterior + dinero;
    colab.setDineroDonado(dineroNuevo);
    resul = true;
    }else{
      resul = false;
    }
    em.getTransaction().commit();

    return resul;
  }

  public void actualizarPesosPuntosJPA(
          Double pesosDonados,
          Double viandasDistribuidas,
          Double viandasDonadas,
          Double tarjetasRepartidas,
          Double heladerasActivas,
          Double reparacionJPA) {
    this.setViandasDistribuidasPeso(viandasDistribuidas);
    this.setViandasDonadasPeso(viandasDonadas);
    this.setDineroDonadoPeso(pesosDonados);
    this.setArregloPeso(reparacionJPA);
  }

  public void suscribir(Long id, HeladeraDTO heladeraDTO) throws IOException {

    HeladeraDTO dtoRTA = heladerasFachada.suscribir(id, heladeraDTO);
  }

  public void evento(EventoDTO evento, EntityManager em){

        List<Long> ids = evento.getListaColabIDS();

        TypedQuery<Colaborador> query = em.createQuery(
                "SELECT c FROM Colaborador c WHERE c.id IN :ids", Colaborador.class);
        // Establecemos el parámetro
        query.setParameter("ids", ids);
        // Ejecutamos la consulta y devolvemos los resultados
        List<Colaborador> colabs =  query.getResultList();

        for(Colaborador colaborador : colabs){
          colaborador.notificar(evento);
        }
  }

  public void falla(HeladeraDTO heladera, EntityManager em){
    //TODO:
    //Detectar el tipo de falla / alerta
    //Elegir al colaborador que sea tecnico y este suscrito a la heladera
    //Reparar la heladera (mensaje a heladera y actualizar BD al colab)
  }

  public void setRegistry(PrometheusMeterRegistry registry){
    this.registry = registry;

    this.cantidadColaboradores = Counter.builder("app.colaboradores.counter").
            description("Cantidad de colaboradores").register(registry);
    this.puntosTotal = Counter.builder("app.puntos.totales").
            description("Cantidad de puntos totales").register(registry);
  }

  @Override
  public void setLogisticaProxy(FachadaLogistica logistica) {
    this.logisticaFachada = logistica;
  }

  @Override
  public void setViandasProxy(FachadaViandas viandas) {
    this.viandasFachada = viandas;
  }

  public void setHeladerasProxy(HeladeraProxy heladeras) {
    this.heladerasFachada = heladeras;
  }


  // Overrides de la fachada-------------------------------------------------------------------------------------
  //-------------------------------------------------------------------------------------------------------------
  @Override
  public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO){
    Colaborador colaborador = new Colaborador();
    colaborador.setNombre(colaboradorDTO.getNombre());
    //colaborador.setFormas(colaboradorDTO.getFormas());
    Colaborador colaboradorGuardado = this.colaboradorRepository.save(colaborador);
    colaboradorDTO.setId(colaboradorGuardado.getId());
    return colaboradorDTO;
  }
  @Override
  public ColaboradorDTO buscarXId(Long colaboradorId) {
    Colaborador colab = colaboradorRepository.findById(colaboradorId);
    List<FormaDeColaborarEnum> formas = new ArrayList<>();
    formas.add(FormaDeColaborarEnum.DONADOR);
    return new ColaboradorDTO(colab.getNombre(),formas);
  }
  @Override
  public ColaboradorDTO modificar(
          Long colaboradorId, List<FormaDeColaborarEnum> nuevasFormasDeColaborar) {
          //colaboradorRepository.modificarFormasDe(colaboradorId, nuevasFormasDeColaborar);
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
    this.setDineroDonadoPeso(pesosDonados);
  }

  @Override
  public Double puntos(Long colaboradorId) {
    ColaboradorDTO colaboradorDTO = this.buscarXId(colaboradorId);
    //Colaborador colaborador = colaboradorMapper.pam(colaboradorDTO);
    Double puntosCalculados =
        ((this.viandasDistribuidasPeso
                * logisticaFachada.trasladosDeColaborador(colaboradorId, 1, 2024).size()))
            + (this.viandasDonadasPeso
                * viandasFachada.viandasDeColaborador(colaboradorId, 1, 2024).size());
    //colaborador.setPuntos(puntosCalculados);
    return puntosCalculados;
  }
}

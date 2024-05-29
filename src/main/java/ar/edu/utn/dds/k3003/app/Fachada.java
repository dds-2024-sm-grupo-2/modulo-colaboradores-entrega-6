package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorRepository;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaColaboradores {

  private final ColaboradorRepository colaboradorRepository;
  private final ColaboradorMapper colaboradorMapper;
  private Double viandasDistribuidasPeso, viandasDonadasPeso;
  private FachadaViandas viandasFachada;
  private FachadaLogistica logisticaFachada;

  public Fachada() {
    this.colaboradorRepository = new ColaboradorRepository();
    this.colaboradorMapper = new ColaboradorMapper();
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
  public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO) {
    Colaborador colaborador = new Colaborador(colaboradorDTO.getNombre());
    colaborador.setFormas(colaboradorDTO.getFormas());
    Colaborador colaboradorGuardado = this.colaboradorRepository.save(colaborador);
    colaboradorDTO.setId(colaboradorGuardado.getId());
    return colaboradorMapper.map(colaboradorGuardado);
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
  public ColaboradorDTO buscarXId(Long colaboradorId) {
    Colaborador colab = colaboradorRepository.findById(colaboradorId);
    return colaboradorMapper.map(colab);
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

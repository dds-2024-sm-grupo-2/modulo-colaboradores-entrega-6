package ar.edu.utn.dds.k3003.repositorios;

import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;

public class ColaboradorMapper {

  public ColaboradorMapper() {}

  public ColaboradorDTO map(Colaborador colaborador) {
    ColaboradorDTO colaboradorDTO =
        new ColaboradorDTO(colaborador.getNombre(), colaborador.getFormas());
    colaboradorDTO.setId(colaborador.getId());
    return colaboradorDTO;
  }

  public Colaborador pam(ColaboradorDTO colaboradorDTO) {
    Colaborador colaborador = new Colaborador(colaboradorDTO.getNombre());
    colaborador.setFormas(colaboradorDTO.getFormas());
    colaborador.setId(colaboradorDTO.getId());
    return colaborador;
  }
}

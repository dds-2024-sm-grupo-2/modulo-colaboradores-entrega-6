package ar.edu.utn.dds.k3003.repositorios;

import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.dtos.MiColaboradorDTO;

import java.util.concurrent.atomic.AtomicLong;

public class ColaboradorMapper {

  public ColaboradorMapper() {}

  public MiColaboradorDTO map(Colaborador colaborador) {
    MiColaboradorDTO colaboradorDTO =
        new MiColaboradorDTO(colaborador.getNombre(), colaborador.getFormas());
    colaboradorDTO.setId(colaborador.getId());
    return colaboradorDTO;
  }

  public Colaborador pam(MiColaboradorDTO colaboradorDTO) {
    Colaborador colaborador = new Colaborador();
    colaborador.setNombre(colaboradorDTO.getNombre());
    colaborador.setFormas(colaboradorDTO.getFormas());
    colaborador.setId(colaboradorDTO.getId());
    return colaborador;
  }
}

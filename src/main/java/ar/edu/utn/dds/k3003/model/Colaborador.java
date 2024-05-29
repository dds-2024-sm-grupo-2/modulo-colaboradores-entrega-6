package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Colaborador {
  private Long id;
  private String nombre;
  private List<FormaDeColaborarEnum> formas;
  private Double puntos = (double) 0;

  public Colaborador(String nombreColab) {
    super();
    this.nombre = nombreColab;
    this.formas = new ArrayList<>();
  }
  public Colaborador(){
    super();
  }
}

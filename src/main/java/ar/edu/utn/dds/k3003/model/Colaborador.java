package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
@Entity
@Table(name = "colaborador")
public class Colaborador {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column (name = "nombre")
  private String nombre;
  @Column
  private Double puntos = (double) 0;

  @Transient
  private List<FormaDeColaborarEnum> formas;

  public Colaborador() {}
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getPuntos() {
    return puntos;
  }

  public void setPuntos(Double puntos) {
    this.puntos = puntos;
  }

  public List<FormaDeColaborarEnum> getFormas() {
    return formas;
  }

  public void setFormas(List<FormaDeColaborarEnum> formas) {
    this.formas = formas;
  }
}

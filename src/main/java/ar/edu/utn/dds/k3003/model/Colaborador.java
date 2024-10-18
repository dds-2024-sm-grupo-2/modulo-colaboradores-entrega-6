package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
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

  @ElementCollection(targetClass = FormaDeColaborarEnum.class)
  @Enumerated(EnumType.STRING) // Puedes usar EnumType.ORDINAL si prefieres almacenar índices
  @CollectionTable(name = "colaborador_formas_de_colaborar", joinColumns = @JoinColumn(name = "colaborador_id"))
  @Column(name = "forma_de_colaborar")
  private List<FormaDeColaborarEnum> formas;

  @Column
  private Double dineroDonado;

  @Column
  private Double heladerasReparadas;

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

  public Double getDineroDonado() {
    return dineroDonado;
  }

  public void setDineroDonado(Double dineroDonado) {
    this.dineroDonado = dineroDonado;
  }

  public Double getHeladerasReparadas() {
    return heladerasReparadas;
  }

  public void setHeladerasReparadas(Double heladerasReparadas) {
    this.heladerasReparadas = heladerasReparadas;
  }
}

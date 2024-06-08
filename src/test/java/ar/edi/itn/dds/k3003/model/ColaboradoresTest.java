package ar.edi.itn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositorios.ColaboradorRepository;
import ar.edu.utn.dds.k3003.tests.TestTP;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ColaboradoresTest implements TestTP<FachadaColaboradores> {

  FachadaColaboradores instanciaFachada;
  final String nombre1 = "Santino";
  ColaboradorRepository repoColab;
  ColaboradorMapper colabMapper;

  @Mock FachadaViandas viandas;
  @Mock FachadaLogistica logistica;

  @SneakyThrows
  @BeforeEach
  void setUp() {
    repoColab = new ColaboradorRepository();
    instanciaFachada = this.instance();
    colabMapper = new ColaboradorMapper();
    instanciaFachada.setLogisticaProxy(logistica);
    instanciaFachada.setViandasProxy(viandas);
  }

  @Test
  @DisplayName("Test agregar colaborador")
  void testAgregarColaborador() {

    Colaborador colaborador1 = new Colaborador();
    colaborador1.setNombre(this.nombre1);
    ColaboradorDTO colaborador1DTO = colabMapper.map(colaborador1);
    ColaboradorDTO rtaDTO = instanciaFachada.agregar(colaborador1DTO);
    assertNotNull(rtaDTO.getId());
    System.out.print(rtaDTO.getId() + "\n");

    Colaborador colaborador2 = new Colaborador();
    colaborador2.setNombre("Pepe");
    ColaboradorDTO colab2DTO = colabMapper.map(colaborador2);
    ColaboradorDTO rtaDTO2 = instanciaFachada.agregar(colab2DTO);
    assertNotNull(rtaDTO2.getId());
    System.out.print(rtaDTO2.getId() + "\n");
  }

  @Test
  @DisplayName("Test buscar colaboradorXID")
  void testBuscarXID() {

    Colaborador colaborador1 = new Colaborador();
    colaborador1.setNombre(this.nombre1);
    ColaboradorDTO colaborador1DTO = colabMapper.map(colaborador1);
    ColaboradorDTO rtaDTO = instanciaFachada.agregar(colaborador1DTO);
    ColaboradorDTO rtaColaboradorDTO = instanciaFachada.buscarXId(rtaDTO.getId());
    assertEquals(rtaColaboradorDTO.getId(), rtaDTO.getId());
    assertEquals(rtaColaboradorDTO.getNombre(), rtaDTO.getNombre());
    assertEquals(rtaColaboradorDTO.getFormas(), rtaDTO.getFormas());
    System.out.print("El colaborador agregado y el buscadoXID son el mismo. \n");
  }

  @Test
  @DisplayName("Test actualizar pesos de puntos")
  void testActualizarPesosPuntos() {

    Double pesoDonadas = 1.5;
    Double pesoDistribuidas = (double) 1;
    instanciaFachada.actualizarPesosPuntos(0.0, pesoDistribuidas, pesoDonadas, 0.0, 0.0);
    assertEquals(pesoDistribuidas, 1);
    assertEquals(pesoDonadas, 1.5);
    System.out.print("Los pesos son iguales \n");

    instanciaFachada.actualizarPesosPuntos(0.0, (double) 2, 2.5, 0.0, 0.0);
    assertNotEquals(pesoDistribuidas, 2);
    assertNotEquals(pesoDonadas, 2.5);
    System.out.print("Los pesos no son iguales, y es lo correcto \n");
  }

  @Test
  @DisplayName("Test modificar formas de colaborar")
  void testModificarFormas() {

    Colaborador colaborador1 = new Colaborador();
    colaborador1.setNombre(this.nombre1);
    ColaboradorDTO colaborador1DTO = colabMapper.map(colaborador1);
    ColaboradorDTO rtaDTO = instanciaFachada.agregar(colaborador1DTO);
    List<FormaDeColaborarEnum> nuevasFormas = new ArrayList<>();
    nuevasFormas.add(FormaDeColaborarEnum.DONADOR);
    nuevasFormas.add(FormaDeColaborarEnum.TRANSPORTADOR);
    ColaboradorDTO formasCambiadasDTO = instanciaFachada.modificar(rtaDTO.getId(), nuevasFormas);
    assertNotNull(formasCambiadasDTO.getId());
    assertEquals(formasCambiadasDTO.getFormas(), nuevasFormas);
    System.out.print("Las formas cambiaron correctamente \n");
  }

  @Test
  @DisplayName("Test calculo de puntos")
  void testPuntos() {
    Double pesoDonadas = 1.5;
    Double pesoDistribuidas = (double) 1;

    Colaborador colaborador1 = new Colaborador();
    colaborador1.setNombre(this.nombre1);
    ColaboradorDTO colaborador1DTO = colabMapper.map(colaborador1);
    ColaboradorDTO rtaDTO = instanciaFachada.agregar(colaborador1DTO);
    instanciaFachada.actualizarPesosPuntos(0.0, pesoDistribuidas, pesoDonadas, 0.0, 0.0);

    when(logistica.trasladosDeColaborador(rtaDTO.getId(), 1, 2024)).thenReturn(List.of());
    when(viandas.viandasDeColaborador(rtaDTO.getId(), 1, 2024)).thenReturn(List.of());
    Double puntosDeColaborador = instanciaFachada.puntos(rtaDTO.getId());
    assertEquals(0, puntosDeColaborador);
    System.out.print("El colaborador tiene 0 puntos. Correcto! \n");

    var viandaDTO =
        new ViandaDTO("y", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, rtaDTO.getId(), 20);
    when(logistica.trasladosDeColaborador(rtaDTO.getId(), 1, 2024)).thenReturn(List.of());
    when(viandas.viandasDeColaborador(rtaDTO.getId(), 1, 2024)).thenReturn(List.of(viandaDTO));
    Double puntosDeColaborador1 = instanciaFachada.puntos(rtaDTO.getId());
    assertEquals(1.5, puntosDeColaborador1);
    System.out.print(
        "El colaborador tiene 1.5 puntos, equivalente a una vianda donada y 0 distribuidas."
            + " Correcto! \n");

    var trasladoDTO = new TrasladoDTO("x", 18, 19);
    when(logistica.trasladosDeColaborador(rtaDTO.getId(), 1, 2024))
        .thenReturn(List.of(trasladoDTO));
    when(viandas.viandasDeColaborador(rtaDTO.getId(), 1, 2024)).thenReturn(List.of(viandaDTO));

    Double puntosDeColaborador2 = instanciaFachada.puntos(rtaDTO.getId());
    assertEquals(2.5, puntosDeColaborador2);
    System.out.print(
        "El colaborador tiene 2.5 puntos, equivalente a una vianda donada y una vianda"
            + " distribuidas. Correcto! \n");
  }

  @Override
  public String paquete() {
    return PAQUETE_BASE + "tests.colaboradores";
  }

  @Override
  public Class<FachadaColaboradores> clase() {
    return FachadaColaboradores.class;
  }
}

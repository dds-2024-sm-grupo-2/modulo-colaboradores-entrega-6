package ar.edu.utn.dds.k3003.model.dtos;

import ar.edu.utn.dds.k3003.model.enums.EstadoIncidenteEnum;
import ar.edu.utn.dds.k3003.model.enums.TipoIncidenteEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Incidentes")
public class IncidenteDTO {

    @Id
    private Long id;
    @Column(name = "tipo")
    private TipoIncidenteEnum tipoIncidente;
    @Column(name = "heladeraID")
    private Long heladeraId;
    @Column(name = "estadoIncidente")
    private EstadoIncidenteEnum estadoIncidente;
    @Column(name = "excedeTemperatura")
    private boolean excedeTemperatura;
    @Column(name = "valorExceso")
    private Integer excesoTemperatura;
    @Column(name = "tiempoSinRespuesta")
    private Integer tiempoSinRespuesta;
    @Column(name = "fechaCreacion")
    private LocalDateTime fechaCreacion;

    public IncidenteDTO(TipoIncidenteEnum tipoIncidente, Long heladeraId, EstadoIncidenteEnum estadoIncidente,
                        boolean excedeTemperatura, Integer excesoTemperatura, Integer tiempoSinRespuesta) {
        this.tipoIncidente = tipoIncidente;
        this.heladeraId = heladeraId;
        this.estadoIncidente = estadoIncidente;
        this.excedeTemperatura = excedeTemperatura;
        this.excesoTemperatura = excesoTemperatura;
        this.tiempoSinRespuesta = tiempoSinRespuesta;
    }
}

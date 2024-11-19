package ar.edu.utn.dds.k3003.model.dtos;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Notificaciones")
public class NotificacionDTO {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;
    @ElementCollection
    @CollectionTable(name = "notificaciones_colaboradores", joinColumns = @JoinColumn(name = "notificacion_id"))
    @Column(name = "colaborador_id")
    List<Long> idColab;
    @Column(name = "mensaje", nullable = false, length = 500)
    String msg;

    public List<Long> getIdColab() {
        return idColab;
    }

    public void setIdColab(List<Long> idColab) {
        this.idColab = idColab;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

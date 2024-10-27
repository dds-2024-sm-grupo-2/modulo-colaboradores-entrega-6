package ar.edu.utn.dds.k3003.model.dtos;

import ar.edu.utn.dds.k3003.model.enums.EventTypeEnum;

import java.util.List;

public class EventosSuscriptoDTO {
    private List<EventTypeEnum> tipos;

    public List<EventTypeEnum> getFormas() {
        return tipos;
    }

    public void setFormas(List<EventTypeEnum> tiposs) {
        this.tipos = tiposs;
    }
}

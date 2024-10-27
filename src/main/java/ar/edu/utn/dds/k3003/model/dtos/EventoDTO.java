package ar.edu.utn.dds.k3003.model.dtos;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;

import java.util.List;

public class EventoDTO {

    List<Long> listaColabIDS;
    HeladeraDTO heladera;
    Integer eventType;

    public List<Long> getListaColabIDS() {
        return listaColabIDS;
    }

    public void setListaColabIDS(List<Long> listaColabIDS) {
        this.listaColabIDS = listaColabIDS;
    }

    public HeladeraDTO getHeladera() {
        return heladera;
    }

    public void setHeladera(HeladeraDTO heladera) {
        this.heladera = heladera;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }
}

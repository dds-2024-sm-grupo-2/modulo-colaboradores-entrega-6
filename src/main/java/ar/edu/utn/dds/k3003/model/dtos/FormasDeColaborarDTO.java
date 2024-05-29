package ar.edu.utn.dds.k3003.model.dtos;

import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;

import java.util.List;

public class FormasDeColaborarDTO {

    private List<FormaDeColaborarEnum> formas;

    public List<FormaDeColaborarEnum> getFormas() {
        return formas;
    }

    public void setFormas(List<FormaDeColaborarEnum> formas) {
        this.formas = formas;
    }
}

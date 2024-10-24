package ar.edu.utn.dds.k3003.model.dtos;

import ar.edu.utn.dds.k3003.model.enums.MisFormasDeColaborar;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MiColaboradorDTO {

    private Long id;
    private String nombre;
    private List<MisFormasDeColaborar> formas;

    public MiColaboradorDTO(String nombre, List<MisFormasDeColaborar> formas) {
        this.nombre = nombre;
        this.formas = formas;
    }
}

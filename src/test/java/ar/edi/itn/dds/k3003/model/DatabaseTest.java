package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.Colaborador;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import java.util.List;

public class DatabaseTest {

    @Test
    public void persistirUnColaborador(){

        Colaborador colaborador = new Colaborador("Santino");
        colaborador.setPuntos(2.5);
        colaborador.setId(0L);


    }

}

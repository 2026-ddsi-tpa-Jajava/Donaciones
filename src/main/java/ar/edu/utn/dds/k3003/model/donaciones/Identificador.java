package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import lombok.Getter;

public class Identificador {

    private TipoIdentificadorEnum tipo;
    @Getter
    private String id;

    public Identificador(TipoIdentificadorEnum tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }
}

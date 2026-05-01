package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;

public class Identificador {

    private TipoIdentificadorEnum tipo;
    private String id;

    public Identificador(TipoIdentificadorEnum tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }
}

package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;

public interface TipoIdentificador {

    boolean esValido(Producto producto);

    TipoIdentificadorEnum getEnum();
}


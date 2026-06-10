package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;

public interface ValidadorIdentificador {
    boolean esValido(String nombre, String descripcion);
    TipoIdentificadorEnum getTipo();
}

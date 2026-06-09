package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.model.Identificador;

public class IdentificadoresDataMapper {

    public IdentificadorDTO toIdentificadorDTO(Identificador identificador) {
        return new IdentificadorDTO(
                identificador.getId().toString(),
                identificador.getTipo(),
                identificador.getDescripcion()
        );
    }
}

package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import ar.edu.utn.dds.k3003.model.Subcategoria;

public class SubcategoriaDataMapper {

    public SubcategoriaDTO toSubategoriaDTO(Subcategoria subcategoria) {
        return new SubcategoriaDTO(
                subcategoria.getId().toString(),
                subcategoria.getNombre(),
                subcategoria.getCategoria().getId().toString()
        );
    }
}

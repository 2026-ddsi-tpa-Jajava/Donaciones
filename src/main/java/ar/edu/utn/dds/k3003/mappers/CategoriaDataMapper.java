package ar.edu.utn.dds.k3003.mappers;


import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.model.Categoria;


public class CategoriaDataMapper {

    public CategoriaDTO toCategoriaDTO(Categoria categoria) {
        return new CategoriaDTO(
                categoria.getId() != null ? categoria.getId().toString() : null,
                categoria.getNombre(),
                categoria.getDescripcion(),
                null
        );
    }
}

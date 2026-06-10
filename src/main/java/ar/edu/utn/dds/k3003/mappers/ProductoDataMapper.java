package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.Producto;

public class ProductoDataMapper {

    public ProductoDTO toProductoDTO(Producto producto) {
        return new ProductoDTO(
                producto.getId().toString(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getSubcategoria() != null ? producto.getSubcategoria().getId().toString() : null,
                producto.getIdentificador() != null ? producto.getIdentificador().getId().toString() : null
        );
    }
}

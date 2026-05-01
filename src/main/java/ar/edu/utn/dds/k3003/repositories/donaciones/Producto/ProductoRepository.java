package ar.edu.utn.dds.k3003.repositories.donaciones.Producto;

import ar.edu.utn.dds.k3003.model.donaciones.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> buscarPorId(String id);

    Producto guardar(Producto producto);

    void actualizar(Producto producto);

    List<Producto> buscarPorSubcategoria(String subcategoriaID);
}

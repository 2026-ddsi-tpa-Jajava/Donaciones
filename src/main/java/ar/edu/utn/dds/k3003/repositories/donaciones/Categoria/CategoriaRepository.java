package ar.edu.utn.dds.k3003.repositories.donaciones.Categoria;

import ar.edu.utn.dds.k3003.model.donaciones.Categoria;
import ar.edu.utn.dds.k3003.model.donaciones.Subcategoria;

import java.util.Optional;

public interface CategoriaRepository {
    Optional<Categoria> buscarCategoriaPorId(String categoriaID);

    Optional<Subcategoria> buscarSubcategoriaPorId(String subcategoriaID);

    Categoria guardar(Categoria nuevaCategoria);

    String generarIdSubcategoria();

    void actualizar(Categoria categoria);
}

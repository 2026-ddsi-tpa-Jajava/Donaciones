package ar.edu.utn.dds.k3003.repositories.donaciones.categoria;

import ar.edu.utn.dds.k3003.model.donaciones.Categoria;
import ar.edu.utn.dds.k3003.model.donaciones.Subcategoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCategoriaRepo implements CategoriaRepository {

    private List<Categoria> categorias;
    private AtomicLong idSecuenciaCategoria;
    private AtomicLong idSecuenciaSubcategoria;

    public InMemoryCategoriaRepo() {
        this.categorias = new ArrayList<>();
        this.idSecuenciaCategoria = new AtomicLong(1);
        this.idSecuenciaSubcategoria = new AtomicLong(1);
    }

    @Override
    public Optional<Categoria> buscarCategoriaPorId(String categoriaID) {
        return this.categorias.stream().filter(categoria -> categoria.tieneID(categoriaID)).findFirst();
    }

    @Override
    public Optional<Subcategoria> buscarSubcategoriaPorId(String subcategoriaID) {
        return this.categorias.stream()
                .flatMap(categoria -> categoria.getSubcategorias().stream())
                .filter(sub -> sub.getId().equals(subcategoriaID))
                .findFirst();
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        categoria.setId(this.generarIdCategoria());
        this.categorias.add(categoria);

        return categoria;
    }

    private String generarIdCategoria() {
        return String.valueOf(this.idSecuenciaCategoria.getAndIncrement());
    }

    public String generarIdSubcategoria() {
        return String.valueOf(this.idSecuenciaSubcategoria.getAndIncrement());
    }

    @Override
    public void actualizar(Categoria categoria) {
    }
}

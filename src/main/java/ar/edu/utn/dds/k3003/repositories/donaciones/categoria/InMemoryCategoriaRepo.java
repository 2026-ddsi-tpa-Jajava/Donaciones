package ar.edu.utn.dds.k3003.repositories.donaciones.categoria;

import ar.edu.utn.dds.k3003.exceptions.donaciones.CategoriaNoEncontradaException;
import ar.edu.utn.dds.k3003.exceptions.donaciones.ProductoNoEncontradoException;
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
    public Categoria guardar(Categoria categoria) {
        categoria.setId(this.generarIdCategoria());
        categoria.setSubcategoriaID(this.generarIdSubcategoria());
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

    @Override
    public List<Categoria> buscarTodos() {
        return new ArrayList<>(this.categorias);
    }

    @Override
    public void eliminarPorId(String id) {
        boolean eliminado = this.categorias.removeIf(categoria -> categoria.tieneID(id));

        if (!eliminado) {
            throw new CategoriaNoEncontradaException("No se encontró la categoria con ID: " + id);
        }
    }
}

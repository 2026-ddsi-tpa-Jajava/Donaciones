package ar.edu.utn.dds.k3003.repositories.donaciones.Producto;

import ar.edu.utn.dds.k3003.model.donaciones.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProductoRepo implements ProductoRepository {

    private List<Producto> productos;

    public InMemoryProductoRepo() {
        this.productos = new ArrayList<>();
    }

    @Override
    public Optional<Producto> buscarPorId(String id) {
        return this.productos.stream().filter(producto -> producto.tieneID(id)).findFirst();
    }

    @Override
    public Producto guardar(Producto producto) {
        this.productos.add(producto);

        return producto;
    }

    @Override
    public void actualizar(Producto producto) {
    }

    @Override
    public List<Producto> buscarPorSubcategoria(String subcategoriaID) {
        return this.productos.stream().filter(producto -> producto.tieneSubcategoria(subcategoriaID))
                .toList();
    }

}

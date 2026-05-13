package ar.edu.utn.dds.k3003.repositories.donaciones.Producto;

import ar.edu.utn.dds.k3003.model.donaciones.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductoRepo implements ProductoRepository {

    private List<Producto> productos;
    private AtomicLong idSecuencia;

    public InMemoryProductoRepo() {
        this.productos = new ArrayList<>();
        this.idSecuencia = new AtomicLong(1);
    }

    @Override
    public Optional<Producto> buscarPorId(String id) {
        return this.productos.stream().filter(producto -> producto.tieneID(id)).findFirst();
    }

    @Override
    public Producto guardar(Producto producto) {
        producto.setId(this.generarID());
        this.productos.add(producto);

        return producto;
    }

    private String generarID() {
        return String.valueOf(this.idSecuencia.getAndIncrement());
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

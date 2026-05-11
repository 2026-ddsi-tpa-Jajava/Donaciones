package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.exceptions.donaciones.DonacionNoEncontradaException;
import ar.edu.utn.dds.k3003.exceptions.donaciones.ProductoNoEncontradoException;
import ar.edu.utn.dds.k3003.model.donaciones.Categoria;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.model.donaciones.Producto;
import ar.edu.utn.dds.k3003.model.donaciones.Subcategoria;
import ar.edu.utn.dds.k3003.repositories.donaciones.Categoria.CategoriaRepository;
import ar.edu.utn.dds.k3003.repositories.donaciones.Categoria.InMemoryCategoriaRepo;
import ar.edu.utn.dds.k3003.repositories.donaciones.Donacion.DonacionesDataMapper;
import ar.edu.utn.dds.k3003.repositories.donaciones.Donacion.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.donaciones.Donacion.InMemoryDonacionesRepo;
import ar.edu.utn.dds.k3003.repositories.donaciones.Producto.InMemoryProductoRepo;
import ar.edu.utn.dds.k3003.repositories.donaciones.Producto.ProductoRepository;
import lombok.val;

import java.time.LocalDate;
import java.util.List;

public class DonacionesService {

    private DonacionesRepository donacionesRepository;
    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;


    public DonacionesService() {
        this.donacionesRepository = new InMemoryDonacionesRepo();
        this.productoRepository = new InMemoryProductoRepo();
        this.categoriaRepository = new InMemoryCategoriaRepo();

        this.cargarEnCatalogo("producto1");
    }

    private void cargarEnCatalogo(String productoID) {
    Categoria alimentos = new Categoria("Alimentos", "");
    Subcategoria arroz = new Subcategoria("1","Arroz", "", alimentos);


    Producto producto = new Producto(
            productoID,
            "",
            "",
            arroz
    );
    this.productoRepository.guardar(producto);
  }

    public Donacion gestionarDonacionRecibida(Donacion donacion, String productoID) {
        val producto = this.buscarProducto(productoID);
        donacion.setProducto(producto);
        val donacionRegistrada = this.donacionesRepository.guardar(donacion);

        return donacionRegistrada;
    }

    public void realizarQueja() {
        // TODO
    }

    public void consultarDonacion() {
        // TODO
    }

    public void consultaEstadoDonador() {
        // TODO
    }

    public void incrementarStock() {
        // TODO
    }

    public Producto buscarProducto(String productoID) {
        val producto = this.productoRepository.buscarPorId(productoID);

        if (producto.isEmpty())
        {
            throw new ProductoNoEncontradoException("No se encontró el producto con ID " + productoID);
        }

        return producto.get();
    }

    public Donacion buscarDonacionPorId(String donacionID) {
        val donacion = this.donacionesRepository.buscarPorId(donacionID);

        if (donacion.isEmpty())
        {
            throw new DonacionNoEncontradaException("No se encontró donación con ID " + donacionID);
        }

        return donacion.get();
    }

    public List<Donacion> buscarDonacionPorDonadorYFechaInicio(String donadorID, LocalDate fecha) {
        val donaciones = this.donacionesRepository.buscarPorIDyFecha(donadorID, fecha);

        if (donaciones.isEmpty()) {
          throw new DonacionNoEncontradaException
                  ("No se encontraron donaciones para el donador con ID " + donadorID + "A partir de la fecha " + fecha);
        }

        return donaciones;
    }

    public Donacion cambiarEstadoDonacion(String donacionID, EstadoDonacionEnum estado) {
        val donacion = this.buscarDonacionPorId(donacionID);

        donacion.cambiarEstado(estado);
        this.donacionesRepository.actualizar(donacion);

        return donacion;
    }

    public Donacion registrarQueja(Donacion donacion, String descripcion) {
        donacion.agregarQueja(descripcion);
        this.donacionesRepository.actualizar(donacion);

        return donacion;
    }
}

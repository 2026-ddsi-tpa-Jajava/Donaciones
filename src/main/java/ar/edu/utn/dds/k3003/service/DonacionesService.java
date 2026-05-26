package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.exceptions.donaciones.*;
import ar.edu.utn.dds.k3003.model.donaciones.*;
import ar.edu.utn.dds.k3003.repositories.donaciones.categoria.CategoriaRepository;
import ar.edu.utn.dds.k3003.repositories.donaciones.categoria.InMemoryCategoriaRepo;
import ar.edu.utn.dds.k3003.repositories.donaciones.donacion.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.donaciones.donacion.InMemoryDonacionesRepo;
import ar.edu.utn.dds.k3003.repositories.donaciones.producto.InMemoryProductoRepo;
import ar.edu.utn.dds.k3003.repositories.donaciones.producto.ProductoRepository;
import ar.edu.utn.dds.k3003.repositories.donaciones.identificador.IdentificadoresRepository;
import ar.edu.utn.dds.k3003.repositories.donaciones.identificador.InMemoryIdentificadoresRepo;
import lombok.val;

import java.time.LocalDate;
import java.util.List;

public class DonacionesService {

    private DonacionesRepository donacionesRepository;
    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;
    private IdentificadoresRepository identificadoresRepository;


    public DonacionesService() {
        this.donacionesRepository = new InMemoryDonacionesRepo();
        this.productoRepository = new InMemoryProductoRepo();
        this.categoriaRepository = new InMemoryCategoriaRepo();
        this.identificadoresRepository = new InMemoryIdentificadoresRepo();
    }

    public Donacion gestionarDonacionRecibida(Donacion donacion, String productoID) {
        val producto = this.buscarProducto(productoID);
        donacion.setProducto(producto);
        val donacionRegistrada = this.donacionesRepository.guardar(donacion);

        return donacionRegistrada;
    }

    public Donacion registrarQueja(Donacion donacion, String descripcion) {
        donacion.agregarQueja(descripcion);
        this.donacionesRepository.actualizar(donacion);

        return donacion;
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

    public Producto darAltaProducto(Producto producto, String categoriaID, String identificadorID) {

        if (producto.getId() != null && this.productoRepository.buscarPorId(producto.getId()).isPresent())
        {
            throw new ProductoYaRegistradoException("El producto con ID " + producto.getId() + " ya se encuentra registrado");
        }

        val identificador = this.buscarIdentificador(identificadorID);
        String subcategoria = null;
        if(categoriaID != null) {
            subcategoria = this.buscarSubcategoria(categoriaID);
        }

        identificador.validar(producto);
        producto.setIdentificador(identificador);
        producto.setSubcategoriaID(subcategoria);

        val productoRegistrado = this.productoRepository.guardar(producto);

        return productoRegistrado;
    }

    public Identificador buscarIdentificador(String identificadorID) {
        val identificador = this.identificadoresRepository.buscarPorId(identificadorID);

        if (identificador.isEmpty()) {
            throw new IdentificadorNoEncontradoException("No se encontró identificador con ID " + identificadorID);
        }

        return identificador.get();
    }

    public String buscarSubcategoria(String categoriaID) {
        val categoria = this.categoriaRepository.buscarCategoriaPorId(categoriaID);

        if (categoria.isEmpty()) {
            throw new CategoriaNoEncontradaException("No se encontró subcategoria con ID " + categoriaID);
        }

        return categoria.get().getSubcategoriaID();
    }

    public Identificador darAltaIdentificador(Identificador identificador) {
        if (identificador.getId() != null && this.identificadoresRepository.buscarPorId(identificador.getId()).isPresent())
        {
            throw new RuntimeException("El identificador con ID " + identificador.getId() + " se encuentra registrado");
        }

        val identificadorGuardado = this.identificadoresRepository.guardar(identificador);

        return identificadorGuardado;
    }

    public List<Donacion> buscarTodasDonaciones() {
        return this.donacionesRepository.buscarTodas();
    }

    public void eliminarDonacion(String id) {
        this.donacionesRepository.eliminarPorId(id);
    }

    public List<Producto> buscarTodosLosProductos() {
        return this.productoRepository.buscarTodos();
    }

    public void eliminarProducto(String id) {
        this.productoRepository.eliminarPorId(id);
    }

    public Producto actualizarProducto(String id, ProductoDTO actualizacion) {
        val producto = this.buscarProducto(id);
        val identificador = this.buscarIdentificador(actualizacion.identificadorID());
        val subcategoria = this.buscarSubcategoria(actualizacion.categoriaID());

        producto.setNombre(actualizacion.nombre());
        producto.setDescripcion(actualizacion.descripcion());
        producto.setSubcategoriaID(subcategoria);
        producto.setIdentificador(identificador);

        val productoActualizado = this.productoRepository.guardar(producto);

        return productoActualizado;
    }

    public List<Identificador> buscarTodosLosIdentificadores() {
        return this.identificadoresRepository.buscarTodos();
    }

    public void eliminarIdentificador(String id) {
        this.identificadoresRepository.eliminarPorID(id);
    }

    public Categoria darAltaCategoria(Categoria categoria) {
        if(categoria.getId() != null && this.categoriaRepository.buscarCategoriaPorId(categoria.getId()).isPresent())
        {
            throw new RuntimeException("La categoria con ID " + categoria.getId() + " se encuentra registrada");
        }
        this.categoriaRepository.guardar(categoria);

        return null;
    }

    public List<Categoria> buscarTodasCategorias() {
        return this.categoriaRepository.buscarTodos();
    }

    public void eliminarCategoria(String id) {
        this.categoriaRepository.eliminarPorId(id);
    }
}

package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.exceptions.*;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.CategoriaRepository;
import ar.edu.utn.dds.k3003.repositories.SubcategoriaRepository;
import ar.edu.utn.dds.k3003.repositories.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.ProductoRepository;
import ar.edu.utn.dds.k3003.repositories.IdentificadoresRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonacionesService {
    private final DonacionesRepository donacionesRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final IdentificadoresRepository identificadoresRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final FabricaValidadoresIdentificador fabricaValidadores;

    @Autowired
    public DonacionesService(DonacionesRepository donacionesRepository, ProductoRepository productoRepository, CategoriaRepository categoriaRepository, IdentificadoresRepository identificadoresRepository, SubcategoriaRepository subcategoriaRepository, FabricaValidadoresIdentificador fabricaValidadores) {
        this.donacionesRepository = donacionesRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.identificadoresRepository = identificadoresRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.fabricaValidadores = fabricaValidadores;
    }

    public Donacion gestionarDonacionRecibida(DonacionDTO donacionDTO) {
        Long productoID = Long.parseLong(donacionDTO.productoID());
        val producto = this.buscarProducto(productoID);
        Donacion donacion =  new Donacion(
                donacionDTO.donadorID(),
                donacionDTO.depositoID(),
                donacionDTO.descripcion(),
                producto,
                donacionDTO.cantidad()
        );

        return this.donacionesRepository.save(donacion);
    }

    @Transactional
    public Donacion registrarQueja(Donacion donacion, String descripcion) {
        donacion.agregarQueja(descripcion);

        return this.donacionesRepository.save(donacion);
    }

    public Producto buscarProducto(Long productoID) {
        val producto = this.productoRepository.findById(productoID);

        if (producto.isEmpty())
        {
            throw new ProductoNoEncontradoException("No se encontró el producto con ID " + productoID);
        }

        return producto.get();
    }

    public Donacion buscarDonacionPorId(Long donacionID) {
        val donacion = this.donacionesRepository.findById(donacionID);

        if (donacion.isEmpty())
        {
            throw new DonacionNoEncontradaException("No se encontró donación con ID " + donacionID);
        }

        return donacion.get();
    }

    public List<Donacion> buscarDonacionPorDonadorYFechaInicio(String donadorID, LocalDate fecha) {

        return this.donacionesRepository.findByDonadorIDAndFechaGreaterThanEqual(donadorID, fecha);
    }

    @Transactional
    public Donacion cambiarEstadoDonacion(Long donacionID, EstadoDonacionEnum estado) {
        val donacion = this.buscarDonacionPorId(donacionID);
        if (donacion.getEstado() == estado) {
            return donacion;
        }
        donacion.cambiarEstado(estado);

        return donacion;
    }

    public Producto darAltaProducto(ProductoDTO productoDTO) {

        Long categoriaID = Long.parseLong(productoDTO.categoriaID());
        Long identificadorID = Long.parseLong(productoDTO.identificadorID());

        val identificador = this.buscarIdentificador(identificadorID);

        if(this.productoRepository.existsByIdentificador_Id(identificador.getId())) {
            throw new IdentificadorAsignadoException("El identificador " + identificadorID + " ya se encuentra asignado a otro producto.");
        }

        Subcategoria subcategoria = this.buscarSubcategoria(categoriaID);

        ValidadorIdentificador validador =
                this.fabricaValidadores.obtenerValidador(identificador.getTipo());

        if (!validador.esValido(productoDTO.nombre(), productoDTO.descripcion())) {
            throw new ProductoInvalidoException("El producto no cumple las reglas de validación para su tipo de identificador.");
        }

        Producto producto = new Producto(
                productoDTO.nombre(),
                productoDTO.descripcion(),
                subcategoria,
                identificador
        );

        return this.productoRepository.save(producto);
    }

    public Identificador buscarIdentificador(Long identificadorID) {
        val identificador = this.identificadoresRepository.findById(identificadorID);

        if (identificador.isEmpty()) {
            throw new IdentificadorNoEncontradoException("No se encontró identificador con ID " + identificadorID);
        }

        return identificador.get();
    }

    public Categoria buscarCategoria(Long categoriaID) {
        val categoria = this.categoriaRepository.findById(categoriaID);

        if (categoria.isEmpty()) {
            throw new CategoriaNoEncontradaException("No se encontró categoria con ID " + categoriaID);
        }

        return categoria.get();
    }

    public Subcategoria buscarSubcategoria(Long subcategoriaID) {
        val subcategoria = this.subcategoriaRepository.findById(subcategoriaID);

        if (subcategoria.isEmpty()) {
            throw new CategoriaNoEncontradaException("No se encontró subcategoria con ID " + subcategoriaID);
        }

        return subcategoria.get();
    }

    public Identificador darAltaIdentificador(IdentificadorDTO identificadorDTO) {

        Identificador identificador = new Identificador(
          identificadorDTO.descripcion(),
          identificadorDTO.tipo()
        );

        return this.identificadoresRepository.save(identificador);
    }

    public List<Donacion> buscarTodasDonaciones() {
        return this.donacionesRepository.findAll();
    }

    public void eliminarDonacion(Long id) {
        this.donacionesRepository.deleteById(id);
    }

    public List<Producto> buscarTodosLosProductos() {
        return this.productoRepository.findAll();
    }

    public void eliminarProducto(Long id) {
        this.productoRepository.deleteById(id);
    }

    public Producto actualizarProducto(Long id, ProductoDTO actualizacion) {
        Long identificadorID = Long.parseLong(actualizacion.identificadorID());
        Long categoriaID = Long.parseLong(actualizacion.categoriaID());

        val producto = this.buscarProducto(id);
        val identificador = this.buscarIdentificador(identificadorID);
        val subcategoria = this.buscarSubcategoria(categoriaID);

        producto.setNombre(actualizacion.nombre());
        producto.setDescripcion(actualizacion.descripcion());
        producto.setSubcategoria(subcategoria);
        producto.setIdentificador(identificador);

        return this.productoRepository.save(producto);
    }

    public List<Identificador> buscarTodosLosIdentificadores() {
        return this.identificadoresRepository.findAll();
    }

    public void eliminarIdentificador(Long id) {
        this.identificadoresRepository.deleteById(id);
    }

    public Categoria darAltaCategoria(CategoriaDTO categoriaDTO) {

        Categoria categoria = new Categoria(
                categoriaDTO.nombre(),
                categoriaDTO.descripcion()
        );

        return this.categoriaRepository.save(categoria);
    }

    public List<Categoria> buscarTodasCategorias() {
        return this.categoriaRepository.findAll();
    }

    public void eliminarCategoria(Long id) {
        this.categoriaRepository.deleteById(id);
    }

    public Subcategoria altaSubcategoria(SubcategoriaDTO subcategoriaDTO) {

        Long categoriaID = Long.parseLong(subcategoriaDTO.categoriaID());
        val categoria = this.buscarCategoria(categoriaID);

        Subcategoria subcategoria = new Subcategoria(
                subcategoriaDTO.nombre(),
                categoria
        ) ;

        return this.subcategoriaRepository.save(subcategoria);
    }

    public List<Subcategoria> obtenerSubcategorias(Long categoriaID) {
        return this.subcategoriaRepository.findByCategoria_Id(categoriaID);
    }

    public void eliminarSubcategoria(Long subcategoriaID) {
        this.subcategoriaRepository.deleteById(subcategoriaID);
    }

    public void retirarQueja(Donacion donacion, String descripcion) {
        donacion.retirarQueja(descripcion);

        this.donacionesRepository.save(donacion);
    }

    public void resetear() {
        subcategoriaRepository.deleteAll();
        categoriaRepository.deleteAll();
        identificadoresRepository.deleteAll();
        productoRepository.deleteAll();
        donacionesRepository.deleteAll();
    }

    public List<Donacion> buscarDonacionPorDonador(String donadorID) {

        return this.donacionesRepository.findByDonadorID(donadorID);
    }

    public boolean verificarExistenciaProducto(Long productoID) {
        return this.productoRepository.existsById(productoID);
    }
}
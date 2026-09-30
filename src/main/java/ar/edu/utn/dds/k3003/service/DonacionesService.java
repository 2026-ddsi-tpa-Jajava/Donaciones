package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.*;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.CategoriaRepository;
import ar.edu.utn.dds.k3003.repositories.SubcategoriaRepository;
import ar.edu.utn.dds.k3003.repositories.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.ProductoRepository;
import ar.edu.utn.dds.k3003.repositories.IdentificadoresRepository;
import lombok.Setter;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Setter
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    @Setter
    private FachadaLogistica fachadaLogistica;
    private static final Logger log = LoggerFactory.getLogger(DonacionesService.class);

    @Autowired
    public DonacionesService(DonacionesRepository donacionesRepository, ProductoRepository productoRepository, CategoriaRepository categoriaRepository, IdentificadoresRepository identificadoresRepository, SubcategoriaRepository subcategoriaRepository, FabricaValidadoresIdentificador fabricaValidadores) {
        this.donacionesRepository = donacionesRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.identificadoresRepository = identificadoresRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.fabricaValidadores = fabricaValidadores;
    }

    @Transactional
    public Donacion registrarDonacion(DonacionDTO donacion) {

        log.info("Iniciando registro de donación para el donadorID: {}", donacion.donadorID());

        this.verificarDonador(donacion.donadorID());

        Donacion donacionRegistrada = this.gestionarDonacionRecibida(donacion);

        log.info("Notificando al servicio de Logística sobre la donaciónID: {}", donacionRegistrada.getId());

        fachadaLogistica.gestionarDonacion(
                donacionRegistrada.getDepositoID(),
                donacionRegistrada.getId().toString(),
                donacionRegistrada.getProducto().getId().toString(),
                donacionRegistrada.getCantidad()
        );

        log.info("Donación {} registrada exitosamente.", donacionRegistrada.getId());

        return donacionRegistrada;
    }

    private void verificarDonador(String donadorID) {
        if (!fachadaDonadoresYEntidades.puedeDonar(donadorID)) {
            throw new DonadorNoAptoException("El donador no se encuentra apto para donar");
        }
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
    public Donacion registrarQuejaEnDonacion(String donacionID, String descripcion) {
        Long longID = Long.parseLong(donacionID);
        log.info("Iniciando registro de queja para la donacionID: {}", donacionID);

        Donacion donacion = this.buscarDonacionPorId(longID);
        QuejaDTO queja =  new QuejaDTO(null, donacionID, donacion.getDonadorID(), LocalDate.now(), descripcion);
        Donacion donacionActualizada = this.registrarQueja(donacion, descripcion);

        try {
            this.fachadaDonadoresYEntidades.agregarQueja(queja);
            log.info("Queja comunicada exitosamente al módulo de Donadores para la donacionID: {}", donacionID);
        } catch (Exception e) {
            log.error("Fallo al comunicar la queja al servicio externo para la donacionID: {}. Detalle: {}", donacionID, e.getMessage());
            throw e;
        }

        return donacionActualizada;
    }

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
        log.debug("Consultando donaciones para el donadorID: {} desde la fecha: {}", donadorID, fecha);
        this.fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        return this.donacionesRepository.findByDonadorIDAndFechaGreaterThanEqual(donadorID, fecha);
    }

    @Transactional
    public Donacion cambiarEstadoDonacion(String donacionID, EstadoDonacionEnum estado) {
        Long longID = Long.parseLong(donacionID);
        val donacion = this.buscarDonacionPorId(longID);

        if (donacion.getEstado() == estado) {
            return donacion;
        }

        try {
            donacion.cambiarEstado(estado);
            log.info("Transición exitosa: Donación {} cambió a estado {}", donacionID, estado);
        } catch (CambioEstadoInvalidoException e) {
            log.warn("Transición inválida rechazada: Intento de pasar la donación {} del estado {} al estado {}",
                    donacionID, donacion.getEstado(), estado);
            throw e;
        }

        return donacion;
    }

    public Producto darAltaProducto(ProductoDTO productoDTO) {

        Long categoriaID = Long.parseLong(productoDTO.subcategoriaID());
        Long identificadorID = Long.parseLong(productoDTO.identificadorID());

        val identificador = this.buscarIdentificador(identificadorID);
        if(this.productoRepository.existsByIdentificador_Id(identificador.getId())) {
            throw new IdentificadorAsignadoException("El identificador " + identificadorID + " ya se encuentra asignado a otro producto.");
        }

        Subcategoria subcategoria = this.buscarSubcategoria(categoriaID);
        ValidadorIdentificador validador = this.fabricaValidadores.obtenerValidador(identificador.getTipo());

        if (!validador.esValido(productoDTO.nombre(), productoDTO.descripcion())) {
            log.warn("Rechazo de negocio: El identificador {} (Tipo: {}) no es válido para el producto '{}'",
                    identificador.getId(), identificador.getTipo(), productoDTO.nombre());
            throw new ProductoInvalidoException("El producto no cumple las reglas de validación para su tipo de identificador.");
        }

        Producto producto = new Producto(productoDTO.nombre(), productoDTO.descripcion(), subcategoria, identificador);
        Producto guardado = this.productoRepository.save(producto);
        log.info("Producto dado de alta exitosamente: ID {} - {}", guardado.getId(), guardado.getNombre());

        return guardado;
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
        Long categoriaID = Long.parseLong(actualizacion.subcategoriaID());

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

    public void resetear() {
        donacionesRepository.deleteAll();
        productoRepository.deleteAll();
        subcategoriaRepository.deleteAll();
        categoriaRepository.deleteAll();
        identificadoresRepository.deleteAll();
    }

    public List<Donacion> buscarDonacionPorDonador(String donadorID) {
        log.debug("Consultando todas las donaciones para el donadorID: {}", donadorID);
        this.fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        return this.donacionesRepository.findByDonadorID(donadorID);
    }

    public boolean verificarExistenciaProducto(Long productoID) {
        return this.productoRepository.existsById(productoID);
    }
}
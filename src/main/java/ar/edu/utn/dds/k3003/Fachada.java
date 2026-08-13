package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.exceptions.*;
import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.Subcategoria;
import ar.edu.utn.dds.k3003.mappers.CategoriaDataMapper;
import ar.edu.utn.dds.k3003.mappers.SubcategoriaDataMapper;
import ar.edu.utn.dds.k3003.mappers.ProductoDataMapper;
import ar.edu.utn.dds.k3003.mappers.IdentificadoresDataMapper;
import ar.edu.utn.dds.k3003.service.DonacionesService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.constraints.Pattern;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.mappers.DonacionesDataMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Fachada implements FachadaDonaciones{

    private final DonacionesService donacionesService;
    private final MeterRegistry meterRegistry;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    private FachadaLogistica fachadaLogistica;
    private final DonacionesDataMapper donacionesDataMapper = new DonacionesDataMapper();
    private final ProductoDataMapper productoDataMapper = new ProductoDataMapper();
    private final IdentificadoresDataMapper identificadoresDataMapper = new IdentificadoresDataMapper();
    private final CategoriaDataMapper categoriaDataMapper =  new CategoriaDataMapper();
    private final SubcategoriaDataMapper  subcategoriaDataMapper = new SubcategoriaDataMapper();
    private static final Logger logger = LoggerFactory.getLogger(Fachada.class);

    @Autowired
    public Fachada(DonacionesService donacionesService, MeterRegistry meterRegistry) {
        this.donacionesService = donacionesService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {

        this.verificarDonador(donacionDTO.donadorID());
        val donacionRegistrada = donacionesService.gestionarDonacionRecibida(donacionDTO);

        boolean reversionExitosa = false;

        try {
            fachadaLogistica.gestionarDonacion(
                    donacionRegistrada.getDepositoID(),
                    donacionRegistrada.getId().toString(),
                    donacionRegistrada.getProducto().getId().toString(),
                    donacionRegistrada.getCantidad()
            );

        } catch (PeticionExternaInvalidaException e) {
            this.eliminarDonacion(donacionRegistrada.getId());
            throw new PeticionExternaInvalidaException(
                    "Fallo en Logística: " + e.getMessage(),
                    e.getStatusCode()
            );

        } catch (FalloServicioExternoException e) {
            try {
                this.eliminarDonacion(donacionRegistrada.getId());
                reversionExitosa = true;
            } catch (Exception rollbackEx) {
                logger.error("ALERTA: No se pudo eliminar la donacion {}. El sistema externo y el local estan desincronizados.", donacionRegistrada.getId(), rollbackEx);
            }

            String mensajeError = reversionExitosa
                    ? "Error de comunicación con Logística. La donación fue revertida."
                    : "Error de comunicación con Logística. FALLO CRÍTICO: La donación NO pudo ser revertida.";

            throw new FalloServicioExternoException(mensajeError, e);
        }

        this.meterRegistry.counter("donaciones.donacion.operaciones","operacion", "alta").increment();
        return this.donacionesDataMapper.toDonacionDTO(donacionRegistrada);
    }

    private void verificarDonador(String donadorID) {
//        this.fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        if (!fachadaDonadoresYEntidades.puedeDonar(donadorID)) {
            throw new DonadorNoAptoException("El donador no se encuentra apto para donar");
        }
    }

    @Override
    public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
        Long longID = Long.parseLong(donacionID);
        val donacion = this.donacionesService.buscarDonacionPorId(longID);
        return this.donacionesDataMapper.toDonacionDTO(donacion);
    }

    @Override
    public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado) throws NoSuchElementException {
        Long longID = Long.parseLong(donacionID);
        val donacion = this.donacionesService.cambiarEstadoDonacion(longID, estado);
        this.meterRegistry.counter("donaciones.estado.cambios","nuevo_estado", estado.name()).increment();

        return this.donacionesDataMapper.toDonacionDTO(donacion);
    }

    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) throws NoSuchElementException {
        this.fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        val donaciones = this.donacionesService.buscarDonacionPorDonadorYFechaInicio(donadorID, fecha);
        return donaciones.stream().map(this.donacionesDataMapper::toDonacionDTO).toList();
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
        Long longID = Long.parseLong(donacionID);
        val donacion = this.donacionesService.buscarDonacionPorId(longID);
        QuejaDTO quejaDTO = new QuejaDTO(null, donacionID, donacion.getDonadorID(), LocalDate.now(), descripcion);
        val donacionActualizada = this.donacionesService.registrarQueja(donacion, descripcion);

        boolean reversionExitosa = false;

        try {
            this.fachadaDonadoresYEntidades.agregarQueja(quejaDTO);
        } catch (PeticionExternaInvalidaException e) {
            this.retirarQueja(donacion, descripcion);
            throw e;

        }catch (FalloServicioExternoException e) {
            try {
                this.retirarQueja(donacion, descripcion);
                reversionExitosa = true;
            } catch (Exception rollbackEx) {
                logger.error("ALERTA DE CONSISTENCIA: No se pudo retirar la queja de la donacion {}. El sistema externo y el local estan desincronizados.", donacionID, rollbackEx);
            }

            String mensajeError = reversionExitosa
                    ? "Error al registrar queja en donacion. La acción fue revertida."
                    : "Error al registrar queja en donacion. FALLO CRÍTICO: La acción local NO pudo ser revertida.";

            throw new FalloServicioExternoException(mensajeError, e);
        }

        this.meterRegistry.counter("donaciones.queja.operaciones", "operacion", "alta").increment();
        this.meterRegistry.counter("donaciones.estado.cambios","nuevo_estado", donacionActualizada.getEstado().name()).increment();

        return this.donacionesDataMapper.toDonacionDTO(donacionActualizada);
    }

    private void retirarQueja(Donacion donacion, String descripcion) {
        this.donacionesService.retirarQueja(donacion, descripcion);
        this.meterRegistry.counter("donaciones.queja.operaciones", "operacion", "baja").increment();
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {

        val productoRegistrado = this.donacionesService.darAltaProducto(productoDTO);

        this.meterRegistry.counter("donaciones.producto.operaciones","operacion", "alta").increment();

        return this.productoDataMapper.toProductoDTO(productoRegistrado);
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
        Long longID = Long.parseLong(productoID);
        val producto = this.donacionesService.buscarProducto(longID);
        return this.productoDataMapper.toProductoDTO(producto);
    }

    @Override
    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {

        val identificadorRegistrado = this.donacionesService.darAltaIdentificador(identificadorDTO);

        this.meterRegistry.counter("donaciones.identificador.operaciones","operacion", "alta").increment();

        return this.identificadoresDataMapper.toIdentificadorDTO(identificadorRegistrado);
    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) throws NoSuchElementException {
        Long longID = Long.parseLong(identificadorID);
        val identificador = this.donacionesService.buscarIdentificador(longID);
        return this.identificadoresDataMapper.toIdentificadorDTO(identificador);
    }

    @Autowired
    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
        this.fachadaDonadoresYEntidades = fachadaDonadoresYEntidades;
    }

    @Autowired
    @Override
    public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
        this.fachadaLogistica = fachadaLogistica;
    }

    public List<DonacionDTO> obtenerTodasLasDonaciones() {
        val donaciones = this.donacionesService.buscarTodasDonaciones();
        return donaciones.stream().map(this.donacionesDataMapper::toDonacionDTO).toList();
    }

    public void eliminarDonacion(Long id) {
        this.donacionesService.eliminarDonacion(id);
        this.meterRegistry.counter("donaciones.donacion.operaciones","operacion", "baja").increment();
    }

    public List<ProductoDTO> obtenerTodosLosProductos() {
        val productos = this.donacionesService.buscarTodosLosProductos();
        return productos.stream().map(this.productoDataMapper::toProductoDTO).toList();
    }

    public void eliminarProducto(String id) {
        Long productoID = Long.parseLong(id);
        this.donacionesService.eliminarProducto(productoID);
        this.meterRegistry.counter("donaciones.producto.operaciones","operacion", "baja").increment();
    }

    public ProductoDTO actualizarProducto(String id, ProductoDTO productoDTO) {
        Long productoID =  Long.parseLong(id);
        val productoActualizado = this.donacionesService.actualizarProducto(productoID, productoDTO);

        this.meterRegistry.counter("donaciones.producto.operaciones","operacion", "actualizado").increment();

        return this.productoDataMapper.toProductoDTO(productoActualizado);
    }

    public List<IdentificadorDTO> obtenerTodasLosIdentificadores() {
        val identificadores = this.donacionesService.buscarTodosLosIdentificadores();
        return identificadores.stream().map(this.identificadoresDataMapper::toIdentificadorDTO).toList();
    }

    public void eliminarIdentificador(String id) {
        Long identificadorID = Long.parseLong(id);
        this.donacionesService.eliminarIdentificador(identificadorID);
        this.meterRegistry.counter("donaciones.identificador.operaciones","operacion", "baja").increment();
    }

    public CategoriaDTO agregarCategoria(CategoriaDTO categoriaDTO) {

        val categoriaGuardada = this.donacionesService.darAltaCategoria(categoriaDTO);

        this.meterRegistry.counter("donaciones.categoria.operaciones","operacion", "alta").increment();

        return this.categoriaDataMapper.toCategoriaDTO(categoriaGuardada);
    }

    public List<CategoriaDTO> obtenerTodasLasCategorias() {
        val categorias = this.donacionesService.buscarTodasCategorias();
        return categorias.stream().map(this.categoriaDataMapper::toCategoriaDTO).toList();
    }

    public void eliminarCategoria(String id) {
        Long categoriaID = Long.parseLong(id);
        this.donacionesService.eliminarCategoria(categoriaID);
        this.meterRegistry.counter("donaciones.categoria.operaciones","operacion", "baja").increment();
    }

    public SubcategoriaDTO agregarSubCategoria (SubcategoriaDTO subcategoriaDTO) {

        val subcategoriaGuardada = this.donacionesService.altaSubcategoria(subcategoriaDTO);

        this.meterRegistry.counter("donaciones.subcategoria.operaciones","operacion", "alta").increment();

        return this.subcategoriaDataMapper.toSubategoriaDTO(subcategoriaGuardada);
    }

    public List<SubcategoriaDTO> obtenerSubcategorias(String categoriaID) {
        Long longID = Long.parseLong(categoriaID);
        List<Subcategoria> subcategorias = this.donacionesService.obtenerSubcategorias(longID);

        return subcategorias.stream().map(this.subcategoriaDataMapper::toSubategoriaDTO).toList();
    }

    public void eliminarSubcategoria(String id) {
        Long subcategoriaID = Long.parseLong(id);
        this.donacionesService.eliminarSubcategoria(subcategoriaID);
        this.meterRegistry.counter("donaciones.subcategoria.operaciones","operacion", "baja").increment();
    }

    public void resetear() {
        this.donacionesService.resetear();
    }

    public List<DonacionDTO> buscarPorDonador(String donadorID) {
        this.fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        List<Donacion> donaciones = this.donacionesService.buscarDonacionPorDonador(donadorID);
        return donaciones.stream().map(this.donacionesDataMapper::toDonacionDTO).toList();
    }

    public boolean verificarExistenciaProducto(String productoID) {
        Long longID = Long.parseLong(productoID);
        return this.donacionesService.verificarExistenciaProducto(longID);
    }
}
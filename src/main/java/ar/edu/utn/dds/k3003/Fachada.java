package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.model.Subcategoria;
import ar.edu.utn.dds.k3003.mappers.CategoriaDataMapper;
import ar.edu.utn.dds.k3003.mappers.SubcategoriaDataMapper;
import ar.edu.utn.dds.k3003.mappers.ProductoDataMapper;
import ar.edu.utn.dds.k3003.mappers.IdentificadoresDataMapper;
import ar.edu.utn.dds.k3003.service.DonacionesService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.donaciones.*;
import ar.edu.utn.dds.k3003.mappers.DonacionesDataMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Fachada implements FachadaDonaciones{

    private final DonacionesService donacionesService;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    private FachadaLogistica fachadaLogistica;
    private final DonacionesDataMapper donacionesDataMapper = new DonacionesDataMapper();
    private final ProductoDataMapper productoDataMapper = new ProductoDataMapper();
    private final IdentificadoresDataMapper identificadoresDataMapper = new IdentificadoresDataMapper();
    private final CategoriaDataMapper categoriaDataMapper =  new CategoriaDataMapper();
    private final SubcategoriaDataMapper  subcategoriaDataMapper = new SubcategoriaDataMapper();

    @Autowired
    public Fachada(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    @Override
    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
        this.verificarDonacionIngresada(donacionDTO);
        this.verificarDonador(donacionDTO.donadorID());

        val donacionRegistrada = donacionesService.gestionarDonacionRecibida(donacionDTO);

        try {
            fachadaLogistica.gestionarDonacion(
                    donacionRegistrada.getDepositoID(),
                    donacionRegistrada.getId().toString(),
                    donacionRegistrada.getProducto().getId().toString(),
                    donacionRegistrada.getCantidad()
            );

            return this.donacionesDataMapper.toDonacionDTO(donacionRegistrada);

        } catch (Exception e) {
            donacionesService.eliminarDonacion(donacionRegistrada.getId());

            throw new RuntimeException("Error de comunicación con Logística. La donación fue revertida para mantener la consistencia.", e);
        }
    }

    private void verificarDonacionIngresada(DonacionDTO donacionDTO) {
        if (donacionDTO == null || donacionDTO.id() != null) {
          throw new DonacionInvalidaException("Donación inválida");
        }
      }

    private void verificarDonador(String donadorID) {

        this.fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);

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
        return this.donacionesDataMapper.toDonacionDTO(donacion);
    }

    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) throws NoSuchElementException {
        val donaciones = this.donacionesService.buscarDonacionPorDonadorYFechaInicio(donadorID, fecha);
        return donaciones.stream().map(this.donacionesDataMapper::toDonacionDTO).toList();
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
        Long longID = Long.parseLong(donacionID);
        val donacion = this.donacionesService.buscarDonacionPorId(longID);
        QuejaDTO quejaDTO = new QuejaDTO(null, donacionID, donacion.getDonadorID(), LocalDate.now(), descripcion);
        val donacionActualizada = this.donacionesService.registrarQueja(donacion, descripcion);
        try {
            this.fachadaDonadoresYEntidades.agregarQueja(quejaDTO);
            return this.donacionesDataMapper.toDonacionDTO(donacionActualizada);
        } catch (Exception e) {
            this.donacionesService.retirarQueja(donacion, descripcion);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {

        this.verificarProductoIngresado(productoDTO);

        val productoRegistrado = this.donacionesService.darAltaProducto(productoDTO);

        return this.productoDataMapper.toProductoDTO(productoRegistrado);
    }

    private void verificarProductoIngresado(ProductoDTO productoDTO) {
        if (productoDTO == null || productoDTO.id() != null) {
            throw new DonacionInvalidaException("Producto inválido");
        }
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
        Long longID = Long.parseLong(productoID);
        val producto = this.donacionesService.buscarProducto(longID);
        return this.productoDataMapper.toProductoDTO(producto);
    }

    @Override
    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
        this.verificarIdentificadorIngresado(identificadorDTO);
        val identificadorRegistrado = this.donacionesService.darAltaIdentificador(identificadorDTO);

        return this.identificadoresDataMapper.toIdentificadorDTO(identificadorRegistrado);
    }

    private void verificarIdentificadorIngresado(IdentificadorDTO identificadorDTO) {
        if (identificadorDTO == null || identificadorDTO.id() != null) {
            throw new DonacionInvalidaException("Identificador inválido");
        }
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

    public void eliminarDonacion(String id) {
        Long donacionID = Long.parseLong(id);
        this.donacionesService.eliminarDonacion(donacionID);
    }

    public List<ProductoDTO> obtenerTodosLosProductos() {
        val productos = this.donacionesService.buscarTodosLosProductos();
        return productos.stream().map(this.productoDataMapper::toProductoDTO).toList();
    }

    public void eliminarProducto(String id) {
        Long productoID = Long.parseLong(id);
        this.donacionesService.eliminarProducto(productoID);
    }

    public ProductoDTO actualizarProducto(String id, ProductoDTO productoDTO) {
        Long productoID =  Long.parseLong(id);
        val productoActualizado = this.donacionesService.actualizarProducto(productoID, productoDTO);

        return this.productoDataMapper.toProductoDTO(productoActualizado);
    }

    public List<IdentificadorDTO> obtenerTodasLosIdentificadores() {
        val identificadores = this.donacionesService.buscarTodosLosIdentificadores();
        return identificadores.stream().map(this.identificadoresDataMapper::toIdentificadorDTO).toList();
    }

    public void eliminarIdentificador(String id) {
        Long identificadorID = Long.parseLong(id);
        this.donacionesService.eliminarIdentificador(identificadorID);
    }

    public CategoriaDTO agregarCategoria(CategoriaDTO categoriaDTO) {
        this.verificarCategoriaIngresada(categoriaDTO);
        val categoriaGuardada = this.donacionesService.darAltaCategoria(categoriaDTO);

        return this.categoriaDataMapper.toCategoriaDTO(categoriaGuardada);
    }

    private void verificarCategoriaIngresada(CategoriaDTO categoriaDTO) {
        if (categoriaDTO == null || categoriaDTO.id() != null) {
            throw new DonacionInvalidaException("Categoria inválida");
        }
    }

    public List<CategoriaDTO> obtenerTodasLasCategorias() {
        val categorias = this.donacionesService.buscarTodasCategorias();
        return categorias.stream().map(this.categoriaDataMapper::toCategoriaDTO).toList();
    }

    public void eliminarCategoria(String id) {
        Long categoriaID = Long.parseLong(id);
        this.donacionesService.eliminarCategoria(categoriaID);
    }

    public SubcategoriaDTO agregarSubCategoria (SubcategoriaDTO subcategoriaDTO) {
        this.verificarSubcategoriaIngresada(subcategoriaDTO);
        val subcategoriaGuardada = this.donacionesService.altaSubcategoria(subcategoriaDTO);

        return this.subcategoriaDataMapper.toSubategoriaDTO(subcategoriaGuardada);
    }

    private void verificarSubcategoriaIngresada(SubcategoriaDTO subcategoriaDTO) {
        if (subcategoriaDTO == null || subcategoriaDTO.id() != null) {
            throw new DonacionInvalidaException("SubCategoria inválida");
        }
    }

    public List<SubcategoriaDTO> obtenerSubcategorias(String categoriaID) {
        Long longID = Long.parseLong(categoriaID);
        List<Subcategoria> subcategorias = this.donacionesService.obtenerSubcategorias(longID);

        return subcategorias.stream().map(this.subcategoriaDataMapper::toSubategoriaDTO).toList();
    }

    public void eliminarSubcategoria(String id) {
        Long subcategoriaID = Long.parseLong(id);
        this.donacionesService.eliminarSubcategoria(subcategoriaID);
    }
}
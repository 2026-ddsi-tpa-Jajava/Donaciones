package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.repositories.donaciones.Producto.ProductoDataMapper;
import ar.edu.utn.dds.k3003.repositories.donaciones.identificador.IdentificadoresDataMapper;
import ar.edu.utn.dds.k3003.service.DonacionesService;
import lombok.val;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.donaciones.*;
import ar.edu.utn.dds.k3003.model.donaciones.*;
import ar.edu.utn.dds.k3003.repositories.donaciones.Donacion.DonacionesDataMapper;

import java.time.LocalDate;

@Service
public class Fachada implements FachadaDonaciones{

    private DonacionesService donacionesService;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    private FachadaLogistica fachadaLogistica;
    private DonacionesDataMapper donacionesDataMapper = new DonacionesDataMapper();
    private ProductoDataMapper productoDataMapper = new ProductoDataMapper();
    private IdentificadoresDataMapper identificadoresDataMapper = new IdentificadoresDataMapper();


    public Fachada() {
        donacionesService = new DonacionesService();
    }

    @Override
    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
        this.verificarDonacionIngresada(donacionDTO);

        this.verificarDonador(donacionDTO.donadorID());
        val donacion = this.donacionesDataMapper.toDonacion(donacionDTO);
        val donacionRegistrada = donacionesService.gestionarDonacionRecibida(donacion, donacionDTO.productoID());
        fachadaLogistica.gestionarDonacion(
                donacionRegistrada.getDepositoID(),
                donacionRegistrada.getId(),
                donacionRegistrada.getProducto().getId(),
                donacionRegistrada.getCantidad()
        );

        return this.donacionesDataMapper.toDonacionDTO(donacionRegistrada);
    }

    private void verificarDonacionIngresada(DonacionDTO donacionDTO) {
        if (donacionDTO == null || donacionDTO.id() != null) {
          throw new DonacionInvalidaException("Donación inválida");
        }
      }

    private void verificarDonador(String donadorID) {
        if (!fachadaDonadoresYEntidades.puedeDonar(donadorID)) {
            throw new DonadorNoAptoException("El donador no se encuentra apto para donar");
        }
    }

    @Override
    public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
        val donacion = this.donacionesService.buscarDonacionPorId(donacionID);
        return this.donacionesDataMapper.toDonacionDTO(donacion);
    }

    @Override
    public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado) throws NoSuchElementException {
        val donacion = this.donacionesService.cambiarEstadoDonacion(donacionID, estado);
        return this.donacionesDataMapper.toDonacionDTO(donacion);
    }

    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) throws NoSuchElementException {
        val donaciones = this.donacionesService.buscarDonacionPorDonadorYFechaInicio(donadorID, fecha);
        return donaciones.stream().map(donacion -> this.donacionesDataMapper.toDonacionDTO(donacion)).toList();
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
        val donacion = this.donacionesService.buscarDonacionPorId(donacionID);
        QuejaDTO quejaDTO = new QuejaDTO(null, donacionID, donacion.getDonadorID(), LocalDate.now(), descripcion);
        this.fachadaDonadoresYEntidades.agregarQueja(quejaDTO);
        val donacionActualizada = this.donacionesService.registrarQueja(donacion, descripcion);

        return this.donacionesDataMapper.toDonacionDTO(donacionActualizada);
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
        this.verificarProductoIngresado(productoDTO);

        val producto = this.productoDataMapper.toProducto(productoDTO);
        val productoRegistrado = this.donacionesService.darAltaProducto(producto, productoDTO.categoriaID(), productoDTO.identificadorID());

        return this.productoDataMapper.toProductoDTO(productoRegistrado);
    }

    private void verificarProductoIngresado(ProductoDTO productoDTO) {
        if (productoDTO == null || productoDTO.id() != null) {
            throw new DonacionInvalidaException("Producto inválido");
        }
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
        val producto = this.donacionesService.buscarProducto(productoID);
        return this.productoDataMapper.toProductoDTO(producto);
    }

    @Override
    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
        // TODO
        this.verificarIdentificadorIngresado(identificadorDTO);
        val identificador = this.identificadoresDataMapper.toIdentificador(identificadorDTO);
        val identificadorRegistrado = this.donacionesService.darAltaIdentificador(identificador);

        return this.identificadoresDataMapper.toIdentificadorDTO(identificadorRegistrado);
    }

    private void verificarIdentificadorIngresado(IdentificadorDTO identificadorDTO) {
        if (identificadorDTO == null || identificadorDTO.id() != null) {
            throw new DonacionInvalidaException("Identificador inválido");
        }
    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) throws NoSuchElementException {
        // TODO
        return null;
    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
        this.fachadaDonadoresYEntidades = fachadaDonadoresYEntidades;
    }

    @Override
    public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
        this.fachadaLogistica = fachadaLogistica;
    }
}

//@Service
//public class Fachada implements FachadaDonaciones {
//
//  private DonacionesRepository donacionesRepository;
//  private ProductoRepository productoRepository;
//  private CategoriaRepository categoriaRepository;
//  private DonacionesDataMapper donacionesDataMapper = new DonacionesDataMapper();
//  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
//  private FachadaLogistica fachadaLogistica;
//
//  private DonacionesService donacionesService;
//
//  public Fachada() {
//    this.donacionesRepository = new InMemoryDonacionesRepo();
//    this.productoRepository = new InMemoryProductoRepo();
//    this.categoriaRepository = new InMemoryCategoriaRepo();
//
//    this.cargarEnCatalogo("producto1");
//  }
//
//  private void cargarEnCatalogo(String productoID) {
//    Categoria alimentos = new Categoria("Alimentos", "");
//    Subcategoria arroz = new Subcategoria("1","Arroz", "", alimentos);
//
//
//    Producto producto = new Producto(
//            productoID,
//            "",
//            "",
//            arroz
//    );
//    this.productoRepository.guardar(producto);
//  }
//
//  public ProductoDTO darAltaProducto(String productoID, String nombre, String descripcion,String categoriaID) {
//
//    this.validarID(productoID);
//    this.validarNombre(nombre);
//
//    Subcategoria subcategoria = this.categoriaRepository.buscarSubcategoriaPorId(categoriaID)
//            .orElseThrow(() -> new SubcategoriaInvalidaException("La subcategoría no existe."));
//
//    Producto nuevoProducto = new Producto(
//            productoID,
//            nombre,
//            descripcion,
//            subcategoria);
//
//    Producto guardado = this.productoRepository.guardar(nuevoProducto);
//
//    return new ProductoDTO(
//            guardado.getId(),
//            guardado.getNombre(),
//            guardado.getDescripcion(),
//            guardado.getSubcategoria().getId(),
//            null);
//  }
//
//  private void validarNombre(String nombre) {
//    if (nombre == null || nombre.trim().isEmpty()) {
//      throw new NombreInvalidoException("Nombre inválido");
//    }
//  }
//
//  private void validarID(String id) {
//    if (id == null || id.trim().isEmpty()) {
//      throw new IDInvalidoException("ID inválido.");
//    }
//  }
//
//  public CategoriaDTO darAltaCategoria(String nombre, String descripcion) {
//    this.validarNombre(nombre);
//
//    Categoria nuevaCategoria = new Categoria(
//            nombre,
//            descripcion);
//
//    Categoria guardada = this.categoriaRepository.guardar(nuevaCategoria);
//
//    return new CategoriaDTO(
//            guardada.getId(),
//            guardada.getNombre(),
//            guardada.getDescripcion(),
//            null);
//  }
//
//  public void asignarIdentificadorAProducto(String productoID, String codigo) {
//
//    this.validarID(productoID);
//    if (codigo == null || codigo.trim().isEmpty()) {
//      throw new IllegalArgumentException("código inválido");
//    }
//
//    Producto producto = this.buscarProductoPorID(productoID);
//
//    Identificador nuevoIdentificador = new Identificador(TipoIdentificadorEnum.CODIGODEBARRAS, codigo);
//
//    producto.setIdentificador(nuevoIdentificador);
//
//    this.productoRepository.actualizar(producto);
//  }
//
////  public Producto buscarProductoPorID(String productoID) {
////    return this.productoRepository.buscarPorId(productoID)
////            .orElseThrow(() -> new ProductoNoEncontradoException(
////                    "El producto con ID " + productoID + " no existe."));
////  }
//
//  @Override
//  public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
//      // TODO
//      return null;
//  }
//
//  @Override
//  public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) throws NoSuchElementException {
//      // TODO
//    return null;
//  }
//
//  public String darAltaSubcategoria(String categoriaID, String nombre, String descripcion) {
//
//    this.validarID(categoriaID);
//    this.validarNombre(nombre);
//
//    Categoria categoria = this.categoriaRepository.buscarCategoriaPorId(categoriaID)
//            .orElseThrow(() -> new CategoriaNoEncontradaException(
//                    "La categoría con ID " + categoriaID + " no existe."));
//
//    String id = this.categoriaRepository.generarIdSubcategoria();
//    Subcategoria nuevaSubcategoria = new Subcategoria(
//            id,
//            nombre,
//            descripcion,
//            categoria);
//
//    categoria.agregarSubcategoria(nuevaSubcategoria);
//    this.categoriaRepository.actualizar(categoria);
//
//
//    return nuevaSubcategoria.getId();
//  }
//
//  public void darDeBajaProducto(String productoID) {
//    Producto producto = this.buscarProductoPorID(productoID);
//    producto.darDeBaja();
//    this.productoRepository.actualizar(producto);
//  }
//
//  public void darDeBajaCategoria(String categoriaID) {
//
//    Categoria categoria = this.categoriaRepository.buscarCategoriaPorId(categoriaID)
//            .orElseThrow(() -> new CategoriaNoEncontradaException(
//                    "La categoría con ID " + categoriaID + " no existe."));
//
//    boolean tieneSubcategoriasActivas = categoria.getSubcategorias().stream()
//            .anyMatch(subcategoria -> subcategoria.isEstaDadoDeAlta());
//
//    if (tieneSubcategoriasActivas) {
//      throw new IllegalStateException(
//              "No se puede dar de baja: existen subcategorias activas vinculadas.");
//    }
//
//    categoria.darDeBaja();
//
//    this.categoriaRepository.actualizar(categoria);
//  }
//
//  public void darDeBajaSubcategoria(String categoriaID, String subcategoriaID) {
//    Subcategoria subcategoria = this.categoriaRepository.buscarSubcategoriaPorId(subcategoriaID)
//            .orElseThrow(() -> new SubcategoriaInvalidaException
//                    ("La subcategoría con ID " + subcategoriaID + " no existe."));
//
//    boolean tieneProductosActivos = this.productoRepository.buscarPorSubcategoria(subcategoriaID)
//            .stream().anyMatch(producto -> producto.isEstaDadoDeAlta());
//
//    if (tieneProductosActivos) {
//      throw new ProductosActivosVinculadosException
//              ("No se puede dar de baja: existen productos activos vinculados.");
//    }
//
//    subcategoria.darDeBaja();
//  }
//
//  public void quitarIdentificadorAProducto(String productoID) {
//    Producto producto = this.buscarProductoPorID(productoID);
//    producto.setIdentificador(null);
//    this.productoRepository.actualizar(producto);
//  }
//
//  public List<String> verHistorialEstadosDonacion(String donacionID) {
//    val donacion = this.buscarDonacionPorIDEnRepo(donacionID);
//    List<HistorialEstado> historial = donacion.getHistorialEstados();
//
//    return this.historialEstadosDonacion(historial);
//  }
//
//  private List<String> historialEstadosDonacion(List<HistorialEstado> historial) {
//    return historial.stream()
//            .map(estado -> estado.mostrarEstado()).toList();
//  }
//
//  @Override
//  public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
//
//    this.verificarDonacionIngresada(donacionDTO);
//
//    this.verificarDonador(donacionDTO.donadorID());
//    Producto producto = this.buscarProductoPorID(donacionDTO.productoID());
////    Producto producto = this.buscarProductoPorId(donacionDTO.productoID()).orElseGet(() -> this.darAltaProducto(donacionDTO.productoID()));
//
//    val donacion = donacionesDataMapper.toDonacion(donacionDTO, producto);
//    val donacionRegistrada = this.donacionesRepository.guardar(donacion);
//
//    this.enviarPaqueteALogistica(donacionRegistrada);
//
//    return donacionesDataMapper.toDonacionDTO(donacionRegistrada);
//  }
//
//  private void verificarDonacionIngresada(DonacionDTO donacionDTO) {
//    if (donacionDTO == null || donacionDTO.id() != null) {
//      throw new DonacionInvalidaException("Donación inválida");
//    }
//  }
//
//  private void enviarPaqueteALogistica(Donacion donacion) {
//    fachadaLogistica.gestionarDonacion(donacion.getDepositoID(), donacion.getId(), donacion.getProducto().getId(), donacion.getCantidad());
//  }
//
//  private void verificarDonador(String donadorID) {
//    val donadorDTO = fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
//
//    if (donadorDTO == null) {
//      throw new DonadorNoEncontradoException("No se encontró al donador");
//    }
//    if (!fachadaDonadoresYEntidades.puedeDonar(donadorID)) {
//      throw new DonadorNoAptoException("El donador no se encuentra apto para donar");
//    }
//  }
//
//  @Override
//  public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
//    val donacion = this.buscarDonacionPorIDEnRepo(donacionID);
//
//    return this.donacionesDataMapper.toDonacionDTO(donacion);
//  }
//
//  @Override
//  public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado) throws NoSuchElementException {
//    val donacion = this.buscarDonacionPorIDEnRepo(donacionID);
//
//    donacion.cambiarEstado(estado);
//    this.donacionesRepository.actualizar(donacion);
//
//    return this.donacionesDataMapper.toDonacionDTO(donacion);
//  }
//
//  private Donacion buscarDonacionPorIDEnRepo(String donacionID) throws NoSuchElementException {
//    return this.donacionesRepository.buscarPorId(donacionID)
//            .orElseThrow(() -> new DonacionNoEncontradaException("No se encontró donación con ID: " + donacionID));
//  }
//
//  @Override
//  public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) throws NoSuchElementException {
//    val donaciones = this.donacionesRepository.buscarPorIDyFecha(donadorID, fecha);
//
//    if (donaciones.isEmpty()) {
//      throw new DonacionNoEncontradaException
//              ("No se encontraron donaciones para el donador con ID " + donadorID + "A partir de la fecha " + fecha);
//    }
//
//    return donaciones.stream().map(donacion -> this.donacionesDataMapper.toDonacionDTO(donacion)).toList();
//  }
//
//  @Override
//  public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
//    val donacion = this.buscarDonacionPorIDEnRepo(donacionID);
//    QuejaDTO quejaDTO = new QuejaDTO(null, donacionID, donacion.getDonadorID(), LocalDate.now(), descripcion);
//
//    this.fachadaDonadoresYEntidades.agregarQueja(quejaDTO);
//    donacion.agregarQueja(descripcion);
//    this.donacionesRepository.actualizar(donacion);
//
//    return this.donacionesDataMapper.toDonacionDTO(donacion);
//  }
//
//  @Override
//  public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
//    // TODO
//    return null;
//  }
//
//  @Override
//  public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
//    // TODO
//    return null;
//  }
//
//  @Override
//  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
//    this.fachadaDonadoresYEntidades = fachadaDonadoresYEntidades;
//  }
//
//  @Override
//  public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
//    this.fachadaLogistica = fachadaLogistica;
//  }
//}






//@Service
//public class Fachada implements FachadaDonadoresYEntidades {
//
//  private DonadoresRepository donadoresRepository;
//  private DonadoresYEntidadesDataMapper donadoresYEntidadesDataMapper =
//      new DonadoresYEntidadesDataMapper();
//
//  public Fachada() {
//    /*
//    Para que se ejecuten correctamente los tests, se necesita tener un constructor vacio
//    Es decir, que no reciba parametros.
//    Si necesitan un constructor con parametros
//    Java permite tener varios constructores conviviendo sin conflictos.
//    */
//
//    this.donadoresRepository = new InMemoryDonadoresRepo();
//  }
//
//  @Override
//  public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
//    if (this.donadoresRepository.findById(donadorDTO.id()).isPresent()) {
//      throw new DonadorYaExistenteException("Ya existe un donador con ese ID");
//    }
//
//    val donador = donadoresYEntidadesDataMapper.toDonador(donadorDTO);
//
//    val donadorGuardado = this.donadoresRepository.save(donador);
//
//    return donadoresYEntidadesDataMapper.toDonadorDTO(donadorGuardado);
//  }
//
//  @Override
//  public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
//    val donadorOptional = this.donadoresRepository.findById(donadorID);
//
//    if (donadorOptional.isEmpty()) {
//      throw new DonadorNoEncontradoException("No existe un donador con ese ID");
//    }
//    val donadorFinal = donadorOptional.get();
//
//    return donadoresYEntidadesDataMapper.toDonadorDTO(donadorFinal);
//  }
//
//  @Override
//  public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado)
//      throws NoSuchElementException {
//
//    val donadorOptional = this.donadoresRepository.findById(donadorID);
//
//    if (donadorOptional.isEmpty()) {
//      throw new DonadorNoEncontradoException("No existe un donador con ese ID");
//    }
//
//    val donadorFinal = donadorOptional.get();
//    donadorFinal.setEstado(estado);
//
//    this.donadoresRepository.deleteById(donadorID);
//    this.donadoresRepository.save(donadorFinal);
//
//    return donadoresYEntidadesDataMapper.toDonadorDTO(donadorFinal);
//  }
//
//  @Override
//  public DonadorDTO modifcarCategoria(String donadorID, String categoria)
//      throws NoSuchElementException {
//    val donadorOptional = this.donadoresRepository.findById(donadorID);
//    if (donadorOptional.isEmpty()) {
//      throw new DonadorNoEncontradoException("No existe un donador con ese ID");
//    }
//    val donadorFinal = donadorOptional.get();
//    donadorFinal.setCategoria(categoria);
//
//    this.donadoresRepository.deleteById(donadorID);
//    this.donadoresRepository.save(donadorFinal);
//
//    return donadoresYEntidadesDataMapper.toDonadorDTO(donadorFinal);
//  }
//
//  @Override
//  public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {}
//
//  @Override
//  public Boolean puedeDonar(String donadorID) throws NoSuchElementException {
//    // A implementar por el alumno
//    return null;
//  }
//
//  @Override
//  public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) {
//    // A implementar por el alumno
//    return List.of();
//  }
//
//  @Override
//  public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException {
//    // A implementar por el alumno
//    return List.of();
//  }
//
//  @Override
//  public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidad)
//      throws NoSuchElementException {
//    // A implementar por el alumno
//    return null;
//  }
//
//  @Override
//  public DonadorStatsDTO estadisticasDonador(String donadorID) {
//    return null;
//  }
//
//  @Override
//  public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO) {
//    // A implementar por el alumno
//    return null;
//  }
//
//  @Override
//  public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) throws NoSuchElementException {
//    // A implementar por el alumno
//    return null;
//  }
//
//  @Override
//  public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO) {
//    // A implementar por el alumno
//    return null;
//  }
//
//  @Override
//  public QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException {
//    // A implementar por el alumno
//    return null;
//  }
//}

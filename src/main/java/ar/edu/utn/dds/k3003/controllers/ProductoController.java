package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.exceptions.ProductoInvalidoException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@Validated
public class ProductoController {

    private final Fachada fachada;

    @Autowired
    public ProductoController(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> agregarProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        if (productoDTO.id() != null) {
            throw new ProductoInvalidoException("El ID debe ser nulo al crear un producto");
        }
         ProductoDTO productoRegistrado = this.fachada.agregarProducto(productoDTO);
         return ResponseEntity.status(HttpStatus.CREATED).body(productoRegistrado);
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerProductos() {
        List<ProductoDTO> productos = this.fachada.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarProducto(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
        ProductoDTO producto = this.fachada.buscarProductoPorID(id);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> modificarProducto(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id, @Valid @RequestBody ProductoDTO productoDTO) {
        if (productoDTO.id() != null && !productoDTO.id().equals(id)) {
            throw new ProductoInvalidoException("El ID del cuerpo de la petición no coincide con el recurso solicitado en la URL");
        }
        ProductoDTO productoActualizado = this.fachada.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
        this.fachada.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

     @GetMapping("/{id}/existencia")
    public ResponseEntity<Boolean> verificarExistencia(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id)
     {
         boolean existe = this.fachada.verificarExistenciaProducto(id);
         return ResponseEntity.ok(existe);
     }
}

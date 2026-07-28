package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@Validated
public class CategoriaController {

    private final Fachada fachada;

    @Autowired
    public CategoriaController(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> agregarCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        if (categoriaDTO.id() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo al crear una categoría");
        }
        CategoriaDTO categoria = this.fachada.agregarCategoria(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias() {
        List<CategoriaDTO> categorias = this.fachada.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaDTO> eliminarCategoria(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
        this.fachada.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    // SUBCATEGORIAS
    @PostMapping("/{categoriaID}/subcategorias")
    public ResponseEntity<SubcategoriaDTO> agregarSubcategoria(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String categoriaID, @Valid @RequestBody SubcategoriaDTO subcategoriaDTO) {
        if (subcategoriaDTO.id() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo al crear una subcategoría");
        }
        SubcategoriaDTO nuevaSubcategoria = new SubcategoriaDTO(null, subcategoriaDTO.nombre(), categoriaID);
        SubcategoriaDTO subcategoria = this.fachada.agregarSubCategoria(nuevaSubcategoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(subcategoria);
    }
    @GetMapping("/{id}/subcategorias")
    public ResponseEntity<List<SubcategoriaDTO>> obtenerSubcategorias(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
        List<SubcategoriaDTO> subcategorias = this.fachada.obtenerSubcategorias(id);
        return ResponseEntity.ok(subcategorias);
    }
}

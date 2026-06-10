package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import ar.edu.utn.dds.k3003.exceptions.donaciones.CategoriaNoEncontradaException;
import ar.edu.utn.dds.k3003.exceptions.donaciones.ProductoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private Fachada fachada;

    public CategoriaController(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> agregarCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        try {
            CategoriaDTO categoria = this.fachada.agregarCategoria(categoriaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias() {
        List<CategoriaDTO> categorias = this.fachada.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaDTO> eliminarCategoria(@RequestBody String id) {
        try {
            this.fachada.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (CategoriaNoEncontradaException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // SUBCATEGORIAS
    @PostMapping("/{id}/subcategorias")
    public ResponseEntity<SubcategoriaDTO> agregarSubcategoria(@PathVariable String id, @RequestBody SubcategoriaDTO subcategoriaDTO) {
        try {
            SubcategoriaDTO nuevaSubcategoria = new SubcategoriaDTO(subcategoriaDTO.id(), subcategoriaDTO.nombre(), id);
            SubcategoriaDTO subcategoria = this.fachada.agregarSubCategoria(nuevaSubcategoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(subcategoria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @GetMapping("/{id}/subcategorias")
    public ResponseEntity<List<SubcategoriaDTO>> obtenerSubcategorias(@PathVariable String id) {
        try {
            List<SubcategoriaDTO> subcategorias = this.fachada.obtenerSubcategorias(id);
            return ResponseEntity.ok(subcategorias);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}

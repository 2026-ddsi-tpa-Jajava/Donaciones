package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subcategorias")
public class SubcategoriaController {
    private final Fachada fachada;

    @Autowired
    public SubcategoriaController(Fachada fachada) {
        this.fachada = fachada;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SubcategoriaDTO> eliminarSubcategoria(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
            this.fachada.eliminarSubcategoria(id);
            return ResponseEntity.noContent().build();
    }
}

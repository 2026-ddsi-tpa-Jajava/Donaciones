package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import ar.edu.utn.dds.k3003.exceptions.donaciones.CategoriaNoEncontradaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/subcategorias")
public class SubcategoriaController {
    private final Fachada fachada;

    @Autowired
    public SubcategoriaController(Fachada fachada) {
        this.fachada = fachada;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SubcategoriaDTO> eliminarSubcategoria(@RequestBody String id) {
        try {
            this.fachada.eliminarSubcategoria(id);
            return ResponseEntity.noContent().build();
        } catch (CategoriaNoEncontradaException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

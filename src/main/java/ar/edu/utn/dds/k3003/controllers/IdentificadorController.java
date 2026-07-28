package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identificadores")
@Validated
public class IdentificadorController {

    private final Fachada fachada;

    @Autowired
    public IdentificadorController(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping
    public ResponseEntity<IdentificadorDTO> agregarIdentificador(@Valid @RequestBody IdentificadorDTO identificadorDTO) {
        if (identificadorDTO.id() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo al crear un identificador");
        }
        IdentificadorDTO categoria = this.fachada.agregarIdentificador(identificadorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @GetMapping
    public ResponseEntity<List<IdentificadorDTO>> obtenerCategorias() {
        List<IdentificadorDTO> productos = this.fachada.obtenerTodasLosIdentificadores();
        return ResponseEntity.ok(productos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<IdentificadorDTO> eliminarIdentificador(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
        this.fachada.eliminarIdentificador(id);
        return ResponseEntity.noContent().build();
    }
}

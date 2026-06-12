package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reset")
public class ResetController {
    private final Fachada fachada;

    @Autowired
    public ResetController(Fachada fachada) {
        this.fachada = fachada;
    }

    @DeleteMapping
    public ResponseEntity<Void> resetearBaseDeDatos() {
        this.fachada.resetear();
        return ResponseEntity.noContent().build();
    }
}

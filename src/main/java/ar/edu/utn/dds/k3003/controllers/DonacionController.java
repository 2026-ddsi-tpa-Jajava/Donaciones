package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.exceptions.DonacionInvalidaException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/donaciones")
@Validated
public class DonacionController {

    private final Fachada fachada;

    @Autowired
    public DonacionController(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping
    public ResponseEntity<DonacionDTO> registrarDonacion(@Valid @RequestBody DonacionDTO donacionDTO) {
        if (donacionDTO.id() != null) {
            throw new DonacionInvalidaException("El ID debe ser nulo al registrar una donación");
        }
        DonacionDTO donacionRegistrada = this.fachada.registrarDonacion(donacionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(donacionRegistrada);
    }

    @GetMapping
    public ResponseEntity<List<DonacionDTO>> obtenerDonaciones() {
        List<DonacionDTO> donaciones = this.fachada.obtenerTodasLasDonaciones();
        return ResponseEntity.ok(donaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonacionDTO> obtenerDonacion(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id) {
        DonacionDTO donacionBuscada = this.fachada.buscarDonacionPorID(id);
        return ResponseEntity.ok(donacionBuscada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDonacion(@PathVariable Long id) {
        this.fachada.eliminarDonacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DonacionDTO>> buscarDonacionPorDonadorYFecha(
            @RequestParam @NotBlank(message = "El donadorID es obligatorio") String donadorID,
            @RequestParam(required = false) String fechaInicio) {

        List<DonacionDTO> donaciones;

        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            donaciones = this.fachada.buscarPorDonador(donadorID);
        } else {
            LocalDate fecha = LocalDate.parse(fechaInicio);
            donaciones = this.fachada.buscarPorDonadorYFechaInicio(donadorID, fecha);
        }

        return ResponseEntity.ok(donaciones);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DonacionDTO> ModificarEstadoDeDonacion(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id, @RequestBody EstadoDTO request) {
        DonacionDTO donacionActualizada = this.fachada.cambiarEstadoDeDonacion(id, request.estado());
        return ResponseEntity.ok(donacionActualizada);
    }

    @PostMapping("/{id}/queja")
    public ResponseEntity<DonacionDTO> registrarQuejaSobreDonacion(@PathVariable @Pattern(regexp = "^\\d+$", message = "El ID debe ser numérico") String id, @RequestBody QuejaDTO queja) {
        if (queja == null || queja.descripcion() == null || queja.descripcion().trim().isEmpty()) {
            throw new DonacionInvalidaException("La descripción de la queja es obligatoria.");
        }

        String descripcion = queja.descripcion();
        DonacionDTO donacionActualizada = this.fachada.registrarQuejaEnDonacion(id, descripcion);
        return ResponseEntity.status(HttpStatus.CREATED).body(donacionActualizada);
    }
}
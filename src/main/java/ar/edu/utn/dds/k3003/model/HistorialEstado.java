package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table (name = "Historial")
public class HistorialEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDonacionEnum estado;

    @Column(nullable = false)
    private LocalDate fecha;

    public HistorialEstado() {
    }

    public HistorialEstado(EstadoDonacionEnum estado, LocalDate fecha) {
        this.estado = estado;
        this.fecha = fecha;
    }

    public String mostrarEstado() {
        return this.fecha.toString() + " - " + this.estado.name();
    }
}

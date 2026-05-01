package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import lombok.Getter;

import java.time.LocalDate;

public class HistorialEstado {
    @Getter
    private EstadoDonacionEnum estado;
    @Getter
    private LocalDate fecha;

    public HistorialEstado(EstadoDonacionEnum estado, LocalDate fecha) {
        this.estado = estado;
        this.fecha = fecha;
    }

    public String mostrarEstado() {
        return this.fecha.toString() + " - " + this.estado.name();
    }
}

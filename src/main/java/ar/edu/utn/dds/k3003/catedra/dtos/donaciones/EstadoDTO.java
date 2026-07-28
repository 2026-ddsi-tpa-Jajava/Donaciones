package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.NotNull;

public record EstadoDTO(
    @NotNull(message = "El estado es obligatorio")
    EstadoDonacionEnum estado
) {}

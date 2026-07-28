package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.NotNull;

public record IdentificadorDTO(
        String id,
        @NotNull(message = "El tipo de identificador es obligatorio")
        TipoIdentificadorEnum tipo,
        String descripcion
) {}

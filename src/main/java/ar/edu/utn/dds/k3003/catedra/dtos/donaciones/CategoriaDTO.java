package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.NotBlank;

public record CategoriaDTO(
        String id,
        @NotBlank(message = "El nombre de la categoria es obligatorio")
        String nombre,
        String descripcion,
        String subcategoriaID
) {}

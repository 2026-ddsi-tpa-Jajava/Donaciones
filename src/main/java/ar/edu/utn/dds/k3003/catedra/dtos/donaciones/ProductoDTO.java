package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.NotBlank;


public record ProductoDTO(
        String id,
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,
        String descripcion,
        @NotBlank(message = "La categoriaID del producto es obligatoria")
        String categoriaID,
        @NotBlank(message = "El identificadorID del producto es obligatorio")
        String identificadorID
) {}

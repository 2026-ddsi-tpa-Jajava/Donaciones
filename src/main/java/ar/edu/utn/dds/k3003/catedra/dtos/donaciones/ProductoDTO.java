package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.NotBlank;


public record ProductoDTO(
        String id,
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,
        String descripcion,
        @NotBlank(message = "La subcategoriaID del producto es obligatoria")
        String subcategoriaID,
        @NotBlank(message = "El identificadorID del producto es obligatorio")
        String identificadorID
) {}

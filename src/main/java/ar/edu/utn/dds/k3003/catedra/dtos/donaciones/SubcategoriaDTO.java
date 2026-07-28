package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.NotBlank;

public record SubcategoriaDTO (
        String id,
        @NotBlank(message = "El nombre de la subcategoria es obligatoria")
        String nombre,
        String categoriaID
) {}

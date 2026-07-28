package ar.edu.utn.dds.k3003.catedra.dtos.donaciones;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DonacionDTO(
        String id,
        @NotBlank(message = "El donadorID de la donacion es obligatorio")
        String donadorID,
        @NotBlank(message = "El depositoID de la donacion es obligatorio")
        String depositoID,
        String descripcion,
        @NotBlank(message = "El productoID de la donacion es obligatorio")
        String productoID,
        @NotNull(message = "La cantidad donada es obligatoria")
        @Min(value = 1, message = "La cantidad donada debe ser mayor a 0")
        Integer cantidad,
        EstadoDonacionEnum estado
) {}

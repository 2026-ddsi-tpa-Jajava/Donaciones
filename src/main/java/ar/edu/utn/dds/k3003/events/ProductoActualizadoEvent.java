package ar.edu.utn.dds.k3003.events;

import java.time.Instant;

public record ProductoActualizadoEvent(
        String eventoId,
        Instant timestamp,
        Long version,
        String productoId,
        String nombre,
        String descripcion,
        String subcategoriaId,
        String subcategoriaNombre,
        String categoriaNombre
) {}


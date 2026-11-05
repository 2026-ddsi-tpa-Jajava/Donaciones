package ar.edu.utn.dds.k3003.events;

import java.time.Instant;

public record DonacionCreadaEvent(
        String eventoId,
        Instant timestamp,
        Long version,
        String donacionId,
        String donadorId,
        String depositoId,
        String productoId,
        Integer cantidad
) {}
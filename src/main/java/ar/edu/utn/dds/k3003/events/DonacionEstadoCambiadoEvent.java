package ar.edu.utn.dds.k3003.events;

import java.time.Instant;
import java.util.List;

public record DonacionEstadoCambiadoEvent(
        String eventoId,
        Instant timestamp,
        Long version,
        String donacionId,
        String nuevoEstado,
        String descripcionQueja,
        List<String> quejas
) {
    public DonacionEstadoCambiadoEvent(String eventoId, Instant timestamp, Long version, String donacionId, String nuevoEstado) {
        this(eventoId, timestamp, version, donacionId, nuevoEstado, null, List.of());
    }
}
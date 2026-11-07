package ar.edu.utn.dds.k3003.listeners;

import ar.edu.utn.dds.k3003.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class RabbitMQPublisherListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPublisherListener.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisherListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Retryable(value = AmqpException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDonacionCreada(DonacionCreadaEvent evento) {
        log.info("Commit confirmado en DB. Publicando evento {} en RabbitMQ...", evento.eventoId());
        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("id", evento.donacionId());
        payload.put("donadorID", evento.donadorId());
        payload.put("depositoID", evento.depositoId());
        payload.put("productoID", evento.productoId());
        payload.put("cantidad", evento.cantidad());
        payload.put("estado", evento.estado());
        payload.put("categoria", evento.categoria());
        payload.put("subcategoriaID", evento.subcategoriaId());
        payload.put("subcategoria", evento.subcategoria());
        payload.put("fecha", evento.fecha());
        payload.put("fechaIngreso", evento.timestamp().toString());
        Map<String, Object> eventoIntegracion = Map.of(
                "id", evento.eventoId(),
                "tipo", "DONACION_REGISTRADA",
                "version", evento.version(),
                "ocurridoEn", evento.timestamp().toString(),
                "payload", payload
        );
        rabbitTemplate.convertAndSend("consultas.queue", eventoIntegracion);
        log.info("Evento {} publicado exitosamente en consultas.queue.", evento.eventoId());
    }

    @Retryable(value = AmqpException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCambioEstadoDonacion(DonacionEstadoCambiadoEvent evento) {
        log.info("Armando evento de cambio de estado {} para CQRS...", evento.eventoId());

        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("id", evento.donacionId());
        payload.put("estado", evento.nuevoEstado());

        if (evento.descripcionQueja() != null) {
            payload.put("ultimaQuejaDescripcion", evento.descripcionQueja());
        }
        if (evento.quejas() != null && !evento.quejas().isEmpty()) {
            payload.put("quejas", evento.quejas());
        }

        java.util.Map<String, Object> eventoIntegracion = java.util.Map.of(
                "id", evento.eventoId(),
                "tipo", "DONACION_ESTADO_CAMBIADO",
                "version", evento.version(),
                "ocurridoEn", evento.timestamp().toString(),
                "payload", payload
        );
        rabbitTemplate.convertAndSend("consultas.queue", eventoIntegracion);
        log.info("Cambio de estado de donación {} publicado en consultas.queue.", evento.donacionId());
    }

    @Retryable(value = AmqpException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductoCreado(ProductoCreadoEvent evento) {
        log.info("Armando evento de producto creado {} para CQRS...", evento.eventoId());
        java.util.Map<String, Object> payload = java.util.Map.of(
                "id", evento.productoId(),
                "nombre", evento.nombre(),
                "descripcion", evento.descripcion() != null ? evento.descripcion() : "",
                "subcategoriaID", evento.subcategoriaId(),
                "subcategoria", evento.subcategoriaNombre(),
                "categoria", evento.categoriaNombre()
        );
        java.util.Map<String, Object> eventoIntegracion = java.util.Map.of(
                "id", evento.eventoId(),
                "tipo", "PRODUCTO_REGISTRADO",
                "version", evento.version(),
                "ocurridoEn", evento.timestamp().toString(),
                "payload", payload
        );
        rabbitTemplate.convertAndSend("consultas.queue", eventoIntegracion);
        log.info("Producto {} publicado exitosamente en consultas.queue.", evento.productoId());
    }

    @Retryable(value = AmqpException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductoActualizado(ProductoActualizadoEvent evento) {
        log.info("Commit confirmado en DB. Publicando evento de producto actualizado {} para CQRS...", evento.eventoId());
        java.util.Map<String, Object> payload = java.util.Map.of(
                "id", evento.productoId(),
                "nombre", evento.nombre(),
                "descripcion", evento.descripcion() != null ? evento.descripcion() : "",
                "subcategoriaID", evento.subcategoriaId(),
                "subcategoria", evento.subcategoriaNombre(),
                "categoria", evento.categoriaNombre()
        );
        java.util.Map<String, Object> eventoIntegracion = java.util.Map.of(
                "id", evento.eventoId(),
                "tipo", "PRODUCTO_ACTUALIZADO",
                "version", evento.version(),
                "ocurridoEn", evento.timestamp().toString(),
                "payload", payload
        );
        rabbitTemplate.convertAndSend("consultas.queue", eventoIntegracion);
        log.info("Actualización de producto {} publicada exitosamente en consultas.queue.", evento.productoId());
    }

    @Recover
    public void recover(AmqpException e, DonacionCreadaEvent evento) {
        log.error("ALERTA CRITICA: Pérdida de evento tras agotar reintentos. EventoID: {} | DonacionID: {}",
                evento.eventoId(), evento.donacionId(), e);
    }
}
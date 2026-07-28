package ar.edu.utn.dds.k3003.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            DonacionNoEncontradaException.class,
            CategoriaNoEncontradaException.class,
            IdentificadorNoEncontradoException.class,
            ProductoNoEncontradoException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex, HttpServletRequest req) {
        logger.warn("Recurso no encontrado en {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND, req, ex));
    }

    @ExceptionHandler({
            DonacionInvalidaException.class,
            ProductoInvalidoException.class,
            CambioEstadoInvalidoException.class,
            IllegalArgumentException.class,
            DateTimeParseException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex, HttpServletRequest req) {
        logger.warn("Petición inválida en {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, req, ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.warn("Petición rechazada por validación en {}: {}", req.getRequestURI(), errores);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, req, "Error de validación: " + errores));
    }

    @ExceptionHandler(PeticionExternaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleExternalBadRequest(PeticionExternaInvalidaException ex, HttpServletRequest req) {
        logger.warn("Petición rechazada por servicio externo en {}: {} con estado {}", req.getRequestURI(), ex.getMessage(), ex.getStatusCode());

        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());

        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, req, "Fallo en integración externa: " + ex.getMessage()));
    }

    @ExceptionHandler(DonadorNoAptoException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(DonadorNoAptoException ex, HttpServletRequest req) {
        logger.warn("Operación no autorizada en {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED, req, ex));
    }

    @ExceptionHandler(FalloServicioExternoException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(FalloServicioExternoException ex, HttpServletRequest req) {
        logger.error("Fallo de integración en {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(HttpStatus.BAD_GATEWAY, req, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest req) {
        logger.error("Error interno del servidor no controlado en {}", req.getRequestURI(), ex);

        String message = "Ocurrió un error en nuestros servicios. Por favor intente nuevamente más tarde.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, req, message));
    }
}
package ar.edu.utn.dds.k3003.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse of(HttpStatus status, HttpServletRequest req, Exception ex) {
        return new ErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    public static ErrorResponse of(HttpStatus status, HttpServletRequest req, String customMessage) {
        return new ErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                customMessage,
                req.getRequestURI()
        );
    }
}
package ar.edu.utn.dds.k3003.exceptions;

import lombok.Getter;

public class PeticionExternaInvalidaException extends RuntimeException {
    @Getter
    private final int statusCode;

    public PeticionExternaInvalidaException(String mensaje, int statusCode) {
        super(mensaje);
        this.statusCode = statusCode;
    }
}

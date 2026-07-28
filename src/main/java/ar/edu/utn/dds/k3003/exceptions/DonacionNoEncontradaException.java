package ar.edu.utn.dds.k3003.exceptions;

import java.util.NoSuchElementException;

public class DonacionNoEncontradaException extends NoSuchElementException {
    public DonacionNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}

package ar.edu.utn.dds.k3003.exceptions;

import java.util.NoSuchElementException;

public class IdentificadorNoEncontradoException extends NoSuchElementException {
    public IdentificadorNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

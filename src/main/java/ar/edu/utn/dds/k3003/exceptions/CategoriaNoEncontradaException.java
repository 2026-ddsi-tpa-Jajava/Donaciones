package ar.edu.utn.dds.k3003.exceptions;

import java.util.NoSuchElementException;

public class CategoriaNoEncontradaException extends NoSuchElementException {
    public CategoriaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}

package ar.edu.utn.dds.k3003.exceptions;

import java.util.NoSuchElementException;

public class ProductoNoEncontradoException extends NoSuchElementException {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

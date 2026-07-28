package ar.edu.utn.dds.k3003.exceptions;

public class ProductoInvalidoException extends IllegalArgumentException {
    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

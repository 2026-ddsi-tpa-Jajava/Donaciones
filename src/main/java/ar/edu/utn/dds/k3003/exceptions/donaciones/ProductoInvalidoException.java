package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class ProductoInvalidoException extends RuntimeException {
    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

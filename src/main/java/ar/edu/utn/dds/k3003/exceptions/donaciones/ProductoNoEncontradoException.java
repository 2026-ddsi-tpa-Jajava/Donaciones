package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class ProductoNoEncontradoException extends RuntimeException{
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

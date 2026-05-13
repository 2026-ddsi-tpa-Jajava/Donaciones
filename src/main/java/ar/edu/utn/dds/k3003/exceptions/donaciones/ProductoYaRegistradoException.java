package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class ProductoYaRegistradoException extends RuntimeException {
    public ProductoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}

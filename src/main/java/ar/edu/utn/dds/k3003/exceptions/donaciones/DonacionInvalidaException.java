package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class DonacionInvalidaException extends RuntimeException {
    public DonacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}

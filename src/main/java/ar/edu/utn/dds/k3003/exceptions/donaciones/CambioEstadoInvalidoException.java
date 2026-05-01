package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class CambioEstadoInvalidoException extends RuntimeException {
    public CambioEstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

package ar.edu.utn.dds.k3003.exceptions;


public class DonadorNoAptoException extends IllegalStateException {
    public DonadorNoAptoException(String mensaje) {
        super(mensaje);
    }
}

package ar.edu.utn.dds.k3003.exceptions;

public class FalloServicioExternoException extends RuntimeException {
    public FalloServicioExternoException(String mensaje, Throwable causa) {
      super(mensaje, causa);
    }
}

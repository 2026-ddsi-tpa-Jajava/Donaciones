package ar.edu.utn.dds.k3003.exceptions;

import java.util.NoSuchElementException;

public class DonadorNoEncontradoException extends NoSuchElementException {
  public DonadorNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}

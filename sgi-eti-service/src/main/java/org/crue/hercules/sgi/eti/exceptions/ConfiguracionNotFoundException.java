package org.crue.hercules.sgi.eti.exceptions;

/**
 * ConfiguracionNotFoundException
 */
public class ConfiguracionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConfiguracionNotFoundException(Long idConfiguracion) {
    super("Configuracion " + idConfiguracion != null ? idConfiguracion.toString() : "" + " does not exist.");
  }

}
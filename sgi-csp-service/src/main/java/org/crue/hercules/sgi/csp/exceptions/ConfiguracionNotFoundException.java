package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConfiguracionNotFoundException
 */
public class ConfiguracionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConfiguracionNotFoundException(Long configuracionId) {
    super("Configuracion " + configuracionId + " does not exist.");
  }

}

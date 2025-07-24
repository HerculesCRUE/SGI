package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * CargoComiteNotFoundException
 */
public class CargoComiteNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_CARGO_COMITE = "org.crue.hercules.sgi.eti.model.CargoComite.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public CargoComiteNotFoundException(Long cargoComiteId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CARGO_COMITE), cargoComiteId }));
  }

}
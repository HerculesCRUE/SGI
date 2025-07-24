package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EstadoRetrospectivaNotFoundException
 */
public class EstadoRetrospectivaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_ESTADO_RETROSPECTIVA = "org.crue.hercules.sgi.eti.model.EstadoRetrospectiva.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EstadoRetrospectivaNotFoundException(Long estadoRetrospectivaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ESTADO_RETROSPECTIVA), estadoRetrospectivaId }));
  }

}
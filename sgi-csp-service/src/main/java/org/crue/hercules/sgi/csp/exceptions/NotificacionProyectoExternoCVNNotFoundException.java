package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class NotificacionProyectoExternoCVNNotFoundException extends CspNotFoundException {

  /**
   * NotificacionProyectoExternoCVNNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public NotificacionProyectoExternoCVNNotFoundException(Long notificacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(
            NotificacionProyectoExternoCVN.class))
        .parameter("id", notificacionId).build());
  }
}

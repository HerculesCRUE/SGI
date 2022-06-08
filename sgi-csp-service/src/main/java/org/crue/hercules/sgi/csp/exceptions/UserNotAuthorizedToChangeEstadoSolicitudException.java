package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud.Estado;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

public class UserNotAuthorizedToChangeEstadoSolicitudException extends CspNotAllowedException {

  public UserNotAuthorizedToChangeEstadoSolicitudException(Estado oldState, Estado newState) {
    super(ProblemMessage.builder()
        .key(UserNotAuthorizedToChangeEstadoSolicitudException.class)
        .parameter("oldState", oldState)
        .parameter("newState", newState).build());
  }

}

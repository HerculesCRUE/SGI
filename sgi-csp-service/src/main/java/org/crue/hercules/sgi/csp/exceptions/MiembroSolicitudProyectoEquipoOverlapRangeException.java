package org.crue.hercules.sgi.csp.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.http.HttpStatus;

public class MiembroSolicitudProyectoEquipoOverlapRangeException extends ProblemException {
  public static final URI CUSTOM_PROBLEM_TYPE = URI.create("urn:problem-type:team-member-overlap");

  public MiembroSolicitudProyectoEquipoOverlapRangeException() {
    super(Problem.builder().type(CUSTOM_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build())
        .detail(ProblemMessage.builder().key(MiembroSolicitudProyectoEquipoOverlapRangeException.class)
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProyectoEquipo.class)).build())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
  }
}

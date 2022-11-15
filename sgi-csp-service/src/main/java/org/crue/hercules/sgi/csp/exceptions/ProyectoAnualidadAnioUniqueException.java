package org.crue.hercules.sgi.csp.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.http.HttpStatus;

public class ProyectoAnualidadAnioUniqueException extends ProblemException {
  public static final URI CUSTOM_PROBLEM_TYPE = URI.create("urn:problem-type:unique");

  public ProyectoAnualidadAnioUniqueException(Integer anio) {
    super(Problem.builder().type(CUSTOM_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(ProyectoAnualidadAnioUniqueException.class)
            .parameter("anio", anio)
            .parameter("entity", ApplicationContextSupport.getMessage(ProyectoAnualidad.class)).build())

        .status(HttpStatus.BAD_REQUEST.value()).build());
  }

}

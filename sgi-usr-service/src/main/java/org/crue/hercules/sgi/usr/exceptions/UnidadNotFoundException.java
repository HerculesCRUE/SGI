package org.crue.hercules.sgi.usr.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.springframework.http.HttpStatus;

/**
 * UnidadNotFoundException
 */
public class UnidadNotFoundException extends ProblemException {
  public static final URI NOT_FOUND_PROBLEM_TYPE = URI.create("urn:problem-type:not-found");

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public UnidadNotFoundException(Long unidadId) {
    super(Problem.builder().type(NOT_FOUND_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_FOUND").build())
        .detail(ProblemMessage.builder().message(ProblemMessage.builder().key(NotFoundException.class)
            .parameter("entity", ApplicationContextSupport.getMessage(Unidad.class)).parameter("id", unidadId).build())
            .build())
        .status(HttpStatus.NOT_FOUND.value()).build());
  }

}

package org.crue.hercules.sgi.rep.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando se produce un error invocando a un microservicio
 */
public class MicroserviceCallException extends ProblemException {
  public static final URI MICROSERVICE_CALL_PROBLEM = URI.create("urn:problem-type:microservice-call");

  public MicroserviceCallException() {
    super(Problem.builder().type(MICROSERVICE_CALL_PROBLEM)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build())
        .detail(ProblemMessage.builder().key(MicroserviceCallException.class).build())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
  }
}

package org.crue.hercules.sgi.prc.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

public class ProduccionCientificaNotUpdatableException extends ProblemException {
  public static final URI CUSTOM_PROBLEM_TYPE = URI.create("urn:problem-type:access-denied");

  public ProduccionCientificaNotUpdatableException() {
    super(Problem.builder().type(CUSTOM_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "FORBIDDEN").build())
        .detail(ProblemMessage.builder().key(
            ProduccionCientificaNotUpdatableException.class).build())
        .status(HttpStatus.FORBIDDEN.value()).build());
  }
}

package org.crue.hercules.sgi.prc.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

public class RangoInicialNotExistsException extends ProblemException {
  public static final URI CUSTOM_PROBLEM_TYPE = URI.create("urn:problem-type:validation");

  public RangoInicialNotExistsException() {
    super(Problem.builder().type(CUSTOM_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build())
        .detail(ProblemMessage.builder().key(RangoInicialNotExistsException.class).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }
}

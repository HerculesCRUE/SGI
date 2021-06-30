package org.crue.hercules.sgi.framework.exception;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ProblemException {
  public static final URI NOT_FOUND_PROBLEM_TYPE = URI.create("urn:problem-type:not-found");

  private static final long serialVersionUID = 1L;

  public NotFoundException() {
    super(Problem.builder().type(NOT_FOUND_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_FOUND").build()).status(HttpStatus.NOT_FOUND.value())
        .build());
  }

  public NotFoundException(String message) {
    super(Problem.builder().type(NOT_FOUND_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_FOUND").build())
        .detail(ProblemMessage.builder().message(message).build()).status(HttpStatus.NOT_FOUND.value()).build());
  }

}
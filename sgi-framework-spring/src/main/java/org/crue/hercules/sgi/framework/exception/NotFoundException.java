package org.crue.hercules.sgi.framework.exception;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

/**
 * Custom {@link ProblemException} for not found Entities.
 */
public class NotFoundException extends ProblemException {
  /**
   * Cutom URI ("urn:problem-type:not-found") for {@link NotFoundException}s
   */
  public static final URI NOT_FOUND_PROBLEM_TYPE = URI.create("urn:problem-type:not-found");

  private static final long serialVersionUID = 1L;

  /**
   * Builds a generic {@link NotFoundException}
   */
  public NotFoundException() {
    super(Problem.builder().type(NOT_FOUND_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_FOUND").build()).status(HttpStatus.NOT_FOUND.value())
        .build());
  }

  /**
   * Builds a {@link NotFoundException} with the specified message.
   * 
   * @param message the message
   */
  public NotFoundException(String message) {
    super(Problem.builder().type(NOT_FOUND_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_FOUND").build())
        .detail(ProblemMessage.builder().message(message).build()).status(HttpStatus.NOT_FOUND.value()).build());
  }

}
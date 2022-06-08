package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

public class CspNotAllowedException extends ProblemException {

  public CspNotAllowedException(Class<?> clazz) {
    this(ProblemMessage.builder().key(clazz).build());
  }

  public CspNotAllowedException(String detail) {
    super(Problem.builder().type(ProblemExceptionHandler.METHOD_NOT_ALLOWED_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.FORBIDDEN.name()).build())
        .detail(detail)
        .status(HttpStatus.FORBIDDEN.value()).build());
  }

}

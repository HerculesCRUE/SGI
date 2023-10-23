package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

public class EstadoMemoriaIndicarSubsanacionNotValidException extends ProblemException {

  public EstadoMemoriaIndicarSubsanacionNotValidException() {
    super(Problem.builder().type(ProblemExceptionHandler.VALIDATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(EstadoMemoriaIndicarSubsanacionNotValidException.class).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }
}

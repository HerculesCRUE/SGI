package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * RespuestaRetrospectivaFormularioNotValidException
 */
public class RespuestaRetrospectivaFormularioNotValidException extends ProblemException {

  private static final long serialVersionUID = 1L;

  public RespuestaRetrospectivaFormularioNotValidException() {
    super(Problem.builder().type(ProblemExceptionHandler.BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(RespuestaRetrospectivaFormularioNotValidException.class).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }

}

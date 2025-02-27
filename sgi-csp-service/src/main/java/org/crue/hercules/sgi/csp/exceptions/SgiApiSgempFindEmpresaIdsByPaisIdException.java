package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

public class SgiApiSgempFindEmpresaIdsByPaisIdException extends ProblemException {

  public SgiApiSgempFindEmpresaIdsByPaisIdException() {
    super(Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.INTERNAL_SERVER_ERROR.name()).build())
        .detail(ProblemMessage.builder().key(SgiApiSgempFindEmpresaIdsByPaisIdException.class).build())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
  }

}

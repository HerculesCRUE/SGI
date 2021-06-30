package org.crue.hercules.sgi.csp.exceptions.eti;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * Excepción lanzada cuando se produce un error recuperando el contenido de una
 * petición de evaluación de ETICA
 */
public class GetPeticionEvaluacionException extends ProblemException {
  private static final long serialVersionUID = 1L;

  public GetPeticionEvaluacionException() {
    super(Problem.builder().title(ProblemMessage.builder().key(GetPeticionEvaluacionException.class).build()).build());
  }
}

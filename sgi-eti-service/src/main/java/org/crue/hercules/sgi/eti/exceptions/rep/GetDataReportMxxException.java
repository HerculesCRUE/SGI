package org.crue.hercules.sgi.eti.exceptions.rep;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * Excepción lanzada cuando se produce un error recuperando el contenido de un
 * informe MXX de ETICA
 */
public class GetDataReportMxxException extends ProblemException {
  private static final long serialVersionUID = 1L;

  public GetDataReportMxxException() {
    super(Problem.builder().title(ProblemMessage.builder().key(GetDataReportMxxException.class).build()).build());
  }

}

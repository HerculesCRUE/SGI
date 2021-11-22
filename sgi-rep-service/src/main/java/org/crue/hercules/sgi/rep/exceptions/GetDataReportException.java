package org.crue.hercules.sgi.rep.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando se produce un error recuperando el contenido de un
 * informe
 */
public class GetDataReportException extends ProblemException {
  public static final URI REPORT_NO_DATA = URI.create("urn:problem-type:report-no-data");

  public GetDataReportException() {
    super(Problem.builder().type(REPORT_NO_DATA)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build())
        .detail(ProblemMessage.builder().key(GetDataReportException.class).build())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
  }
}

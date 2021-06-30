package org.crue.hercules.sgi.csp.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.http.HttpStatus;

public class ProyectoIVAException extends ProblemException {
  public static final URI PERCENTAGE_IVA_ZERO = URI.create("urn:problem-type:percentage-iva-zero");

  public ProyectoIVAException() {
    super(Problem.builder().type(PERCENTAGE_IVA_ZERO)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build())
        .detail(ProblemMessage.builder().key(ProyectoIVAException.class).build())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
  }
}

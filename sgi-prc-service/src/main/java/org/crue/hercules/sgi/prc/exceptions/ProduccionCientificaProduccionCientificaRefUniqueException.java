package org.crue.hercules.sgi.prc.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.http.HttpStatus;

/**
 * ProduccionCientificaProduccionCientificaRefUniqueException
 */
public class ProduccionCientificaProduccionCientificaRefUniqueException extends ProblemException {
  public static final URI CUSTOM_PROBLEM_TYPE = URI.create("urn:problem-type:unique");

  public ProduccionCientificaProduccionCientificaRefUniqueException() {
    super(Problem.builder().type(CUSTOM_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build())
        .detail(ProblemMessage.builder()
            .key(ProduccionCientificaProduccionCientificaRefUniqueException.class)
            .parameter("entity", ApplicationContextSupport.getMessage(ProduccionCientifica.class))
            .build())

        .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
  }
}

package org.crue.hercules.sgi.rel.exceptions;

import java.net.URI;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.http.HttpStatus;

public class NoRelatedEntitiesException extends ProblemException {
  public static final URI CUSTOM_PROBLEM_TYPE = URI.create("urn:problem-type:no-related-entities");

  public NoRelatedEntitiesException(Class<?> entity, Class<?> related) {
    super(Problem.builder().type(CUSTOM_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build())
        .detail(ProblemMessage.builder().key(NoRelatedEntitiesException.class)
            .parameter("entity", ApplicationContextSupport.getMessage(entity))
            .parameter("related", ApplicationContextSupport.getMessage(related)).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }
}

package org.crue.hercules.sgi.framework.problem.spring.boot.web.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Intercept "application/problem+json" {@link Problem} responses.
 */
public class RestTemplateProblemInterceptor implements ClientHttpRequestInterceptor {
  private ObjectMapper mapper;

  /**
   * Creates a new {@link RestTemplateProblemInterceptor}.
   * 
   * @param mapper the {@link ObjectMapper} used to serialize {@link Problem}
   */
  public RestTemplateProblemInterceptor(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Intercept the response and if the response Content Type is
   * "application/problem+json" a new Problem object is created from the response
   * body and a ProblemException containing the Problem is thrown.
   * 
   * @param request   the request, containing method, URI, and headers
   * @param body      the body of the request
   * @param execution the request execution
   * @return the response
   * @throws IOException      in case of I/O errors
   * @throws ProblemException if the response is of type
   *                          "application/problem+json"
   */
  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    ClientHttpResponse response = execution.execute(request, body);
    if (MediaType.APPLICATION_PROBLEM_JSON.equals(response.getHeaders().getContentType())) {
      Problem result = mapper.readValue(response.getBody(), Problem.class);
      throw new ProblemException(result);
    }
    return response;
  }

}

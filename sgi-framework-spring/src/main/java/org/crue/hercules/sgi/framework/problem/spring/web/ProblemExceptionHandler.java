package org.crue.hercules.sgi.framework.problem.spring.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.Problem.ProblemBuilder;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.extension.FieldError;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.MimeType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ProblemExceptionHandler extends ResponseEntityExceptionHandler {
  public static final URI ACCESS_DENIED_PROBLEM_TYPE = URI.create("urn:problem-type:access-denied");
  public static final URI AUTHENTICATION_PROBLEM_TYPE = URI.create("urn:problem-type:authentication");
  public static final URI BAD_REQUEST_PROBLEM_TYPE = URI.create("urn:problem-type:bad-request");
  public static final URI ILLEGAL_ARGUMENT_PROBLEM_TYPE = URI.create("urn:problem-type:illegal-argument");
  public static final URI METHOD_NOT_ALLOWED_PROBLEM_TYPE = URI.create("urn:problem-type:method-not-allowed");
  public static final URI MISSING_PATH_VARIABLE_PROBLEM_TYPE = URI.create("urn:problem-type:missing-path-variable");
  public static final URI MISSING_REQUEST_PARAMETER_PROBLEM_TYPE = URI
      .create("urn:problem-type:missing-request-parameter");
  public static final URI NOT_ACCEPTABLE_PROBLEM_TYPE = URI.create("urn:problem-type:not-acceptable");
  public static final URI TYPE_MISMATCH_PROBLEM_TYPE = URI.create("urn:problem-type:type-mismatch");
  public static final URI UNSUPPORTED_MEDIA_TYPE_PROBLEM_TYPE = URI.create("urn:problem-type:unsupported-media-type");
  public static final URI VALIDATION_PROBLEM_TYPE = URI.create("urn:problem-type:validation");

  @ExceptionHandler({ ProblemException.class })
  public ResponseEntity<Object> handleProblemException(ProblemException ex, WebRequest request) {
    log.debug("handleProblemException(ProblemException ex, WebRequest request) - start");
    Problem problem = ex.getProblem();
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.valueOf(problem.getStatus());
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug("handleProblemException(ProblemException ex, WebRequest request) - end");
    return response;
  }

  @ExceptionHandler({ IllegalArgumentException.class })
  public final ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    log.debug("handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(ILLEGAL_ARGUMENT_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value())
        .detail(ex.getMessage()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug("handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) - end");
    return response;
  }

  /**
   * ConstraintViolationException is thrown by the MethodValidationInterceptor,
   * which matches on classes or method arguments annotated with @Validated (see
   * MethodValidationPostProcessor).
   * <p>
   * <code>@RequestParam @Min(2) Integer parameter</code> throws
   * ConstraintViolationException
   * <p>
   * <code>@RequestBody @Valid BodyModel body</code> throws
   * MethodArgumentNotValidException
   * <p>
   * see: https://github.com/zalando/problem-spring-web/issues/3
   * 
   * @param ex      the ConstraintViolationException
   * @param request the WebRequest
   * @return the ResponseEntity
   */
  @ExceptionHandler({ ConstraintViolationException.class })
  public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
      WebRequest request) {
    log.debug("handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();

    HttpStatus status = HttpStatus.BAD_REQUEST;
    Problem problem = from(ex.getConstraintViolations()).type(VALIDATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug("handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) - end");
    return response;
  }

  /**
   * Handle method-level security @PreAuthorize, @PostAuthorize, and @Secure
   * Access Denied.
   * 
   * @param ex      the AccessDeniedException
   * @param request the WebRequest
   * @return the ResponseEntity
   */
  @ExceptionHandler({ AccessDeniedException.class })
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    log.debug("handleAccessDeniedException(Exception ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.FORBIDDEN;
    Problem problem = Problem.builder().type(ACCESS_DENIED_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "FORBIDDEN").build()).status(status.value())
        .detail(ProblemMessage.builder().key(AccessDeniedException.class).build()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug("handleAccessDeniedException(Exception ex, WebRequest request) - end");
    return response;
  }

  /**
   * Handle method-level security @PreAuthorize, @PostAuthorize, and @Secure
   * Access Denied.
   * 
   * @param ex      the AuthenticationException
   * @param request the WebRequest
   * @return the ResponseEntity
   */
  @ExceptionHandler({ AuthenticationException.class })
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    log.debug("handleAuthenticationException(AuthenticationException ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    Problem problem = Problem.builder().type(AUTHENTICATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "UNAUTHORIZED").build()).status(status.value())
        .detail(ProblemMessage.builder().key(AuthenticationException.class).build()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug("handleAuthenticationException(AuthenticationException ex, WebRequest request) - end");
    return response;
  }

  @ExceptionHandler({ RestClientResponseException.class })
  public ResponseEntity<Object> handleRestClientResponseException(RestClientResponseException ex, WebRequest request) {
    log.debug("handleRestClientResponseException(RestClientResponseException ex, WebRequest request) - start");
    HttpStatus status = HttpStatus.valueOf(ex.getRawStatusCode());

    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).build()).status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    log.debug("handleRestClientResponseException(RestClientResponseException ex, WebRequest request) - end");
    return response;
  }

  @ExceptionHandler({ Exception.class })
  public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request) {
    log.debug("handleOtherException(Exception ex, WebRequest request) - start");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build()).status(status.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    log.debug("handleOtherException(Exception ex, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.METHOD_NOT_ALLOWED;
    ProblemBuilder builder = Problem.builder().type(METHOD_NOT_ALLOWED_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "METHOD_NOT_ALLOWED").build()).status(status.value())
        .detail(ProblemMessage.builder().key(HttpRequestMethodNotSupportedException.class)
            .parameter("method", ex.getMethod()).build());
    if (ex.getSupportedMethods() != null) {
      builder.extension("supported", Arrays.asList(ex.getSupportedMethods()));
    }
    Problem problem = builder.build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    Problem problem = Problem.builder().type(UNSUPPORTED_MEDIA_TYPE_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "UNSUPPORTED_MEDIA_TYPE").build()).status(status.value())
        .detail(ProblemMessage.builder().key(HttpMediaTypeNotSupportedException.class)
            .parameter("mediaType", ex.getContentType()).build())
        .extension("supported", new ArrayList<>(ex.getSupportedMediaTypes())).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.NOT_ACCEPTABLE;
    Problem problem = Problem.builder().type(NOT_ACCEPTABLE_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_ACCEPTABLE").build()).status(status.value())
        .detail(ProblemMessage.builder().key(HttpMediaTypeNotAcceptableException.class)
            .parameter("mediaType",
                headers.getAccept().stream().map(MimeType::toString).collect(Collectors.joining(", ")))
            .build())
        .extension("supported", new ArrayList<>(ex.getSupportedMediaTypes())).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(MISSING_PATH_VARIABLE_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value())
        .detail(ProblemMessage.builder().key(MissingPathVariableException.class)
            .parameter("variableName", ex.getVariableName()).build())
        .extension("name", ex.getVariableName()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(MISSING_REQUEST_PARAMETER_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value())
        .detail(ProblemMessage.builder().key(MissingServletRequestParameterException.class)
            .parameter("parameterName", ex.getParameterName()).parameter("parameterType", ex.getParameterType())
            .build())
        .extension("name", ex.getParameterName()).extension("type", ex.getParameterType()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  // TODO Support other ServletRequestBindingException extended exceptions

  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value())
        .detail(ex.getMessage()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build()).status(status.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.debug(
        "handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(TYPE_MISMATCH_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value())
        .detail(
            ProblemMessage.builder().key(TypeMismatchException.class).parameter("propertyName", ex.getPropertyName())
                .parameter("propertyType", ex.getRequiredType().getSimpleName()).build())
        .extension("name", ex.getPropertyName()).extension("type", ex.getRequiredType().getSimpleName()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value())
        .detail(ProblemMessage.builder().key(HttpMessageNotReadableException.class).build()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build()).status(status.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = from(ex.getBindingResult())
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build())
        .status(status.value())
        .detail(ProblemMessage.builder().key(MissingServletRequestPartException.class)
            .parameter("requestPartName", ex.getRequestPartName()).build())
        .extension("param", ex.getRequestPartName()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.debug(
        "handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.BAD_REQUEST;
    Problem problem = from(ex.getBindingResult())
        .title(ProblemMessage.builder().key(HttpStatus.class, "BAD_REQUEST").build()).status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.NOT_FOUND;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "NOT_FOUND").build()).status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    status = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, "INTERNAL_SERVER_ERROR").build()).status(status.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug(
        "handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    if (body instanceof Problem) {
      headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
      log.error(((Problem) body).getInstance().toString(), ex);
    } else {
      log.error("Exception thrown", ex);
    }
    ResponseEntity<Object> response = super.handleExceptionInternal(ex, body, headers, status, request);
    log.debug(
        "handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  private ProblemBuilder from(BindingResult bindingResult) {
    ArrayList<FieldError> details = new ArrayList<>();
    bindingResult.getFieldErrors()
        .forEach(f -> details.add(new FieldError(f.getField(), ApplicationContextSupport.getMessage(f))));
    return Problem.builder().type(VALIDATION_PROBLEM_TYPE)
        .detail(ProblemMessage.builder().key(BindingResult.class).build()).extension("errors", details);
  }

  private ProblemBuilder from(Set<ConstraintViolation<?>> constraintViolations) {
    ArrayList<FieldError> details = new ArrayList<>();
    constraintViolations.stream()
        .forEach(violation -> details.add(new FieldError(determineField(violation), violation.getMessage())));
    return Problem.builder().type(VALIDATION_PROBLEM_TYPE)
        .detail(ProblemMessage.builder().key(BindingResult.class).build()).extension("errors", details);
  }

  /**
   * Determine a field for the given constraint violation.
   * <p>
   * The default implementation returns the stringified property path.
   * 
   * @param violation the current JSR-303 ConstraintViolation
   * @return the Spring-reported field
   * @see org.springframework.validation.beanvalidation.SpringValidatorAdapter
   */
  protected String determineField(ConstraintViolation<?> violation) {
    Path path = violation.getPropertyPath();
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Path.Node node : path) {
      if (node.isInIterable()) {
        sb.append('[');
        Object index = node.getIndex();
        if (index == null) {
          index = node.getKey();
        }
        if (index != null) {
          sb.append(index);
        }
        sb.append(']');
      }
      String name = node.getName();
      if (name != null && node.getKind() == ElementKind.PROPERTY && !name.startsWith("<")) {
        if (!first) {
          sb.append('.');
        }
        first = false;
        sb.append(name);
      }
    }
    return sb.toString();
  }
}

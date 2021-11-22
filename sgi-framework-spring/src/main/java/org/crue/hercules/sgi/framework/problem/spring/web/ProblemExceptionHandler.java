package org.crue.hercules.sgi.framework.problem.spring.web;

import java.io.Serializable;
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
import org.springframework.dao.DataAccessException;
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

/**
 * {@link ResponseEntityExceptionHandler} that handles and convers
 * {@link Exception}s into {@link MediaType#APPLICATION_PROBLEM_JSON} responses.
 */
@RestControllerAdvice
@Slf4j
public class ProblemExceptionHandler extends ResponseEntityExceptionHandler {
  /**
   * {@link Problem} type {@link URI} for {@link AccessDeniedException}
   * ("urn:problem-type:access-denied")
   */
  public static final URI ACCESS_DENIED_PROBLEM_TYPE = URI.create("urn:problem-type:access-denied");
  /**
   * {@link Problem} type {@link URI} for {@link AuthenticationException}
   * ("urn:problem-type:authentication")
   */
  public static final URI AUTHENTICATION_PROBLEM_TYPE = URI.create("urn:problem-type:authentication");
  /**
   * {@link Problem} type {@link URI} for {@link ServletRequestBindingException}
   * and {@link HttpMessageNotReadableException} ("urn:problem-type:bad-request")
   */
  public static final URI BAD_REQUEST_PROBLEM_TYPE = URI.create("urn:problem-type:bad-request");
  /**
   * {@link Problem} type {@link URI} for {@link IllegalArgumentException}
   * ("urn:problem-type:illegal-argument")
   */
  public static final URI ILLEGAL_ARGUMENT_PROBLEM_TYPE = URI.create("urn:problem-type:illegal-argument");
  /**
   * {@link Problem} type {@link URI} for
   * {@link HttpRequestMethodNotSupportedException}
   * ("urn:problem-type:method-not-allowed")
   */
  public static final URI METHOD_NOT_ALLOWED_PROBLEM_TYPE = URI.create("urn:problem-type:method-not-allowed");
  /**
   * {@link Problem} type {@link URI} for {@link MissingPathVariableException}
   * ("urn:problem-type:missing-path-variable")
   */
  public static final URI MISSING_PATH_VARIABLE_PROBLEM_TYPE = URI.create("urn:problem-type:missing-path-variable");
  /**
   * {@link Problem} type {@link URI} for
   * {@link MissingServletRequestParameterException}
   * ("urn:problem-type:missing-request-parameter")
   */
  public static final URI MISSING_REQUEST_PARAMETER_PROBLEM_TYPE = URI
      .create("urn:problem-type:missing-request-parameter");
  /**
   * {@link Problem} type {@link URI} for
   * {@link HttpMediaTypeNotAcceptableException}
   * ("urn:problem-type:not-acceptable")
   */
  public static final URI NOT_ACCEPTABLE_PROBLEM_TYPE = URI.create("urn:problem-type:not-acceptable");
  /**
   * {@link Problem} type {@link URI} for {@link TypeMismatchException}
   * ("urn:problem-type:type-mismatch")
   */
  public static final URI TYPE_MISMATCH_PROBLEM_TYPE = URI.create("urn:problem-type:type-mismatch");
  /**
   * {@link Problem} type {@link URI} for
   * {@link HttpMediaTypeNotSupportedException}
   * ("urn:problem-type:unsupported-media-type")
   */
  public static final URI UNSUPPORTED_MEDIA_TYPE_PROBLEM_TYPE = URI.create("urn:problem-type:unsupported-media-type");
  /**
   * {@link Problem} type {@link URI} for {@link ConstraintViolationException}
   * {@link BindingResult} and {@link ConstraintViolation}
   * ("urn:problem-type:validation")
   */
  public static final URI VALIDATION_PROBLEM_TYPE = URI.create("urn:problem-type:validation");
  /**
   * {@link Problem} type {@link URI} for {@link DataAccessException}
   * ("urn:problem-type:data-access")
   */
  public static final URI DATA_ACCESS_PROBLEM_TYPE = URI.create("urn:problem-type:data-access");

  private static final String EXTENSION_SUPPORTED = "supported";

  /**
   * Handles {@link ProblemException}s.
   * 
   * @param ex      the exception thrown
   * @param request the current {@link WebRequest}
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
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

  /**
   * Handles {@link IllegalArgumentException}s.
   * 
   * @param ex      the exception thrown
   * @param request the current {@link WebRequest}
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
  @ExceptionHandler({ IllegalArgumentException.class })
  public final ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    log.debug("handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(ILLEGAL_ARGUMENT_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).build()).status(status.value())
        .detail(ex.getLocalizedMessage()).build();
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
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
  @ExceptionHandler({ ConstraintViolationException.class })
  public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
      WebRequest request) {
    log.debug("handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();

    HttpStatus status = HttpStatus.BAD_REQUEST;
    Problem problem = from(ex.getConstraintViolations()).type(VALIDATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).build()).status(status.value()).build();
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
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
  @ExceptionHandler({ AccessDeniedException.class })
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    log.debug("handleAccessDeniedException(Exception ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.FORBIDDEN;
    Problem problem = Problem.builder().type(ACCESS_DENIED_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).build()).status(status.value())
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
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
  @ExceptionHandler({ AuthenticationException.class })
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    log.debug("handleAuthenticationException(AuthenticationException ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    Problem problem = Problem.builder().type(AUTHENTICATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).build()).status(status.value())
        .detail(ProblemMessage.builder().key(AuthenticationException.class).build()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, status, request);
    log.debug("handleAuthenticationException(AuthenticationException ex, WebRequest request) - end");
    return response;
  }

  /**
   * Handles {@link RestClientResponseException}s.
   * 
   * @param ex      the exception thrown
   * @param request the current {@link WebRequest}
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
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

  // TODO Support other DataAccessException extended exceptions (like
  // DuplicateKeyException)
  /**
   * Handles {@link DataAccessException}s.
   * 
   * @param ex      the exception thrown
   * @param request the current {@link WebRequest}
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
  @ExceptionHandler({ DataAccessException.class })
  public ResponseEntity<Object> handleDataAccessException(DataAccessException ex, WebRequest request) {
    log.debug("handleDataAccessException(DataAccessException ex, WebRequest request) - start");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    Problem problem = Problem.builder().type(DATA_ACCESS_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).message(ex.getLocalizedMessage()).build())
        .status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    log.debug("handleDataAccessException(DataAccessException ex, WebRequest request) - end");
    return response;
  }

  /**
   * Handles generic {@link Exception}s.
   * 
   * @param ex      the exception thrown
   * @param request the current {@link WebRequest}
   * @return the {@link ResponseEntity} holding the {@link Problem}
   */
  @ExceptionHandler({ Exception.class })
  public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request) {
    log.debug("handleOtherException(Exception ex, WebRequest request) - start");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, status.name()).build()).status(status.value()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    log.debug("handleOtherException(Exception ex, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.METHOD_NOT_ALLOWED;
    ProblemBuilder builder = Problem.builder().type(METHOD_NOT_ALLOWED_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(HttpRequestMethodNotSupportedException.class)
            .parameter("method", ex.getMethod()).build());
    if (ex.getSupportedMethods() != null) {
      builder.extension(EXTENSION_SUPPORTED, (Serializable) Arrays.asList(ex.getSupportedMethods()));
    }
    Problem problem = builder.build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    Problem problem = Problem.builder().type(UNSUPPORTED_MEDIA_TYPE_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(HttpMediaTypeNotSupportedException.class)
            .parameter("mediaType", ex.getContentType()).build())
        .extension(EXTENSION_SUPPORTED, new ArrayList<>(ex.getSupportedMediaTypes())).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.NOT_ACCEPTABLE;
    Problem problem = Problem.builder().type(NOT_ACCEPTABLE_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(HttpMediaTypeNotAcceptableException.class)
            .parameter("mediaType",
                headers.getAccept().stream().map(MimeType::toString).collect(Collectors.joining(", ")))
            .build())
        .extension(EXTENSION_SUPPORTED, new ArrayList<>(ex.getSupportedMediaTypes())).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(MISSING_PATH_VARIABLE_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(MissingPathVariableException.class)
            .parameter("variableName", ex.getVariableName()).build())
        .extension("variableName", ex.getVariableName()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(MISSING_REQUEST_PARAMETER_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(MissingServletRequestParameterException.class)
            .parameter("parameterName", ex.getParameterName()).parameter("parameterType", ex.getParameterType())
            .build())
        .extension("parameterName", ex.getParameterName()).extension("parameterType", ex.getParameterType()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
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
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ex.getLocalizedMessage()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.debug(
        "handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    String requiredType = "Unknown";
    if (ex.getRequiredType() != null) {
      Class<?> clazz = ex.getRequiredType();
      if (clazz != null) {
        requiredType = clazz.getSimpleName();
      }
    }
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(TYPE_MISMATCH_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(TypeMismatchException.class)
            .parameter("propertyName", ex.getPropertyName()).parameter("propertyType", requiredType).build())
        .extension("propertyName", ex.getPropertyName()).extension("propertyType", requiredType).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().type(BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .detail(ProblemMessage.builder().key(HttpMessageNotReadableException.class).build()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = from(ex.getBindingResult())
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = Problem.builder().title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build())
        .status(newStatus.value())
        .detail(ProblemMessage.builder().key(MissingServletRequestPartException.class)
            .parameter("requestPartName", ex.getRequestPartName()).build())
        .extension("param", ex.getRequestPartName()).build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.debug(
        "handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.BAD_REQUEST;
    Problem problem = from(ex.getBindingResult())
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.NOT_FOUND;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
    log.debug(
        "handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return response;
  }

  @Override
  protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    HttpStatus newStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    Problem problem = Problem.builder().type(Problem.UNKNOWN_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, newStatus.name()).build()).status(newStatus.value())
        .build();
    ResponseEntity<Object> response = handleExceptionInternal(ex, problem, headers, newStatus, request);
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

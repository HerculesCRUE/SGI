package org.crue.hercules.sgi.framework.security.web.exception.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandlerExceptionResolverDelegator implements WebSecurityExceptionHandler {
  private HandlerExceptionResolver resolver;

  public HandlerExceptionResolverDelegator(HandlerExceptionResolver resolver) {
    log.debug("HandlerExceptionResolverDelegator(HandlerExceptionResolver resolver) - start");
    this.resolver = resolver;
    log.debug("HandlerExceptionResolverDelegator(HandlerExceptionResolver resolver) - end");
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    log.debug(
        "handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) - start");
    resolver.resolveException(request, response, null, accessDeniedException);
    log.debug(
        "handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) - end");
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    log.debug(
        "commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) - start");
    resolver.resolveException(request, response, null, authException);
    log.debug(
        "commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) - end");
  }

}

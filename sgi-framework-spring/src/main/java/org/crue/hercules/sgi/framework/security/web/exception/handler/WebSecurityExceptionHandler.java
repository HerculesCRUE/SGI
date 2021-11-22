package org.crue.hercules.sgi.framework.security.web.exception.handler;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Interface that extends {@link AccessDeniedHandler} and
 * {@link AuthenticationEntryPoint}
 */
public interface WebSecurityExceptionHandler extends AccessDeniedHandler, AuthenticationEntryPoint {

}

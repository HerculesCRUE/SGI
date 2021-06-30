package org.crue.hercules.sgi.framework.security.web.exception.handler;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

public interface WebSecurityExceptionHandler extends AccessDeniedHandler, AuthenticationEntryPoint {

}

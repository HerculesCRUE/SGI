package org.crue.hercules.sgi.framework.web.config;

import org.crue.hercules.sgi.framework.security.access.expression.SgiMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Provide a {@link SgiMethodSecurityExpressionHandler} that is registered with
 * the {@link ExpressionBasedPreInvocationAdvice}.
 */
@Component
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SgiSecurityConfig extends GlobalMethodSecurityConfiguration {
  /**
   * The {@link SgiMethodSecurityExpressionHandler} that is registered with the
   * {@link ExpressionBasedPreInvocationAdvice}.
   */
  protected DefaultMethodSecurityExpressionHandler sgiMethodExpressionHandler = new SgiMethodSecurityExpressionHandler();

  /**
   * Provide a {@link SgiMethodSecurityExpressionHandler} that is registered with
   * the {@link ExpressionBasedPreInvocationAdvice}.
   *
   * @return the {@link SgiMethodSecurityExpressionHandler} to use
   */
  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    log.debug("createExpressionHandler() - start");
    MethodSecurityExpressionHandler returnValue = sgiMethodExpressionHandler;
    log.debug("createExpressionHandler() - end");
    return returnValue;
  }
}
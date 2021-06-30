package org.crue.hercules.sgi.framework.web.config;

import org.crue.hercules.sgi.framework.security.access.expression.SgiMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SgiSecurityConfig extends GlobalMethodSecurityConfiguration {
  protected DefaultMethodSecurityExpressionHandler defaultMethodExpressionHandler = new SgiMethodSecurityExpressionHandler();

  /**
   * Provide a {@link MethodSecurityExpressionHandler} that is registered with the
   * {@link ExpressionBasedPreInvocationAdvice}. The default is
   * {@link DefaultMethodSecurityExpressionHandler} which optionally will Autowire
   * an {@link AuthenticationTrustResolver}.
   *
   * <p>
   * Subclasses may override this method to provide a custom
   * {@link MethodSecurityExpressionHandler}
   * </p>
   *
   * @return the {@link MethodSecurityExpressionHandler} to use
   */
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    log.debug("createExpressionHandler() - start");
    MethodSecurityExpressionHandler returnValue = defaultMethodExpressionHandler;
    log.debug("createExpressionHandler() - end");
    return returnValue;
  }
}
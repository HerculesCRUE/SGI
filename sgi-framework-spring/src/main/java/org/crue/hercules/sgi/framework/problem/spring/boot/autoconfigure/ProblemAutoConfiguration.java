package org.crue.hercules.sgi.framework.problem.spring.boot.autoconfigure;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.jackson.ProblemModule;
import org.crue.hercules.sgi.framework.security.web.exception.handler.HandlerExceptionResolverDelegator;
import org.crue.hercules.sgi.framework.security.web.exception.handler.WebSecurityExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * AutoConfiguration for {@link Problem} support.
 */
@Configuration
@ComponentScan(basePackageClasses = { Problem.class })
public class ProblemAutoConfiguration {

  /**
   * Register a custom {@link WebSecurityExceptionHandler} to delegate exception
   * handling to the provided {@link HandlerExceptionResolver}.
   * 
   * @param resolver the {@link HandlerExceptionResolver} to deletage exception
   *                 handling
   * @return the {@link WebSecurityExceptionHandler}
   */
  @ConditionalOnWebApplication
  @ConditionalOnMissingBean
  @Bean
  public WebSecurityExceptionHandler webSecurityExceptionHandler(
      @Qualifier(DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME) HandlerExceptionResolver resolver) {
    return new HandlerExceptionResolverDelegator(resolver);
  }

  /**
   * Registers a custom Jackson module for {@link Problem} transformations.
   * 
   * @return the Jackson module for {@link Problem} transformations
   */
  @ConditionalOnMissingBean
  @Bean
  public ProblemModule problemModule() {
    return new ProblemModule();
  }
}

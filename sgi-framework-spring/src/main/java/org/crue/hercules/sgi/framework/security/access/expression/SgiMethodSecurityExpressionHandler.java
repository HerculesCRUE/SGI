package org.crue.hercules.sgi.framework.security.access.expression;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SgiMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

  /**
   * Creates the root object for expression evaluation.
   */
  @Override
  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
      MethodInvocation invocation) {
    log.debug("createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) - start");
    SgiMethodSecurityExpressionRoot root = new SgiMethodSecurityExpressionRoot(authentication);
    root.setThis(invocation.getThis());
    root.setPermissionEvaluator(getPermissionEvaluator());
    root.setTrustResolver(getTrustResolver());
    root.setRoleHierarchy(getRoleHierarchy());
    root.setDefaultRolePrefix(getDefaultRolePrefix());

    log.debug("createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) - end");
    return root;
  }
}
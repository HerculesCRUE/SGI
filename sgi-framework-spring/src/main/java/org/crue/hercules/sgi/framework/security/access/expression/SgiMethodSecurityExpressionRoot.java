package org.crue.hercules.sgi.framework.security.access.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SgiMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {
  private Set<String> roles;
  private Map<String, List<String>> rolesMap;
  private RoleHierarchy roleHierarchy;
  private String defaultRolePrefix = "ROLE_";

  private Object filterObject;
  private Object returnObject;
  private Object target;

  public void setFilterObject(Object filterObject) {
    log.debug("setFilterObject(Object filterObject) - start");
    this.filterObject = filterObject;
    log.debug("setFilterObject(Object filterObject) - end");
  }

  public Object getFilterObject() {
    log.debug("getFilterObject() - start");
    log.debug("getFilterObject() - end");
    return filterObject;
  }

  public void setReturnObject(Object returnObject) {
    log.debug("setReturnObject(Object returnObject) - start");
    this.returnObject = returnObject;
    log.debug("setReturnObject(Object returnObject) - end");
  }

  public Object getReturnObject() {
    log.debug("getReturnObject() - start");
    log.debug("getReturnObject() - end");
    return returnObject;
  }

  /**
   * Sets the "this" property for use in expressions. Typically this will be the
   * "this" property of the {@code JoinPoint} representing the method invocation
   * which is being protected.
   *
   * @param target the target object on which the method in is being invoked.
   */
  void setThis(Object target) {
    log.debug("setThis(Object target) - start");
    this.target = target;
    log.debug("setThis(Object target) - end");
  }

  public Object getThis() {
    log.debug("getThis() - start");
    log.debug("getThis() - end");
    return target;
  }

  public SgiMethodSecurityExpressionRoot(Authentication authentication) {
    super(authentication);
    log.debug("SgiMethodSecurityExpressionRoot(Authentication authentication) - start");
    log.debug("SgiMethodSecurityExpressionRoot(Authentication authentication) - end");
  }

  public final boolean hasAuthorityForAnyUO(String authority) {
    log.debug("hasAuthorityForAnyUO(String authority) - start");
    boolean returnValue = hasAnyAuthorityForAnyUO(authority);
    log.debug("hasAuthorityForAnyUO(String authority) - end");
    return returnValue;
  }

  public final boolean hasAnyAuthorityForAnyUO(String... authorities) {
    log.debug("hasAnyAuthorityForAnyUO(String... authorities) - start");
    boolean returnValue = hasAnyAuthorityNameForAnyUO(null, authorities);
    log.debug("hasAnyAuthorityForAnyUO(String... authorities) - end");
    return returnValue;
  }

  public final boolean hasRoleForAnyUO(String role) {
    log.debug("hasRoleForAnyUO(String role) - start");
    boolean returnValue = hasAnyRoleForAnyUO(role);
    log.debug("hasRoleForAnyUO(String role) - start");
    return returnValue;
  }

  public final boolean hasAnyRoleForAnyUO(String... roles) {
    log.debug("hasAnyRoleForAnyUO(String... roles) - start");
    boolean returnValue = hasAnyAuthorityNameForAnyUO(defaultRolePrefix, roles);
    log.debug("hasAnyRoleForAnyUO(String... roles) - end");
    return returnValue;
  }

  public final String[] getAuthorityUOs(String authority) {
    log.debug("getAuthorityUOs(String authority) - start");
    String[] returnValue = getAuthorityNameUOs(null, authority);
    log.debug("getAuthorityUOs(String authority) - end");
    return returnValue;
  }

  public final String[] getRolUOs(String authority) {
    log.debug("getRolUOs(String authority) - start");
    String[] returnValue = getAuthorityNameUOs(defaultRolePrefix, authority);
    log.debug("getRolUOs(String authority) - end");
    return returnValue;
  }

  private boolean hasAnyAuthorityNameForAnyUO(String prefix, String... roles) {
    log.debug("hasAnyAuthorityNameForAnyUO(String prefix, String... roles) - start");
    Set<String> roleSet = getAuthoritySet();

    for (String role : roles) {
      String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
      if (roleSet.contains(defaultedRole)) {
        log.debug("hasAnyAuthorityNameForAnyUO(String prefix, String... roles) - end");
        return true;
      }
    }

    log.debug("hasAnyAuthorityNameForAnyUO(String prefix, String... roles) - end");
    return false;
  }

  private String[] getAuthorityNameUOs(String prefix, String role) {
    log.debug("getAuthorityNameUOs(String prefix, String role) - start");
    Map<String, List<String>> roleMap = getAuthorityMap();
    String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
    List<String> uoList = roleMap.get(defaultedRole);
    String[] uoArray = new String[] {};
    if (uoList != null) {
      uoArray = uoList.toArray(uoArray);
    }
    log.debug("getAuthorityNameUOs(String prefix, String role) - end");
    return uoArray;
  }

  /**
   * <p>
   * Sets the default prefix to be added to {@link #hasAnyRole(String...)} or
   * {@link #hasRole(String)}. For example, if hasRole("ADMIN") or
   * hasRole("ROLE_ADMIN") is passed in, then the role ROLE_ADMIN will be used
   * when the defaultRolePrefix is "ROLE_" (default).
   * </p>
   *
   * <p>
   * If null or empty, then no default role prefix is used.
   * </p>
   *
   * @param defaultRolePrefix the default prefix to add to roles. Default "ROLE_".
   */
  @Override
  public void setDefaultRolePrefix(String defaultRolePrefix) {
    log.debug("setDefaultRolePrefix(String defaultRolePrefix) - start");
    super.setDefaultRolePrefix(defaultRolePrefix);
    this.defaultRolePrefix = defaultRolePrefix;
    log.debug("setDefaultRolePrefix(String defaultRolePrefix) - end");
  }

  /**
   * Prefixes role with defaultRolePrefix if defaultRolePrefix is non-null and if
   * role does not already start with defaultRolePrefix.
   *
   * @param defaultRolePrefix
   * @param role
   * @return
   */
  private static String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
    log.debug("getRoleWithDefaultPrefix(String defaultRolePrefix, String role) - start");
    if (role == null || defaultRolePrefix == null || defaultRolePrefix.length() == 0
        || role.startsWith(defaultRolePrefix)) {
      log.debug("getRoleWithDefaultPrefix(String defaultRolePrefix, String role) - end");
      return role;
    }
    String returnValue = defaultRolePrefix + role;
    log.debug("getRoleWithDefaultPrefix(String defaultRolePrefix, String role) - end");
    return returnValue;
  }

  private Set<String> getAuthoritySet() {
    log.debug("getAuthoritySet() - start");
    if (roles == null) {
      Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

      if (roleHierarchy != null) {
        userAuthorities = roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
      }

      roles = authorityListToSetWithoutUO(userAuthorities);
    }

    log.debug("getAuthoritySet() - end");
    return roles;
  }

  private Map<String, List<String>> getAuthorityMap() {
    log.debug(" getAuthorityMap() - start");
    if (rolesMap == null) {
      Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

      if (roleHierarchy != null) {
        userAuthorities = roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
      }

      rolesMap = authorityListToMapWithUOs(userAuthorities);
    }

    log.debug(" getAuthorityMap() - end");
    return rolesMap;
  }

  /**
   * Converts an array of GrantedAuthority objects to a Set.
   * 
   * @return a Set of the Strings obtained from each call to
   *         GrantedAuthority.getAuthority()
   */
  private static Set<String> authorityListToSetWithoutUO(Collection<? extends GrantedAuthority> userAuthorities) {
    log.debug("authorityListToSetWithoutUO(Collection<? extends GrantedAuthority> userAuthorities) - start");
    Assert.notNull(userAuthorities, "userAuthorities cannot be null");
    Set<String> set = new HashSet<>(userAuthorities.size());

    for (GrantedAuthority authority : userAuthorities) {
      String auth = authority.getAuthority();
      set.add(getAuthorityWithoutUO(auth));
    }

    log.debug("authorityListToSetWithoutUO(Collection<? extends GrantedAuthority> userAuthorities) - end");
    return set;
  }

  /**
   * Converts an array of GrantedAuthority objects to a Map of Authorities and
   * Unidades Organizativas.
   * 
   * @return a Map of the Strings obtained from each call to
   *         GrantedAuthority.getAuthority()
   */
  private static Map<String, List<String>> authorityListToMapWithUOs(
      Collection<? extends GrantedAuthority> userAuthorities) {
    log.debug("authorityListToMapWithUOs(Collection<? extends GrantedAuthority> userAuthorities) - start");
    Assert.notNull(userAuthorities, "userAuthorities cannot be null");
    Map<String, List<String>> map = new HashMap<>();

    for (GrantedAuthority authority : userAuthorities) {
      String auth = authority.getAuthority();
      String uo = null;
      int index = auth.indexOf("_");
      if (index != -1) {
        uo = auth.substring(index + 1, auth.length());
        auth = auth.substring(0, index);
      }
      List<String> uoList = map.get(auth);
      if (uoList == null) {
        uoList = new ArrayList<>();
      }
      if (uo != null) {
        uoList.add(uo);
      }
      map.put(auth, uoList);
    }

    log.debug("authorityListToMapWithUOs(Collection<? extends GrantedAuthority> userAuthorities) - end");
    return map;
  }

  private static String getAuthorityWithoutUO(String authority) {
    log.debug("getAuthorityWithoutUO(String authority) - start");
    int index = authority.indexOf("_");
    if (index != -1) {
      String returnValue = authority.substring(0, index);
      log.debug("getAuthorityWithoutUO(String authority) - start");
      return returnValue;
    }
    log.debug("getAuthorityWithoutUO(String authority) - end");
    return authority;
  }

  @Override
  public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
    log.debug("setRoleHierarchy(RoleHierarchy roleHierarchy) - start");
    super.setRoleHierarchy(roleHierarchy);
    this.roleHierarchy = roleHierarchy;
    log.debug("setRoleHierarchy(RoleHierarchy roleHierarchy) - end");
  }

}
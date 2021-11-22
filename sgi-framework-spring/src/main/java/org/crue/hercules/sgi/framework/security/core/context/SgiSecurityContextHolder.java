package org.crue.hercules.sgi.framework.security.core.context;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SgiSecurityContextHolder
 */
public class SgiSecurityContextHolder {

  private SgiSecurityContextHolder() {

  }

  /**
   * Obtain the list of authorities of the currently authenticated user
   * 
   * @return the list of authorizations
   * @throws InsufficientAuthenticationException when no authenticated user
   */
  public static List<String> getAuthorities() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new InsufficientAuthenticationException("Authentication null or not authenticated");
    }
    return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).distinct()
        .collect(Collectors.toList());
  }

  /**
   * Check if the current user has an authority
   * 
   * @param authority the autority to check
   * @return true if has the autority, false otherwise
   */
  public static boolean hasAuthority(String authority) {
    return SgiSecurityContextHolder.getAuthorities().stream()
        .anyMatch(userAuthority -> userAuthority.matches("^" + authority + "$"));
  }

  /**
   * Check if the current user has an authority ignoring the UO
   * 
   * @param authority the autority to check
   * @return true if has the autority, false otherwise
   */
  public static boolean hasAuthorityForAnyUO(String authority) {
    return SgiSecurityContextHolder.getAuthorities().stream()
        .anyMatch(userAuthority -> userAuthority.matches("^" + authority + "($|_.+$)"));
  }

  /**
   * Check if the current user has an authority for an UO
   * 
   * @param authority the autority to check
   * @param uo        the UO to check
   * @return true if has the autority for the UO, false otherwise
   */
  public static boolean hasAuthorityForUO(String authority, String uo) {
    return SgiSecurityContextHolder.getAuthorities().stream()
        .anyMatch(userAuthority -> userAuthority.matches("^" + authority + "($|_" + uo + "$)"));
  }

  /**
   * Check if the current user has any authority for an UO
   * 
   * @param authorities list of authorities to check
   * @param uo          the UO to check
   * 
   * @return true if has any of the autorities for the UO, false otherwise
   */
  public static boolean hasAnyAuthorityForUO(String[] authorities, String uo) {
    List<String> userAuthorities = SgiSecurityContextHolder.getAuthorities();
    return Arrays.asList(authorities).stream().anyMatch(authority -> userAuthorities.stream()
        .anyMatch(userAuthority -> userAuthority.matches("^" + authority + "($|_" + uo + "$)")));
  }

  /**
   * Return the list of UOs that the current user has access for an authority
   * 
   * @param authority the authority to be checked
   * @return List of UOs
   */
  public static List<String> getUOsForAuthority(String authority) {
    return SgiSecurityContextHolder.getAuthorities().stream()
        .filter(userAuthority -> userAuthority.matches("^" + authority + "_.+$"))
        .map(userAuthority -> userAuthority.replaceAll("^.+_", "")).distinct().collect(Collectors.toList());
  }

  /**
   * Return the list of UOs that the current user has access for the provided list
   * of authorities
   * 
   * @param authorities list of authorities to check
   * @return List of UOs
   */
  public static List<String> getUOsForAnyAuthority(String[] authorities) {
    return SgiSecurityContextHolder.getAuthorities().stream()
        .filter(userAuthority -> Arrays.asList(authorities).stream()
            .anyMatch(authority -> userAuthority.matches("^" + authority + "_.+$")))
        .map(filtered -> filtered.replaceAll("^.+_", "")).distinct().collect(Collectors.toList());
  }
}

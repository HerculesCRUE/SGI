package org.crue.hercules.sgi.esb.keycloak;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.keycloak.TokenVerifier.Predicate;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

/**
 * HasAnyAuthorityOrScopeCheck
 */
public class HasAnyAuthorityOrScopeCheck implements Predicate<AccessToken> {

  private final List<String> requiredAuthorities;
  private final List<String> requiredScopes;

  public HasAnyAuthorityOrScopeCheck(List<String> requiredAuthorities, List<String> requiredScopes) {
    this.requiredAuthorities = requiredAuthorities;
    this.requiredScopes = requiredScopes;
  }

  @Override
  public boolean test(AccessToken accessToken) throws VerificationException {
    if (accessToken == null) {
      return false;
    }
    if (CollectionUtils.isEmpty(requiredAuthorities) && CollectionUtils.isEmpty(requiredScopes)) {
      return true;
    }

    if (CollectionUtils.isNotEmpty(requiredAuthorities)) {
      AccessToken.Access realmAccess = accessToken.getRealmAccess();
      if (realmAccess != null) {
        Set<String> tokenAuthorities = realmAccess.getRoles();
        if (tokenAuthorities != null
            && tokenAuthorities.stream().anyMatch((tokenAuthority) -> requiredAuthorities.stream()
                .anyMatch((requiredAuthority) -> requiredAuthority.matches("^" + tokenAuthority + "($|_.+$)")))) {
          return true;
        }
      }
    }

    if (CollectionUtils.isNotEmpty(requiredScopes)) {
      String scope = accessToken.getScope();
      if (scope != null) {
        String[] tokenScopes = scope.split(" ");

        if (Arrays.stream(tokenScopes).anyMatch(tokenScope -> requiredScopes.stream()
            .anyMatch(requiredScope -> requiredScope.equalsIgnoreCase(tokenScope)))) {
          return true;
        }
      }
    }
    return false;
  }
};

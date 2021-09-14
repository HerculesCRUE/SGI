package org.crue.hercules.sgi.esb.keycloak;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.keycloak.TokenVerifier.Predicate;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

/**
 * HasAnyAuthorityCheck
 */
public class HasAnyAuthorityCheck implements Predicate<AccessToken> {

  private final List<String> requiredAuthorities;

  public HasAnyAuthorityCheck(List<String> requiredAuthorities) {
    this.requiredAuthorities = requiredAuthorities;
  }

  @Override
  public boolean test(AccessToken accessToken) throws VerificationException {
    Set<String> tokenAuthorities = accessToken.getRealmAccess().getRoles();

    if (CollectionUtils.isEmpty(requiredAuthorities)
        || tokenAuthorities.stream().anyMatch((tokenAuthority) -> requiredAuthorities.stream()
            .anyMatch((requiredAuthority) -> requiredAuthority.matches("^" + tokenAuthority + "($|_.+$)")))) {
      return true;
    } else {
      throw new VerificationException("Has no authority");
    }
  }
};

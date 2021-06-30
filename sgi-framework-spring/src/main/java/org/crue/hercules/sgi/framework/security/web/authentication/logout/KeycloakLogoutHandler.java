package org.crue.hercules.sgi.framework.security.web.authentication.logout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Propagates logouts to Keycloak.
 * 
 * Necessary because Spring Security 5 (currently) doesn't support
 * end-session-endpoints.
 */
@RequiredArgsConstructor
@Slf4j
public class KeycloakLogoutHandler extends SecurityContextLogoutHandler {

  private final RestTemplate restTemplate;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    log.debug(
        "logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) - start");
    super.logout(request, response, authentication);

    propagateLogoutToKeycloak((OidcUser) authentication.getPrincipal());
    log.debug("logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) - end");
  }

  private void propagateLogoutToKeycloak(OidcUser user) {
    log.debug("propagateLogoutToKeycloak(OidcUser user) - start");
    String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";

    // @formatter:off
    UriComponentsBuilder builder = UriComponentsBuilder 
        .fromUriString(endSessionEndpoint) 
        .queryParam("id_token_hint", user.getIdToken().getTokenValue());
    // @formatter:on

    ResponseEntity<String> logoutResponse = restTemplate.getForEntity(builder.toUriString(), String.class);
    if (logoutResponse.getStatusCode().is2xxSuccessful()) {
      log.info("Successfulley logged out in Keycloak");
    } else {
      log.info("Could not propagate logout to Keycloak");
    }
    log.debug("propagateLogoutToKeycloak(OidcUser user) - end");
  }
}
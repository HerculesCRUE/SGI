package org.crue.hercules.sgi.framework.security.oauth2.client.oicd.userinfo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link OidcUserService} cutom implementation for Keycloak.
 */
@RequiredArgsConstructor
@Slf4j
public class KeycloakOidcUserService extends OidcUserService {
  private static final OAuth2Error INVALID_REQUEST = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);

  private final JwtDecoder jwtDecoder;

  private final Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    log.debug("loadUser(OidcUserRequest userRequest) - start");
    // Delegate to the default implementation for loading a user
    OidcUser oidcUser = super.loadUser(userRequest);

    OAuth2AccessToken accessToken = userRequest.getAccessToken();
    Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

    // 1) Fetch the authority information from the protected resource using
    // accessToken
    Jwt jwt = parseJwt(accessToken.getTokenValue());
    // 2) Map the authority information to one or more GrantedAuthority's and add it
    // to mappedAuthorities
    AbstractAuthenticationToken token = convert(jwt);
    if (token != null) {
      mappedAuthorities.addAll(token.getAuthorities());
    }

    // 3) Create a copy of oidcUser but use the mappedAuthorities instead
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
        .getUserNameAttributeName();
    if (StringUtils.hasText(userNameAttributeName)) {
      oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(),
          userNameAttributeName);
    } else {
      oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    log.debug("loadUser(OidcUserRequest userRequest) - end");
    return oidcUser;
  }

  private Jwt parseJwt(String accessTokenValue) {
    log.debug("parseJwt(String accessTokenValue) - start");
    try {
      // Token is already verified by spring security infrastructure
      Jwt returnValue = jwtDecoder.decode(accessTokenValue);
      log.debug("parseJwt(String accessTokenValue) - end");
      return returnValue;
    } catch (JwtException e) {
      log.error(OAuth2ErrorCodes.INVALID_REQUEST, e);
      throw new OAuth2AuthenticationException(INVALID_REQUEST, e);
    }
  }

  private AbstractAuthenticationToken convert(Jwt jwt) {
    log.debug("convert(Jwt jwt) - start");
    AbstractAuthenticationToken returnValue = jwtAuthenticationConverter.convert(jwt);
    log.debug("convert(Jwt jwt) - end");
    return returnValue;
  }
}
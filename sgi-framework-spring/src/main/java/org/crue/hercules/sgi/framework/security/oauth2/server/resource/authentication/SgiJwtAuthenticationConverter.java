package org.crue.hercules.sgi.framework.security.oauth2.server.resource.authentication;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.Assert;

/**
 * Custom {@link Converter} from {@link Jwt} to
 * {@link AbstractAuthenticationToken}.
 */
public class SgiJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
  private static final String MESSAGE_NOT_EMPTY = "{} cannot be empty";

  private String userNameClaim;
  /** The default JWT claim containing the user name ("clientId") */
  public static final String CLIENT_ID = "clientId";

  @Override
  public final AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    if (userNameClaim != null) {
      if (Boolean.TRUE.equals(jwt.containsClaim(userNameClaim))) {
        // OAuth 2 Authorization Code Flow (C2B)
        String userName = jwt.getClaimAsString(userNameClaim);
        Assert.hasText(userName, String.format(MESSAGE_NOT_EMPTY, userNameClaim));
        return new JwtAuthenticationToken(jwt, authorities, userName);
      } else if (Boolean.TRUE.equals(jwt.containsClaim(CLIENT_ID))) {
        // OAuth 2 Client Credentials Grant (B2B)
        String userName = jwt.getClaimAsString(CLIENT_ID);
        Assert.hasText(userName, String.format(MESSAGE_NOT_EMPTY, CLIENT_ID));
        return new JwtAuthenticationToken(jwt, authorities, userName);
      } else {
        throw new IllegalArgumentException(String.format(MESSAGE_NOT_EMPTY, userNameClaim));
      }
    }
    return new JwtAuthenticationToken(jwt, authorities);
  }

  /**
   * Extracts the {@link GrantedAuthority}s from scope attributes typically found
   * in a {@link Jwt}
   *
   * @param jwt The token
   * @return The collection of {@link GrantedAuthority}s found on the token
   * @see JwtGrantedAuthoritiesConverter
   * @see #setJwtGrantedAuthoritiesConverter(Converter)
   */
  protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    return this.jwtGrantedAuthoritiesConverter.convert(jwt);
  }

  /**
   * Sets the {@link Converter Converter&lt;Jwt,
   * Collection&lt;GrantedAuthority&gt;&gt;} to use. Defaults to
   * {@link JwtGrantedAuthoritiesConverter}.
   *
   * @param jwtGrantedAuthoritiesConverter The converter
   * @see JwtGrantedAuthoritiesConverter
   */
  public void setJwtGrantedAuthoritiesConverter(
      Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
    Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
    this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
  }

  /**
   * Sets the JWT claim containing the user name.
   * 
   * @param userNameClaim the JWT claim
   */
  public void setUserNameClaim(String userNameClaim) {
    this.userNameClaim = userNameClaim;
  }
}
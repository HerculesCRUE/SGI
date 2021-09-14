package org.crue.hercules.sgi.framework.security.oauth2.server.resource.authentication;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.Assert;

public class SgiJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

  private String userNameClaim;
  public static final String CLIENT_ID = "clientId";

  @Override
  public final AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    if (userNameClaim != null) {
      if (jwt.containsClaim(userNameClaim)) {
        // OAuth 2 Authorization Code Flow (C2B)
        String userName = jwt.getClaimAsString(userNameClaim);
        Assert.hasText(userName, userNameClaim + " cannot be empty");
        return new JwtAuthenticationToken(jwt, authorities, userName);
      } else if (jwt.containsClaim(CLIENT_ID)) {
        // OAuth 2 Client Credentials Grant (B2B)
        String userName = jwt.getClaimAsString(CLIENT_ID);
        Assert.hasText(userName, CLIENT_ID + " cannot be empty");
        return new JwtAuthenticationToken(jwt, authorities, userName);
      } else {
        throw new IllegalArgumentException(userNameClaim + " cannot be empty");
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
   * @deprecated Since 5.2. Use your own custom converter instead
   * @see JwtGrantedAuthoritiesConverter
   * @see #setJwtGrantedAuthoritiesConverter(Converter)
   */
  @Deprecated
  protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    return this.jwtGrantedAuthoritiesConverter.convert(jwt);
  }

  /**
   * Sets the {@link Converter Converter&lt;Jwt,
   * Collection&lt;GrantedAuthority&gt;&gt;} to use. Defaults to
   * {@link JwtGrantedAuthoritiesConverter}.
   *
   * @param jwtGrantedAuthoritiesConverter The converter
   * @since 5.2
   * @see JwtGrantedAuthoritiesConverter
   */
  public void setJwtGrantedAuthoritiesConverter(
      Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
    Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
    this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
  }

  public void setUserNameClaim(String userNameClaim) {
    this.userNameClaim = userNameClaim;
  }
}
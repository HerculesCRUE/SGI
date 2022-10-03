package org.crue.hercules.sgi.framework.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.security.oauth2.client.oicd.userinfo.KeycloakOidcUserService;
import org.crue.hercules.sgi.framework.security.oauth2.server.resource.authentication.SgiJwtAuthenticationConverter;
import org.crue.hercules.sgi.framework.security.web.authentication.logout.KeycloakLogoutHandler;
import org.crue.hercules.sgi.framework.security.web.exception.handler.WebSecurityExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * A {@link WebSecurityConfigurerAdapter} that configures {@link HttpSecurity}
 * based on configuration properties.
 */
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@Slf4j
public class SgiWebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Value("${spring.security.oauth2.enable-login:false}")
  private boolean loginEnabled;

  @Value("${spring.security.oauth2.resourceserver.jwt.user-name-claim:sub}")
  private String userNameClaim;

  @Value("${spring.security.csrf.enable:true}")
  private boolean csrfEnabled;

  @Value("${spring.security.frameoptions.enable:true}")
  private boolean frameoptionsEnabled;

  @Autowired
  private JwtDecoder jwtDecoder;

  @Autowired(required = false)
  WebSecurityExceptionHandler webSecurityExceptionHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    log.debug("configure(HttpSecurity http) - start");
    // @formatter:off
      http
        // Configure session management as a basis for a classic, server side rendered application
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
      .and()
        // Require authentication for all requests except for error, health checks and public endpoints
        .authorizeRequests()
        .antMatchers("/error", "/actuator/health/liveness", "/actuator/health/readiness").permitAll()
        .antMatchers("/public/**", "/config/time-zone").permitAll()
        .antMatchers("/**").authenticated()
      .and()
        // Validate tokens through configured OpenID Provider
        .oauth2ResourceServer()
          .jwt()
          .jwtAuthenticationConverter(jwtAuthenticationConverter())
        .and()
      .and();
      // @formatter:on

    if (csrfEnabled) {
      // @formatter:off
        http
          // CSRF protection by cookie
          .csrf()
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    } else {
      // @formatter:off
        http
          // CSRF protection disabled
          .csrf()
          .disable();
        // @formatter:on
    }

    if (!frameoptionsEnabled) {
      // @formatter:off
        http
          .headers()
          // Disable X-Frame-Options
          .frameOptions().disable();
        // @formatter:on
    }

    if (loginEnabled) {
      // @formatter:off
        http
            // This is the point where OAuth2 login of Spring 5 gets enabled
            .oauth2Login()
              .userInfoEndpoint()
              .oidcUserService(keycloakOidcUserService())
            .and()
          .and()
            // Propagate logouts via /logout to Keycloak
            .logout()
            .addLogoutHandler(keycloakLogoutHandler());
        // @formatter:on
    }

    if (webSecurityExceptionHandler != null) {
      if (loginEnabled) {
        http
            // Handle Spring Security exceptions
            .exceptionHandling().accessDeniedHandler(webSecurityExceptionHandler);
      } else {
        http
            // Handle Spring Security exceptions
            .exceptionHandling().accessDeniedHandler(webSecurityExceptionHandler)
            .authenticationEntryPoint(webSecurityExceptionHandler);
      }
    }
    log.debug("configure(HttpSecurity http) - end");
  }

  /**
   * Creates new {@link KeycloakOidcUserService}.
   * 
   * @return the {@link KeycloakOidcUserService}
   */
  protected OAuth2UserService<OidcUserRequest, OidcUser> keycloakOidcUserService() {
    log.debug("keycloakOidcUserService() - start");
    OAuth2UserService<OidcUserRequest, OidcUser> returnValue = new KeycloakOidcUserService(jwtDecoder,
        jwtAuthenticationConverter());
    log.debug("keycloakOidcUserService() - start");
    return returnValue;
  }

  /**
   * Creates a new {@link SgiJwtAuthenticationConverter}
   * 
   * @return the {@link SgiJwtAuthenticationConverter}
   */
  protected Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    log.debug("jwtAuthenticationConverter() - start");
    SgiJwtAuthenticationConverter sgiJwtAuthenticationConverter = new SgiJwtAuthenticationConverter();
    // Convert realm_access.roles claims to granted authorities, for use in access
    // decisions
    sgiJwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
    // Specify the JWT claim to be used as username
    sgiJwtAuthenticationConverter.setUserNameClaim(userNameClaim);
    log.debug("jwtAuthenticationConverter() - end");
    return sgiJwtAuthenticationConverter;
  }

  /**
   * Creates a {@link Converter} to create a {@link GrantedAuthority} collection
   * from a JSON Web Token.
   * 
   * @return the {@link Converter}
   */
  protected Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    log.debug("jwtGrantedAuthoritiesConverter() - start");
    Converter<Jwt, Collection<GrantedAuthority>> returnValue = new Converter<Jwt, Collection<GrantedAuthority>>() {
      private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

      @Override
      @SuppressWarnings("unchecked")
      public Collection<GrantedAuthority> convert(final Jwt jwt) {
        log.debug("convert(final Jwt jwt) - start");
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null) {
          Collection<GrantedAuthority> returnValue = new ArrayList<>(
              Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
          String scopes = (String) jwt.getClaims().get("scope");
          if (scopes != null) {
            List<String> scopeList = Arrays.asList(scopes.split(" "));
            returnValue.addAll(scopeList.stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                .collect(Collectors.toList()));
          }

          log.warn("No realm_acces found in token");
          log.debug("convert(final Jwt jwt) - end");
          return returnValue;
        }
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles")).stream()
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        log.debug("convert(final Jwt jwt) - end");
        return returnValue;
      }
    };
    log.debug("jwtGrantedAuthoritiesConverter() - end");
    return returnValue;
  }

  /**
   * Custom {@link KeycloakLogoutHandler} to propagete logout from application to
   * Keycloak.
   * 
   * @return the {@link KeycloakLogoutHandler}
   */
  protected KeycloakLogoutHandler keycloakLogoutHandler() {
    log.debug("keycloakLogoutHandler() - start");
    KeycloakLogoutHandler returnValue = new KeycloakLogoutHandler(new RestTemplate());
    log.debug("keycloakLogoutHandler() - end");
    return returnValue;
  }
}

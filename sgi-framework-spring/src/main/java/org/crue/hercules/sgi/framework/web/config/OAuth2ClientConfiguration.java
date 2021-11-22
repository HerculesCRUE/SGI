package org.crue.hercules.sgi.framework.web.config;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

/**
 * Provides a {@link AuthorizedClientServiceOAuth2AuthorizedClientManager} to be
 * used by the {@link HttpEntityBuilder} to provide an OAuth 2 Client
 * Authorization header in the HttpEntity.
 */
@Component
public class OAuth2ClientConfiguration {

  /**
   * Provides a {@link AuthorizedClientServiceOAuth2AuthorizedClientManager} to be
   * used by the {@link HttpEntityBuilder} to provide an OAuth 2 Client
   * Authorization header in the HttpEntity.
   * 
   * @param clientRegistrationRepository the repository of client registrations
   * @param authorizedClientService      the authorized client service
   * @return a {@link AuthorizedClientServiceOAuth2AuthorizedClientManager}
   */
  @Bean
  public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientService authorizedClientService) {

    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials().build();

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }
}

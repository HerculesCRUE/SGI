package org.crue.hercules.sgi.com.service.sgi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.Recipient;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.ApplicationContextSupportAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
@TestPropertySource(locations = { "classpath:application.yml" })
@Import({ ValidationAutoConfiguration.class, MessageSourceAutoConfiguration.class,
    ApplicationContextSupportAutoConfiguration.class })
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { SgiApiInternetAddressesService.class,
    RestApiProperties.class, AuthorizedClientServiceOAuth2AuthorizedClientManager.class })
@MockBean({ RestTemplate.class, ClientRegistrationRepository.class, OAuth2AuthorizedClientService.class })
class SgiApiInternetAddressesServiceTest {

  private final SgiApiInternetAddressesService sgiApiInternetAddressesService;
  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;
  private final ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  public SgiApiInternetAddressesServiceTest(SgiApiInternetAddressesService sgiApiEmailParamsService,
      RestApiProperties restApiProperties, RestTemplate restTemplate,
      ClientRegistrationRepository clientRegistrationRepository) {
    this.sgiApiInternetAddressesService = sgiApiEmailParamsService;
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Test
  void call_ReturnsRecipientList() {
    Recipient recipient = Recipient.builder()
        .name("test@test.tst").address("test@test.tst")
        .build();
    List<Recipient> recipients = new LinkedList<>();
    recipients.add(recipient);

    BDDMockito.given(clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.anyString()))
        .willReturn(buildMockClientRegistration());

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Recipient>>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(recipients));

    List<Recipient> output = this.sgiApiInternetAddressesService.call(ServiceType.PII, StringUtils.EMPTY,
        HttpMethod.GET);

    assertThat(output).isNotNull().hasSize(1);
  }

  private ClientRegistration buildMockClientRegistration() {
    return ClientRegistration.withRegistrationId("com-service")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationUri("http://localhost")
        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
        .clientSecret("0000")
        .jwkSetUri("http://localhost")
        .clientId("com-service")
        .redirectUriTemplate("http://localhost")
        .tokenUri("http://localhost")
        .build();
  }
}
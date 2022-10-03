package org.crue.hercules.sgi.com.service.sgi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.EmailParam;
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
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { SgiApiEmailParamsService.class,
    RestApiProperties.class, AuthorizedClientServiceOAuth2AuthorizedClientManager.class })
@MockBean({ RestTemplate.class, ClientRegistrationRepository.class, OAuth2AuthorizedClientService.class })
class SgiApiEmailParamsServiceTest {

  private static final String CSP_URL = "http://sgi-csp-service.local.computer:4281";
  private static final String ETI_URL = "http://sgi-eti-service.local.computer:4280";
  private static final String PII_URL = "http://sgi-pii-service.local.computer:4283";
  private static final String REL_URL = "http://sgi-rel-service.local.computer:4284";
  private static final String REP_URL = "http://sgi-rep-service.local.computer:4287";
  private static final String SGDOC_URL = "http://sgi.ic.corp.treelogic.com/api/sgdoc";
  private static final String TP_URL = "http://sgi-tp-service.local.computer:4286";
  private static final String USR_URL = "http://sgi-usr-service.local.computer:4282";

  private final SgiApiEmailParamsService sgiApiEmailParamsService;
  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;
  private final ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  public SgiApiEmailParamsServiceTest(SgiApiEmailParamsService sgiApiEmailParamsService,
      RestApiProperties restApiProperties, RestTemplate restTemplate,
      ClientRegistrationRepository clientRegistrationRepository) {
    this.sgiApiEmailParamsService = sgiApiEmailParamsService;
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Test
  void buildUri_WithServiceTypeCSP_ShouldReturnCspUrl() {
    String cspUrl = sgiApiEmailParamsService.buildUri(ServiceType.CSP, StringUtils.EMPTY);

    assertThat(cspUrl).isNotNull().isEqualTo(CSP_URL);
  }

  @Test
  void buildUri_WithServiceTypeETI_ShouldReturnEtiUrl() {
    String cspUrl = sgiApiEmailParamsService.buildUri(ServiceType.ETI, StringUtils.EMPTY);

    assertThat(cspUrl).isNotNull().isEqualTo(ETI_URL);
  }

  @Test
  void buildUri_WithServiceTypePII_ShouldReturnPiiUrl() {
    String piiUrl = sgiApiEmailParamsService.buildUri(ServiceType.PII, StringUtils.EMPTY);

    assertThat(piiUrl).isNotNull().isEqualTo(PII_URL);
  }

  @Test
  void buildUri_WithServiceTypeREL_ShouldReturnRelUrl() {
    String relUrl = sgiApiEmailParamsService.buildUri(ServiceType.REL, StringUtils.EMPTY);

    assertThat(relUrl).isNotNull().isEqualTo(REL_URL);
  }

  @Test
  void buildUri_WithServiceTypeREP_ShouldReturnRelUrl() {
    String repUrl = sgiApiEmailParamsService.buildUri(ServiceType.REP, StringUtils.EMPTY);

    assertThat(repUrl).isNotNull().isEqualTo(REP_URL);
  }

  @Test
  void buildUri_WithServiceTypeSGDOC_ShouldReturnSgdocUrl() {
    String sgdocUrl = sgiApiEmailParamsService.buildUri(ServiceType.SGDOC, StringUtils.EMPTY);

    assertThat(sgdocUrl).isNotNull().isEqualTo(SGDOC_URL);
  }

  @Test
  void buildUri_WithServiceTypeTP_ShouldReturnTpUrl() {
    String tpUrl = sgiApiEmailParamsService.buildUri(ServiceType.TP, StringUtils.EMPTY);

    assertThat(tpUrl).isNotNull().isEqualTo(TP_URL);
  }

  @Test
  void buildUri_WithServiceTypeUSR_ShouldReturnUsrUrl() {
    String usrUrl = sgiApiEmailParamsService.buildUri(ServiceType.USR, StringUtils.EMPTY);

    assertThat(usrUrl).isNotNull().isEqualTo(USR_URL);
  }

  @Test
  void call_WithServiceTypePII_ShouldReturnEmailParamList() {
    EmailParam param = EmailParam.builder()
        .name("TEST_PARAM").value("testing")
        .build();
    List<EmailParam> params = new LinkedList<>();
    params.add(param);

    BDDMockito.given(clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.anyString()))
        .willReturn(buildMockClientRegistration());

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<List<EmailParam>>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(params));

    List<EmailParam> output = this.sgiApiEmailParamsService.call(ServiceType.PII, StringUtils.EMPTY, HttpMethod.GET);

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
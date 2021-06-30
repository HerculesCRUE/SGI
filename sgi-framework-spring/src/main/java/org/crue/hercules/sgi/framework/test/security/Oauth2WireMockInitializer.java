package org.crue.hercules.sgi.framework.test.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Oauth2WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    log.debug("initialize(ConfigurableApplicationContext configurableApplicationContext) - start");
    try {
      // Generate an RSA key pair, which will be used for signing and verification of
      // the JWT, wrapped in a JWK
      final RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID("someId").keyUse(KeyUse.SIGNATURE).generate();
      TokenBuilder tokenBuilder = new TokenBuilder() {
        private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TokenBuilder.class);

        @Override
        public String buildToken(String username, String... roles) throws Exception {
          log.debug("buildToken(String username, String... roles) - start");
          JWSSigner signer = new RSASSASigner(rsaJWK);
          JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(username).jwtID(UUID.randomUUID().toString())
              .audience("someAudience").issuer("someIssuer")
              .expirationTime(Date.from(Instant.now().plus(Duration.ofMinutes(1L))))
              .claim("preferred_username", username)
              .claim("realm_access", Collections.singletonMap("roles", Arrays.asList(roles))).build();
          SignedJWT signedJWT = new SignedJWT(
              new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(), claimsSet);
          signedJWT.sign(signer);
          String returnValue = signedJWT.serialize();
          log.debug("buildToken(String username, String... roles) - end");
          return returnValue;
        }
      };
      configurableApplicationContext.getBeanFactory().registerSingleton("tokenBuilder", tokenBuilder);
      RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();
      String pubkey = rsaPublicJWK.toJSONObject().toString();

      WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
      wireMockServer.start();

      wireMockServer
          .stubFor(WireMock.get("/auth/realms/DEMO/protocol/openid-connect/certs").willReturn(WireMock.aResponse()
              .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("{\"keys\":[" + pubkey + "]}")));

      configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

      configurableApplicationContext.addApplicationListener(applicationEvent -> {
        if (applicationEvent instanceof ContextClosedEvent) {
          wireMockServer.stop();
        }
      });

      TestPropertyValues.of("spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:"
          + wireMockServer.port() + "/auth/realms/DEMO/protocol/openid-connect/certs")
          .applyTo(configurableApplicationContext);
      log.debug("initialize(ConfigurableApplicationContext configurableApplicationContext) - end");
    } catch (JOSEException e) {
      log.error("Error intializing WireMock", e);
      throw new RuntimeException(e);
    }
  }

  public interface TokenBuilder {
    String buildToken(String username, String... roles) throws Exception;
  }
}
package org.crue.hercules.sgi.esb.keycloak;

import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.common.util.Time;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.util.JWKSUtils;

/**
 * SgiJWKPublicKeyLocator
 */
public class SgiJWKPublicKeyLocator {

  private Log log = LogFactory.getLog(this.getClass());

  private final static int MIN_TIME_BETWEEN_JWKS_REQUESTS = 10;
  private final static int PUBLIC_KEY_CACHE_TTL = 86400;

  private static SgiJWKPublicKeyLocator sgiJWKPublicKeyLocator;

  private Map<String, PublicKey> cachedPublicKeys = new ConcurrentHashMap<>();
  private volatile long lastRequestTime = 0;

  private SgiJWKPublicKeyLocator() {
  }

  private synchronized static void createInstance() {
    if (sgiJWKPublicKeyLocator == null) {
      sgiJWKPublicKeyLocator = new SgiJWKPublicKeyLocator();
    }
  }

  public static SgiJWKPublicKeyLocator getInstance() {
    createInstance();

    return sgiJWKPublicKeyLocator;
  }

  /**
   * Get the public key of kid
   * 
   * @param kid           key id
   * @param authServerUrl auth server url
   * @param realmId       realm id
   * @return the public key
   */
  public PublicKey getPublicKey(String kid, String authServerUrl, String realmId) {
    log.info("getPublicKey(String kid, String authServerUrl, String realmId) - start");

    PublicKey publicKey = null;

    int currentTime = Time.currentTime();

    if (lastRequestTime + PUBLIC_KEY_CACHE_TTL > currentTime) {
      publicKey = cachedPublicKeys.get(kid);
    }

    if (publicKey == null) {
      synchronized (this) {
        if (currentTime > lastRequestTime + MIN_TIME_BETWEEN_JWKS_REQUESTS) {

          try {
            JSONWebKeySet jwks = new ObjectMapper()
                .readValue(new URL(getRealmJwksUrl(authServerUrl, realmId)).openStream(), JSONWebKeySet.class);
            Map<String, PublicKey> publicKeys = JWKSUtils.getKeysForUse(jwks, JWK.Use.SIG);
            log.info("getPublicKey(String kid, String authServerUrl, String realmId) - Retrieved public keys of kids: "
                + publicKeys.keySet().toString());

            publicKey = publicKeys.get(kid);

            if (publicKey == null) {
              log.error("getPublicKey(String kid, String authServerUrl, String realmId) - Public keys of kid '" + kid
                  + "' not found");
            }

            cachedPublicKeys.clear();
            cachedPublicKeys.putAll(publicKeys);
          } catch (IOException e) {
            log.error("getPublicKey(String kid, String authServerUrl, String realmId) - ", e);
          }

          lastRequestTime = currentTime;
        } else {
          log.error(
              "getPublicKey(String kid, String authServerUrl, String realmId) - Last request time to realm jwks was "
                  + lastRequestTime);
        }
      }
    }

    log.info("getPublicKey(String kid, String authServerUrl, String realmId) - end");

    return publicKey;
  }

  /**
   * Get realm jwks url
   * 
   * @param authServerUrl auth server url
   * @param realmId       realm id
   * @return the realm jwks url
   */
  private String getRealmJwksUrl(String authServerUrl, String realmId) {
    return authServerUrl + "/realms/" + realmId + "/protocol/openid-connect/certs";
  }

}

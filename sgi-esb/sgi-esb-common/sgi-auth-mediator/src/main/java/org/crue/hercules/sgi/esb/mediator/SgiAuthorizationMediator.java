package org.crue.hercules.sgi.esb.mediator;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants.Configuration;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.transport.passthru.PassThroughConstants;
import org.crue.hercules.sgi.esb.keycloak.HasAnyAuthorityOrScopeCheck;
import org.crue.hercules.sgi.esb.keycloak.SgiJWKPublicKeyLocator;
import org.crue.hercules.sgi.esb.response.ErrorResponse;
import org.keycloak.TokenVerifier;
import org.keycloak.TokenVerifier.TokenTypeCheck;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.util.TokenUtil;

public class SgiAuthorizationMediator extends AbstractMediator implements ManagedLifecycle {

  private static final String ENV_VAR_AUTH_SERVER_URL = "SGI_ESB_AUTH_SERVER_URL";
  private static final String ENV_VAR_AUTH_SERVER_REALM_ID = "SGI_ESB_AUTH_SERVER_REALM_ID";

  private String method;

  private String realmId;
  private String authServerUrl;

  @SuppressWarnings("rawtypes")
  public boolean mediate(MessageContext context) {
    log.info("mediate(MessageContext context) - start ");

    ErrorResponse errorResponse = null;

    try {
      checkAuth(context);
    } catch (VerificationException e) {
      errorResponse = new ErrorResponse(ErrorResponse.UNAUTHORIZED, e.getLocalizedMessage());
    } catch (Exception e) {
      errorResponse = new ErrorResponse(ErrorResponse.UNKNOWN, e.getLocalizedMessage());
    }

    if (errorResponse != null) {
      org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context)
          .getAxis2MessageContext();

      Map headers = (Map) axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
      headers.clear();

      axis2MessageContext.setProperty(PassThroughConstants.HTTP_SC, HttpStatus.SC_UNAUTHORIZED);
      axis2MessageContext.setProperty(PassThroughConstants.NO_ENTITY_BODY, false);
      axis2MessageContext.setProperty(Configuration.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
      axis2MessageContext.setProperty(Configuration.MESSAGE_TYPE, ContentType.APPLICATION_JSON.getMimeType());

      context.setTo(null);

      try {
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        JsonUtil.getNewJsonPayload(axis2MessageContext, jsonResponse, true, true);
      } catch (AxisFault e) {
        log.error("mediate(MessageContext context) - AxisFault:", e);
      } catch (IOException e) {
        log.error("mediate(MessageContext context) - IOException:", e);
      }

      // Stop sequence and return error response
      Axis2Sender.sendBack(context);

      log.info("mediate(MessageContext context) - unauthorized");

      return false;
    }

    log.info("mediate(MessageContext context) - end");

    return true;
  }

  public void init(SynapseEnvironment se) {
    log.info("init(SynapseEnvironment se) - start");

    // Get auth server and realm id from environment variables
    setAuthServerUrl(System.getenv(ENV_VAR_AUTH_SERVER_URL));
    setRealmId(System.getenv(ENV_VAR_AUTH_SERVER_REALM_ID));

    log.info("init(SynapseEnvironment se) - authServerUrl: " + getAuthServerUrl() + ", realmId: " + getRealmId());

    log.info("init(SynapseEnvironment se) - end");
  }

  public void destroy() {
    log.info("destroy() - start");
    log.info("destroy() - end");
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getRealmId() {
    return realmId;
  }

  public void setRealmId(String realmId) {
    this.realmId = realmId;
  }

  public String getAuthServerUrl() {
    return authServerUrl;
  }

  public void setAuthServerUrl(String authServerUrl) {
    this.authServerUrl = authServerUrl;
  }

  /**
   * Check auth
   * 
   * @param context synapse message context
   * @throws Exception throw any exception
   */
  @SuppressWarnings("unchecked")
  private void checkAuth(MessageContext context) throws Exception {
    log.info("checkAuth(MessageContext context) - start");

    org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context)
        .getAxis2MessageContext();

    // Get required authorities
    List<String> requiredAuthorities = getAsList((String) context.getProperty("requiredAuthorities"));
    log.info("checkAuth(MessageContext context) - requiredAuthorities: " + requiredAuthorities);
    // Get required scopes
    List<String> requiredScopes = getAsList((String) context.getProperty("requiredScopes"));
    log.info("checkAuth(MessageContext context) - requiredScopes: " + requiredScopes);

    // Get auhorization token
    Map<String, String> headers = (Map<String, String>) axis2MessageContext
        .getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
    String tokenString = headers.get(HttpHeaders.AUTHORIZATION) != null
        ? headers.get(HttpHeaders.AUTHORIZATION).replace("Bearer ", "")
        : "";
    TokenVerifier<AccessToken> tokenVerifier = TokenVerifier.create(tokenString, AccessToken.class);

    // Get public key
    String keyId = null;
    try {
      keyId = tokenVerifier.parse().getHeader().getKeyId();
      log.info("checkAuth(MessageContext context) - keyId: " + keyId);
    } catch (VerificationException e) {
      log.error("checkAuth(MessageContext context) - Error parsing token: " + e.toString());
      throw e;
    }

    PublicKey publicKey = SgiJWKPublicKeyLocator.getInstance().getPublicKey(keyId, getAuthServerUrl(), getRealmId());

    // Verify token
    try {
      tokenVerifier.publicKey(publicKey)
          .withChecks(// @formatter:off
            TokenVerifier.IS_ACTIVE, 
            new TokenTypeCheck(TokenUtil.TOKEN_TYPE_BEARER),
            TokenVerifier.SUBJECT_EXISTS_CHECK,
            new HasAnyAuthorityOrScopeCheck(requiredAuthorities, requiredScopes)
          )// @formatter:on
          .verify();
    } catch (VerificationException e) {
      log.error("checkAuth(MessageContext context) - Error verifying token: " + e.toString());
      throw e;
    } catch (Exception e) {
      log.error("checkAuth(MessageContext context) - Exception: ", e);
      throw e;
    }

    log.info("checkAuth(MessageContext context) - end");
  }

  /**
   * Split the value by comma
   * 
   * @param value string with comma delimited elements
   * @return the list of splitted elements
   */
  public List<String> getAsList(String value) {
    return StringUtils.isEmpty(value) ? new ArrayList<>() : Arrays.asList(value.split(","));
  }

}

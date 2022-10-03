package org.crue.hercules.sgi.auth.broker.saml.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.models.GroupModel;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.ScriptModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.scripting.EvaluatableScriptAdapter;
import org.keycloak.scripting.ScriptingProvider;

public class ScriptToGroupMapper extends AbstractIdentityProviderMapper {

  public static final String PROVIDER_ID = "saml-script-group-idp-mapper";
  public static final String SCRIPT = "script";
  public static final String CLEAR_BEFORE_RUN = "clear.before.run";
  private static final Logger LOGGER = Logger.getLogger(ScriptToGroupMapper.class);

  private static final Set<IdentityProviderSyncMode> IDENTITY_PROVIDER_SYNC_MODES = new HashSet<>(
      Arrays.asList(IdentityProviderSyncMode.values()));

  public static final String[] COMPATIBLE_PROVIDERS = {
      SAMLIdentityProviderFactory.PROVIDER_ID
  };

  private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

  static {
    ProviderConfigProperty isClearBefore = new ProviderConfigProperty();
    isClearBefore.setName(CLEAR_BEFORE_RUN);
    isClearBefore.setLabel("Clear groups before resolve");
    isClearBefore.setHelpText("If enabled the user will leave all existing groups.");
    isClearBefore.setType(ProviderConfigProperty.BOOLEAN_TYPE);
    configProperties.add(isClearBefore);

    ProviderConfigProperty attributeMappingProperty = new ProviderConfigProperty();
    attributeMappingProperty.setName(SCRIPT);
    attributeMappingProperty.setLabel("Script");
    attributeMappingProperty.setHelpText(
        "Script to compute the groups. \n" + //
            " Available variables: \n" + //
            " 'attributes' - Map<String,List<Object>> with SAML attributes.\n" + //
            "To use: the last statement is the value returned to Java.\n"//
    );
    attributeMappingProperty.setDefaultValue("/**\n" + //
        " * Available variables: \n" + //
        " * attributes - Map<String,List<Object>> with SAML attributes.\n" + //
        " */\n\n\n//insert your code here..." //
    );
    attributeMappingProperty.setType(ProviderConfigProperty.SCRIPT_TYPE);
    configProperties.add(attributeMappingProperty);
  }

  @Override
  public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
    return IDENTITY_PROVIDER_SYNC_MODES.contains(syncMode);
  }

  @Override
  public List<org.keycloak.provider.ProviderConfigProperty> getConfigProperties() {
    return configProperties;
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public String[] getCompatibleProviders() {
    return COMPATIBLE_PROVIDERS;
  }

  @Override
  public String getDisplayCategory() {
    return "Group Importer";
  }

  @Override
  public String getDisplayType() {
    return "Script to Group";
  }

  @Override
  public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {

    List<GroupModel> groups = resolve(session, realm, mapperModel, context);
    for (GroupModel group : groups) {
      user.joinGroup(group);
    }
  }

  @Override
  public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    boolean clearBeforeRun = Boolean
        .parseBoolean(mapperModel.getConfig().get(CLEAR_BEFORE_RUN));
    List<GroupModel> groups = resolve(session, realm, mapperModel, context);
    if (clearBeforeRun) {
      for (GroupModel group : user.getGroups()) {
        user.leaveGroup(group);
      }
    }
    for (GroupModel group : groups) {
      user.joinGroup(group);
    }
  }

  @Override
  public String getHelpText() {
    return "If the set of attributes exists and can be matched, assign the user the specified realm or application role.";
  }

  /**
   * Obtains the {@link GroupModel} corresponding the group configured in the
   * specified
   * {@link IdentityProviderMapperModel}.
   * If the group doesn't exist, this method throws an
   * {@link IdentityBrokerException}.
   *
   * @param realm       a reference to the realm.
   * @param mapperModel a reference to the {@link IdentityProviderMapperModel}
   *                    containing the configured group.
   * @return the {@link GroupModel}
   * @throws IdentityBrokerException if the group doesn't exist.
   */
  private GroupModel getGroup(final RealmModel realm, final String groupToSearch) {
    GroupModel group = KeycloakModelUtils.findGroupByPath(realm, groupToSearch);

    if (group == null) {
      LOGGER.error("Unable to find group: " + groupToSearch);
    }
    return group;
  }

  List<GroupModel> resolve(KeycloakSession session, RealmModel realm, IdentityProviderMapperModel mapperModel,
      BrokeredIdentityContext context) {
    String scriptSource = mapperModel.getConfig().get(SCRIPT);

    AssertionType assertion = (AssertionType) context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);
    Set<AttributeStatementType> attributeAssertions = assertion.getAttributeStatements();
    if (attributeAssertions == null) {
      return new ArrayList<>();
    }

    ScriptingProvider scripting = session.getProvider(ScriptingProvider.class);
    ScriptModel scriptModel = scripting.createScript(realm.getId(), ScriptModel.TEXT_JAVASCRIPT,
        "saml-script-to-group_" + mapperModel.getName(), scriptSource, null);

    Map<String, List<Object>> attributes = attributeAssertions.stream()
        .flatMap(statements -> statements.getAttributes().stream())
        .collect(
            Collectors.toMap(attr -> attr.getAttribute().getName(), attr -> attr.getAttribute().getAttributeValue()));

    EvaluatableScriptAdapter script = scripting.prepareEvaluatableScript(scriptModel);
    Object attributeValue;
    List<GroupModel> groups = new ArrayList<>();
    try {
      attributeValue = script.eval((bindings) -> {
        bindings.put("attributes", attributes);
      });
      // If the result is a an array or is iterable, get all values
      if (attributeValue.getClass().isArray()) {
        attributeValue = Arrays.asList((Object[]) attributeValue);
      }
      if (attributeValue instanceof Iterable) {
        for (Object value : (Iterable) attributeValue) {
          GroupModel group = getGroup(realm, value.toString());
          if (group != null) {
            groups.add(group);
          }
        }

      } else {
        // single value case
        GroupModel group = getGroup(realm, attributeValue.toString());
        if (group != null) {
          groups.add(group);
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Error during execution of Mapper script", ex);
    }

    return groups;
  }
}
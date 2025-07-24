package org.crue.hercules.sgi.framework.i18n;

import java.util.Arrays;
import java.util.List;

/**
 * Default provider. Values are provided from {@link Language} enum
 */
public class DefaultI18nConfigProvider implements I18nConfigProvider {

  @Override
  public void refresh() {
    // Do nothing
  }

  @Override
  public List<Language> getEnabledLanguages() {
    return Arrays.asList(Language.values());
  }

  @Override
  public List<Language> getLanguagePriorities() {
    return Arrays.asList(Language.values());
  }

}

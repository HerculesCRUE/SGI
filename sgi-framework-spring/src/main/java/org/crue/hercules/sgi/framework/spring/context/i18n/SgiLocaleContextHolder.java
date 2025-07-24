package org.crue.hercules.sgi.framework.spring.context.i18n;

import java.util.Locale;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.context.i18n.LocaleContextHolder;

public class SgiLocaleContextHolder {

  private SgiLocaleContextHolder() {
  }

  public static Locale getLocale() {
    return LocaleContextHolder.getLocale();
  }

  public static Language getLanguage() {
    Locale locale = getLocale();
    Language language = Language.fromCode(locale.getLanguage());
    if (language == null) {
      return Language.ES;
    }
    return language;
  }

}

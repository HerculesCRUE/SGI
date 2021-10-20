package org.crue.hercules.sgi.eti.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class I18nUtil {

  private static ResourceBundleMessageSource messageSource;

  @Autowired
  I18nUtil(ResourceBundleMessageSource messageSource) {
    I18nUtil.messageSource = messageSource;
  }

  public static String toLocale(String msgCode) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(msgCode, null, locale);
  }
}
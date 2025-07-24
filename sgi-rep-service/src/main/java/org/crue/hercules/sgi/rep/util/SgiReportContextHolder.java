package org.crue.hercules.sgi.rep.util;

import java.util.TimeZone;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

public class SgiReportContextHolder {
  private static final ThreadLocal<ReportContext> reportContextHolder = new NamedThreadLocal<>("ReportContext");

  private SgiReportContextHolder() {
  }

  private static void resetReportContext() {
    reportContextHolder.remove();
  }

  private static void setReportContext(@Nullable ReportContext reportContext) {
    if (reportContext == null) {
      resetReportContext();
    } else {
      reportContextHolder.set(reportContext);
    }
  }

  @Nullable
  private static ReportContext getReportContext() {
    return reportContextHolder.get();
  }

  public static void setLanguage(@Nullable Language language) {
    ReportContext reportContext;

    if (language != null) {
      reportContext = new DefaultReportContext(language);
    } else {
      reportContext = null;
    }
    setReportContext(reportContext);
  }

  public static Language getLanguage() {
    return getLanguage(getReportContext());
  }

  private static Language getLanguage(@Nullable ReportContext reportContext) {
    if (reportContext != null) {
      Language language = reportContext.getLanguage();
      if (language != null) {
        return language;
      }
    }
    return SgiLocaleContextHolder.getLanguage();
  }

  public static TimeZone getTimeZone() {
    return SgiConfigProperties.get().getTimeZone();
  }

}

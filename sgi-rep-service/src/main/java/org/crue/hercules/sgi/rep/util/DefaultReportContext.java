package org.crue.hercules.sgi.rep.util;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.lang.Nullable;

public class DefaultReportContext implements ReportContext {

  @Nullable
  private final Language language;

  public DefaultReportContext(@Nullable Language language) {
    this.language = language;
  }

  @Override
  @Nullable
  public Language getLanguage() {
    return this.language;
  }

  @Override
  public String toString() {
    return (this.language != null ? this.language.toString() : "-");
  }
}

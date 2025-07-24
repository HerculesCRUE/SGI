package org.crue.hercules.sgi.rep.util;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.lang.Nullable;

public interface ReportContext {

  @Nullable
  Language getLanguage();
}

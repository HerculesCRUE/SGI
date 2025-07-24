package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.Getter;

/**
 * Instancia que contiene un report M10, M20 o M30
 */
@Getter
public class ReportMXX {
  private Language lang;

  public ReportMXX(Language lang) {
    this.lang = lang;
  }
}
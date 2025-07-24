package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.report.SgiReportHelper;

public enum Dictamen {
  FAVORABLE(1L, "enum.dictamen.FAVORABLE"),
  FAVORABLE_PDTE_REV_MINIMA(2L, "enum.dictamen.FAVORABLE_PDTE_REV_MINIMA"),
  PDTE_CORRECCIONES(3L, "enum.dictamen.PDTE_CORRECCIONES"),
  NO_PROCEDE_EVALUAR(4L, "enum.dictamen.NO_PROCEDE_EVALUAR"),
  FAVORABLE_SEG_ANUAL(5L, "enum.dictamen.FAVORABLE_SEG_ANUAL"),
  SOLICITUD_MODIFICACIONES_SEG_ANUAL(6L, "enum.dictamen.SOLICITUD_MODIFICACIONES_SEG_ANUAL"),
  FAVORABLE_SEG_FINAL(7L, "enum.dictamen.FAVORABLE_SEG_FINAL"),
  SOLICITUD_ACLARACIONES_SEG_FINAL(8L, "enum.dictamen.SOLICITUD_ACLARACIONES_SEG_FINAL"),
  FAVORABLE_RETROSPECTIVA(9L, "enum.dictamen.FAVORABLE_RETROSPECTIVA"),
  DESFAVORABLE_RETROSPECTIVA(10L, "enum.dictamen.DESFAVORABLE_RETROSPECTIVA"),
  DESFAVORABLE(11L, "enum.dictamen.DESFAVORABLE");

  private final Long id;
  private final String i18nMessage;

  private Dictamen(Long id, String i18nMessage) {
    this.id = id;
    this.i18nMessage = i18nMessage;
  }

  public Long getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return SgiReportHelper.getMessage(this.i18nMessage);
  }

  public static Dictamen fromCode(final Long id) {
    for (Dictamen tipo : Dictamen.values()) {
      if (tipo.getId().equals(id)) {
        return tipo;
      }
    }
    return null;
  }
}

package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.report.SgiReportHelper;

public enum TipoEvaluacion {
  /** Retrospectiva <code>1L</code> */
  RETROSPECTIVA(1L, "enum.tipo-evaluacion.RETROSPECTIVA"),
  /** Memoria <code>2L</code> */
  MEMORIA(2L, "enum.tipo-evaluacion.MEMORIA"),
  /** Seguimiento anual <code>3L</code> */
  SEGUIMIENTO_ANUAL(3L, "enum.tipo-evaluacion.SEGUIMIENTO_ANUAL"),
  /** Seguimiento final <code>4L</code> */
  SEGUIMIENTO_FINAL(4L, "enum.tipo-evaluacion.SEGUIMIENTO_FINAL");

  private final Long id;
  private final String i18nMessage;

  private TipoEvaluacion(Long id, String i18nMessage) {
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

  public static TipoEvaluacion fromCode(final Long id) {
    for (TipoEvaluacion tipo : TipoEvaluacion.values()) {
      if (tipo.getId().equals(id)) {
        return tipo;
      }
    }
    return null;
  }
}

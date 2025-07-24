package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.report.SgiReportHelper;

public enum EstadoRetrospectiva {
  /** Pendiente <code>1L</code> */
  PENDIENTE(1L, "enum.retrospectiva.estado.PENDIENTE"),
  /** Completada <code>2L</code> */
  COMPLETADA(2L, "enum.retrospectiva.estado.COMPLETADA"),
  /** En secretaria <code>3L</code> */
  EN_SECRETARIA(3L, "enum.retrospectiva.estado.EN_SECRETARIA"),
  /** En evaluacion <code>4L</code> */
  EN_EVALUACION(4L, "enum.retrospectiva.estado.EN_EVALUACION"),
  /** Fin evaluacion <code>5L</code> */
  FIN_EVALUACION(5L, "enum.retrospectiva.estado.FIN_EVALUACION");

  private final Long id;
  private final String i18nMessage;

  private EstadoRetrospectiva(Long id, String i18nMessage) {
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

  public static EstadoRetrospectiva fromCode(final Long id) {
    for (EstadoRetrospectiva tipo : EstadoRetrospectiva.values()) {
      if (tipo.getId().equals(id)) {
        return tipo;
      }
    }
    return null;
  }
}

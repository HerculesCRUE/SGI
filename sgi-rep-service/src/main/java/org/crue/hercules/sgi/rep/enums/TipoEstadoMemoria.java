package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.report.SgiReportHelper;

public enum TipoEstadoMemoria {
  /** En elaboración <code>1L</code> */
  EN_ELABORACION(1L, "enum.tipo-estado-memoria.EN_ELABORACION"),
  /** Completada <code>2L</code> */
  COMPLETADA(2L, "enum.tipo-estado-memoria.COMPLETADA"),
  /** En secretaría <code>3L</code> */
  EN_SECRETARIA(3L, "enum.tipo-estado-memoria.EN_SECRETARIA"),
  /** En secretaría revisión mínima <code>4L</code> */
  EN_SECRETARIA_REVISION_MINIMA(4L, "enum.tipo-estado-memoria.EN_SECRETARIA_REVISION_MINIMA"),
  /** En evaluación <code>5L</code> */
  EN_EVALUACION(5L, "enum.tipo-estado-memoria.EN_EVALUACION"),
  /** En secretaría revisión mínima <code>6L</code> */
  FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS(6L,
      "enum.tipo-estado-memoria.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS"),
  /** Pendiente correcciones <code>7L</code> */
  PENDIENTE_CORRECCIONES(7L, "enum.tipo-estado-memoria.PENDIENTE_CORRECCIONES"),
  /** No procede evaluar <code>8L</code> */
  NO_PROCEDE_EVALUAR(8L, "enum.tipo-estado-memoria.NO_PROCEDE_EVALUAR"),
  /** Fin evaluación <code>9L</code> */
  FIN_EVALUACION(9L, "enum.tipo-estado-memoria.FIN_EVALUACION"),
  /** Archivada <code>10L</code> */
  ARCHIVADA(10L, "enum.tipo-estado-memoria.ARCHIVADA"),
  /** Completada seguimiento anual <code>11L</code> */
  COMPLETADA_SEGUIMIENTO_ANUAL(11L, "enum.tipo-estado-memoria.COMPLETADA_SEGUIMIENTO_ANUAL"),
  /** En secretaría seguimiento anual <code>12L</code> */
  EN_SECRETARIA_SEGUIMIENTO_ANUAL(12L, "enum.tipo-estado-memoria.EN_SECRETARIA_SEGUIMIENTO_ANUAL"),
  /** En evaluación seguimiento anual <code>13L</code> */
  EN_EVALUACION_SEGUIMIENTO_ANUAL(13L, "enum.tipo-estado-memoria.EN_EVALUACION_SEGUIMIENTO_ANUAL"),
  /** Fin evaluación seguimiento anual <code>14L</code> */
  FIN_EVALUACION_SEGUIMIENTO_ANUAL(14L, "enum.tipo-estado-memoria.FIN_EVALUACION_SEGUIMIENTO_ANUAL"),
  /** Solicitud modificación <code>15L</code> */
  SOLICITUD_MODIFICACION(15L, "enum.tipo-estado-memoria.SOLICITUD_MODIFICACION"),
  /** Completada seguimiento final <code>16L</code> */
  COMPLETADA_SEGUIMIENTO_FINAL(16L, "enum.tipo-estado-memoria.COMPLETADA_SEGUIMIENTO_FINAL"),
  /** En secretaría seguimiento final <code>17L</code> */
  EN_SECRETARIA_SEGUIMIENTO_FINAL(17L, "enum.tipo-estado-memoria.EN_SECRETARIA_SEGUIMIENTO_FINAL"),
  /** En secretaría seguimiento final aclaraciones <code>18L</code> */
  EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES(18L,
      "enum.tipo-estado-memoria.EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES"),
  /** En evaluación seguimiento final <code>19L</code> */
  EN_EVALUACION_SEGUIMIENTO_FINAL(19L, "enum.tipo-estado-memoria.EN_EVALUACION_SEGUIMIENTO_FINAL"),
  /** Fin evaluación seguimiento final <code>20L</code> */
  FIN_EVALUACION_SEGUIMIENTO_FINAL(20L, "enum.tipo-estado-memoria.FIN_EVALUACION_SEGUIMIENTO_FINAL"),
  /** En aclaración seguimiento final <code>21L</code> */
  EN_ACLARACION_SEGUIMIENTO_FINAL(21L, "enum.tipo-estado-memoria.EN_ACLARACION_SEGUIMIENTO_FINAL"),
  /** En subsanación <code>22L</code> */
  SUBSANACION(22L, "enum.tipo-estado-memoria.SUBSANACION"),
  /** Desfavorable <code>23L</code> */
  DESFAVORABLE(23L, "enum.tipo-estado-memoria.DESFAVORABLE"),
  /** Solicitud modificación seguimiento anual <code>24L</code> */
  SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL(24L, "enum.tipo-estado-memoria.SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL"),
  /** En secretaría seguimiento anual modificación <code>25L</code> */
  EN_SECRETARIA_SEGUIMIENTO_ANUAL_MODIFICACION(25L,
      "enum.tipo-estado-memoria.EN_SECRETARIA_SEGUIMIENTO_ANUAL_MODIFICACION"),
  /** En evaluación revision mínima <code>26L</code> */
  EN_EVALUACION_REVISION_MINIMA(26L, "enum.tipo-estado-memoria.EN_EVALUACION_REVISION_MINIMA");

  private final Long id;
  private final String i18nMessage;

  private TipoEstadoMemoria(Long id, String i18nMessage) {
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

  public static TipoEstadoMemoria fromCode(final Long id) {
    for (TipoEstadoMemoria tipo : TipoEstadoMemoria.values()) {
      if (tipo.getId().equals(id)) {
        return tipo;
      }
    }
    return null;
  }
}

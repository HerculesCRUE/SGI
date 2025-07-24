package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.report.SgiReportHelper;

public enum TipoActividad implements GenderType {
  /** Proyecto de investigación */
  PROYECTO_INVESTIGACION(1L, Genero.M, "enum.tipo-actividad.PROYECTO_INVESTIGACION"),
  /** Práctica docente */
  PRACTICA_DOCENTE(2L, Genero.F, "enum.tipo-actividad.PRACTICA_DOCENTE"),
  /** Investigación tutelada */
  INVESTIGACION_TUTELADA(3L, Genero.F, "enum.tipo-actividad.INVESTIGACION_TUTELADA");

  private final Long id;
  private final Genero genero;
  private final String i18nMessage;

  private TipoActividad(Long id, Genero genero, String i18nMessage) {
    this.id = id;
    this.genero = genero;
    this.i18nMessage = i18nMessage;
  }

  public Long getId() {
    return this.id;
  }

  public Genero getGenero() {
    return this.genero;
  }

  @Override
  public String toString() {
    return SgiReportHelper.getMessage(this.i18nMessage);
  }

  public static TipoActividad fromCode(final Long id) {
    for (TipoActividad tipo : TipoActividad.values()) {
      if (tipo.getId().equals(id)) {
        return tipo;
      }
    }
    return null;
  }
}

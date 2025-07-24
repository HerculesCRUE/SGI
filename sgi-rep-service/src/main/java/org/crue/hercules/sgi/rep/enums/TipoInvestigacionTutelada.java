package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.report.SgiReportHelper;

public enum TipoInvestigacionTutelada implements GenderType {
  /** Tesis doctoral */
  TESIS_DOCTORAL(1L, Genero.F, "enum.tipo-investigacion-tutelada.TESIS_DOCTORAL"),
  /** Trabajo fin de máster */
  TRABAJO_FIN_MASTER(2L, Genero.M, "enum.tipo-investigacion-tutelada.TRABAJO_FIN_MASTER"),
  /** Trabajo fin de grado */
  TRABAJO_FIN_GRADO(3L, Genero.M, "enum.tipo-investigacion-tutelada.TRABAJO_FIN_GRADO");

  private final Long id;
  private final Genero genero;
  private final String i18nMessage;

  private TipoInvestigacionTutelada(Long id, Genero genero, String i18nMessage) {
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

  public static TipoInvestigacionTutelada fromCode(final Long id) {
    for (TipoInvestigacionTutelada tipo : TipoInvestigacionTutelada.values()) {
      if (tipo.getId().equals(id)) {
        return tipo;
      }
    }
    return null;
  }
}

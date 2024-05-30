package org.crue.hercules.sgi.rep.dto.prc;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report resumen puntuación grupo
 */
public class ReportInformeResumenPuntuacionGrupos extends SgiReportDto {
  public ReportInformeResumenPuntuacionGrupos() {
    this.setPath("rep-prc-resumen-puntuacion-grupos-docx");
    this.setName("informeResumenPuntuacionGrupos");
  }
}
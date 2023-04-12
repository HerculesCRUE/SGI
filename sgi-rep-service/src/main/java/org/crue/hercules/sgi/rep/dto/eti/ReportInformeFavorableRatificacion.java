package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe favorable ratificación
 */
public class ReportInformeFavorableRatificacion extends SgiReportDto {
  public ReportInformeFavorableRatificacion() {
    this.setPath("rep-eti-evaluacion-favorable-memoria-ratificacion-prpt");
    this.setName("informeFavorableRatificacion");
  }
}
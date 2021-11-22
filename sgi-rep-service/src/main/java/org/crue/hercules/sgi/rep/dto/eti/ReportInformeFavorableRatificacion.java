package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe favorable ratificación
 */
public class ReportInformeFavorableRatificacion extends SgiReportDto {
  public ReportInformeFavorableRatificacion() {
    this.setPath("report/eti/informeFavorableRatificacion.prpt");
    this.setName("informeFavorableRatificacion");
  }
}